package com.toukenranbu.kaisou.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toukenranbu.kaisou.entity.Battlefield;
import com.toukenranbu.kaisou.entity.Recollection;
import com.toukenranbu.kaisou.entity.Sword;
import com.toukenranbu.kaisou.repository.BattlefieldRepository;
import com.toukenranbu.kaisou.repository.RecollectionRepository;
import com.toukenranbu.kaisou.repository.SwordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * アプリケーション起動時に resources/data 配下のJSONファイルを読み込み、
 * DBが空の場合のみ初期データとして投入するクラス。
 *
 * データを追加・変更したい場合は以下のファイルを編集するだけでよい。
 *   - data/swords.json         … 刀剣男士マスタ(name, reading, type)
 *   - data/battlefields.json   … 合戦場マスタ(id, name, era, mapNumber)
 *   - data/recollections.json  … 回想データ(battlefield は battlefields.json の id を参照する)
 *
 * 並び替え用の sortOrder は JSON には持たせず、mapNumber(例: "3-4")から
 * このクラスが起動時に自動計算して設定する。mapNumber が空・不正な形式の場合は末尾に回す。
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final BattlefieldRepository battlefieldRepository;
    private final SwordRepository swordRepository;
    private final RecollectionRepository recollectionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DataInitializer(BattlefieldRepository battlefieldRepository,
                            SwordRepository swordRepository,
                            RecollectionRepository recollectionRepository) {
        this.battlefieldRepository = battlefieldRepository;
        this.swordRepository = swordRepository;
        this.recollectionRepository = recollectionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (recollectionRepository.count() > 0) {
            updateSwordMetadata();
            log.info("回想データが既に存在するため、初期データ投入をスキップします。");
            return;
        }

        log.info("初期データを投入します。");
        Map<String, Battlefield> battlefieldMap = loadBattlefields();
        Map<String, Sword> swordMap = loadSwords();
        loadRecollections(battlefieldMap, swordMap);
        log.info("初期データの投入が完了しました。(合戦場:{}件, 刀剣男士:{}件, 回想:{}件)",
                battlefieldMap.size(), swordMap.size(), recollectionRepository.count());
    }

    /**
     * mapNumber が見つからない・形式が不正な場合に使う、末尾扱いのための基準値。
     * 実データの章番号(1桁台)より確実に大きくなるよう、余裕を持った値にしている。
     */
    private static final int FALLBACK_CHAPTER = 900;

    /** 戻り値のMapのキーは Battlefield.code (JSONの "id")。 */
    private Map<String, Battlefield> loadBattlefields() throws Exception {
        Map<String, Battlefield> map = new HashMap<>();
        try (InputStream is = new ClassPathResource("data/battlefields.json").getInputStream()) {
            List<BattlefieldSeed> seeds = objectMapper.readValue(is,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, BattlefieldSeed.class));
            for (BattlefieldSeed seed : seeds) {
                Battlefield battlefield = new Battlefield(seed.id, seed.name, seed.era, seed.mapNumber);
                battlefield.setSortOrder(calculateSortOrder(seed.mapNumber));
                battlefieldRepository.save(battlefield);
                map.put(seed.id, battlefield);
            }
        }
        return map;
    }

    /**
     * "3-4" のようなゲーム内番号(章-マップ)から並び替え用の数値を計算する。
     * 章 × 1000 + マップ番号 × 10 とすることで、将来章内にマップを挿入したくなった場合に
     * 既存データを書き換えずに間の値を割り振れるようにしている。
     * mapNumber が空・想定外の形式の場合は一覧の末尾に回す(FALLBACK_CHAPTER を使用)。
     */
    private int calculateSortOrder(String mapNumber) {
        if (mapNumber == null || mapNumber.isBlank()) {
            return FALLBACK_CHAPTER * 1000;
        }
        String[] parts = mapNumber.split("-");
        if (parts.length != 2) {
            log.warn("mapNumberの形式が不正なため末尾扱いにします: {}", mapNumber);
            return FALLBACK_CHAPTER * 1000;
        }
        try {
            int chapter = Integer.parseInt(parts[0].trim());
            int sub = Integer.parseInt(parts[1].trim());
            return chapter * 1000 + sub * 10;
        } catch (NumberFormatException e) {
            log.warn("mapNumberの形式が不正なため末尾扱いにします: {}", mapNumber);
            return FALLBACK_CHAPTER * 1000;
        }
    }

    private Map<String, Sword> loadSwords() throws Exception {
        Map<String, Sword> map = new HashMap<>();
        try (InputStream is = new ClassPathResource("data/swords.json").getInputStream()) {
            List<SwordSeed> seeds = objectMapper.readValue(is,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, SwordSeed.class));
            for (SwordSeed seed : seeds) {
                Sword sword = swordRepository.save(new Sword(seed.name, seed.reading, seed.type, seed.school));
                map.put(seed.name, sword);
            }
        }
        return map;
    }

    private void updateSwordMetadata() throws Exception {
        try (InputStream is = new ClassPathResource("data/swords.json").getInputStream()) {
            List<SwordSeed> seeds = objectMapper.readValue(is,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, SwordSeed.class));
            for (SwordSeed seed : seeds) {
                swordRepository.findByName(seed.name).ifPresent(sword -> {
                    sword.setReading(seed.reading);
                    sword.setType(seed.type);
                    sword.setSchool(seed.school);
                    swordRepository.save(sword);
                });
            }
        }
    }

    private void loadRecollections(Map<String, Battlefield> battlefieldMap, Map<String, Sword> swordMap) throws Exception {
        Map<Integer, Recollection> byNumber = new HashMap<>();

        try (InputStream is = new ClassPathResource("data/recollections.json").getInputStream()) {
            List<RecollectionSeed> seeds = objectMapper.readValue(is,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, RecollectionSeed.class));

            // 1周目: 前提回想を除いて登録(自己参照があるため2段階で処理する)
            for (RecollectionSeed seed : seeds) {
                Recollection r = new Recollection();
                r.setNumber(seed.number);
                r.setTitle(seed.title);

                Battlefield battlefield = battlefieldMap.get(seed.battlefield);
                if (battlefield == null) {
                    log.warn("battlefields.json に存在しない合戦場idが回想{}で参照されています: {}", seed.number, seed.battlefield);
                }
                r.setBattlefield(battlefield);

                r.setCondition(seed.condition);
                r.setBossRequired(seed.bossRequired != null ? seed.bossRequired : Boolean.FALSE);
                r.setRemarks(seed.remarks);

                if (seed.swords != null) {
                    for (String swordName : seed.swords) {
                        Sword sword = swordMap.get(swordName);
                        if (sword != null) {
                            r.getSwords().add(sword);
                        } else {
                            log.warn("swords.json に存在しない刀剣男士が回想{}で参照されています: {}", seed.number, swordName);
                        }
                    }
                }

                recollectionRepository.save(r);
                byNumber.put(seed.number, r);
            }

            // 2周目: 前提回想のリンクを設定
            for (RecollectionSeed seed : seeds) {
                if (seed.prerequisiteNumber != null) {
                    Recollection r = byNumber.get(seed.number);
                    Recollection prerequisite = byNumber.get(seed.prerequisiteNumber);
                    if (r != null && prerequisite != null) {
                        r.setPrerequisite(prerequisite);
                        recollectionRepository.save(r);
                    }
                }
            }
        }
    }

    // ---- JSONマッピング用の内部クラス ----

    static class BattlefieldSeed {
        public String id;
        public String name;
        public String era;
        public String mapNumber;
    }

    static class SwordSeed {
        public String name;
        public String reading;
        public String type;
        public String school;
    }

    static class RecollectionSeed {
        public Integer number;
        public String title;
        /** battlefields.json の id(合戦場の一意識別子)を参照する。 */
        public String battlefield;
        public String condition;
        public Boolean bossRequired;
        public List<String> swords;
        public Integer prerequisiteNumber;
        public String remarks;
    }
}
