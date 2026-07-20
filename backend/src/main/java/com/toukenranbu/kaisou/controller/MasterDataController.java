package com.toukenranbu.kaisou.controller;

import com.toukenranbu.kaisou.dto.BattlefieldOptionDTO;
import com.toukenranbu.kaisou.dto.SwordOptionDTO;
import com.toukenranbu.kaisou.entity.Sword;
import com.toukenranbu.kaisou.repository.BattlefieldRepository;
import com.toukenranbu.kaisou.repository.SwordRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 検索プルダウンの選択肢(刀剣男士一覧・合戦場一覧)を返すAPI。
 *
 * GET /api/swords       … 読み仮名(五十音)順。読み仮名未登録のものは末尾にまとめる。
 * GET /api/battlefields … ゲーム内の並び順(sortOrder昇順)。
 */
@RestController
@RequestMapping("/api")
public class MasterDataController {

    private final SwordRepository swordRepository;
    private final BattlefieldRepository battlefieldRepository;

    public MasterDataController(SwordRepository swordRepository, BattlefieldRepository battlefieldRepository) {
        this.swordRepository = swordRepository;
        this.battlefieldRepository = battlefieldRepository;
    }

    @GetMapping("/swords")
    public List<SwordOptionDTO> swords() {
        Comparator<Sword> byReadingThenName = Comparator
                // 読み仮名が未登録(null・空文字)のものは末尾にまとめる
                .comparing((Sword s) -> isBlank(s.getReading()) ? 1 : 0)
                .thenComparing(s -> isBlank(s.getReading()) ? "" : s.getReading())
                .thenComparing(Sword::getName);

        return swordRepository.findAll().stream()
                .sorted(byReadingThenName)
                .map(s -> new SwordOptionDTO(s.getName(), s.getSchool(), s.getType()))
                .collect(Collectors.toList());
    }

    @GetMapping("/battlefields")
    public List<BattlefieldOptionDTO> battlefields() {
        return battlefieldRepository.findAllByOrderBySortOrderAsc().stream()
                .map(b -> new BattlefieldOptionDTO(b.getCode(), b.getName(), b.getEra(), b.getMapNumber()))
                .collect(Collectors.toList());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
