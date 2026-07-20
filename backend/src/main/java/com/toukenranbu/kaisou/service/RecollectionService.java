package com.toukenranbu.kaisou.service;

import com.toukenranbu.kaisou.dto.RecollectionDetailDTO;
import com.toukenranbu.kaisou.dto.RecollectionListDTO;
import com.toukenranbu.kaisou.entity.Recollection;
import com.toukenranbu.kaisou.entity.Sword;
import com.toukenranbu.kaisou.repository.RecollectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 回想の検索・取得に関するビジネスロジックを担当する。
 */
@Service
public class RecollectionService {

    private final RecollectionRepository recollectionRepository;

    public RecollectionService(RecollectionRepository recollectionRepository) {
        this.recollectionRepository = recollectionRepository;
    }

    /**
     * 刀剣男士名・合戦場コード(Battlefield.code)で回想を検索する。
     * どちらも未指定の場合は全件、両方指定の場合はAND検索となる。
     */
    public List<RecollectionListDTO> search(String swordName, String battlefieldCode) {
        List<Recollection> results = recollectionRepository.search(
                normalize(swordName), normalize(battlefieldCode));
        return results.stream()
                .map(this::toListDTO)
                .collect(Collectors.toList());
    }

    public RecollectionDetailDTO getDetail(Long id) {
        Recollection recollection = recollectionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("回想が見つかりません(id=" + id + ")"));
        return toDetailDTO(recollection);
    }

    /** 空文字はクエリ上 "指定なし" として扱いたいため null に変換する。 */
    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    private RecollectionListDTO toListDTO(Recollection r) {
        RecollectionListDTO dto = new RecollectionListDTO();
        dto.setId(r.getId());
        dto.setNumber(r.getNumber());
        dto.setTitle(r.getTitle());
        dto.setBattlefieldName(r.getBattlefield() != null ? r.getBattlefield().getName() : null);
        dto.setBattlefieldEra(r.getBattlefield() != null ? r.getBattlefield().getEra() : null);
        dto.setSwordNames(sortedSwordNames(r));
        dto.setCondition(r.getCondition());
        dto.setBossRequired(r.getBossRequired());
        return dto;
    }

    private RecollectionDetailDTO toDetailDTO(Recollection r) {
        RecollectionDetailDTO dto = new RecollectionDetailDTO();
        dto.setId(r.getId());
        dto.setNumber(r.getNumber());
        dto.setTitle(r.getTitle());
        dto.setBattlefieldName(r.getBattlefield() != null ? r.getBattlefield().getName() : null);
        dto.setBattlefieldEra(r.getBattlefield() != null ? r.getBattlefield().getEra() : null);
        dto.setSwordNames(sortedSwordNames(r));
        dto.setCondition(r.getCondition());
        dto.setBossRequired(r.getBossRequired());
        dto.setPrerequisiteTitle(r.getPrerequisite() != null ? r.getPrerequisite().getTitle() : null);
        dto.setRemarks(r.getRemarks());
        return dto;
    }

    private List<String> sortedSwordNames(Recollection r) {
        return r.getSwords().stream()
                .map(Sword::getName)
                .sorted()
                .collect(Collectors.toList());
    }
}
