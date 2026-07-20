package com.toukenranbu.kaisou.controller;

import com.toukenranbu.kaisou.dto.RecollectionDetailDTO;
import com.toukenranbu.kaisou.dto.RecollectionListDTO;
import com.toukenranbu.kaisou.service.RecollectionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 回想検索・詳細取得 REST API。
 *
 * GET /api/recollections                                       … 全件取得
 * GET /api/recollections?sword=一期一振                          … 刀剣検索
 * GET /api/recollections?battlefield=池田屋一階                   … 合戦場検索
 * GET /api/recollections?sword=一期一振&battlefield=池田屋一階      … AND検索
 * GET /api/recollections/{id}                                   … 詳細取得
 *
 * ※ battlefield パラメータには合戦場の一意識別子(Battlefield.code、GET /api/battlefields の id)を渡すこと。
 *   「鳥羽」のように name だけでは一意に定まらない合戦場があるため、name ではなく code で絞り込む。
 */
@RestController
@RequestMapping("/api/recollections")
public class RecollectionController {

    private final RecollectionService recollectionService;

    public RecollectionController(RecollectionService recollectionService) {
        this.recollectionService = recollectionService;
    }

    @GetMapping
    public List<RecollectionListDTO> list(
            @RequestParam(required = false) String sword,
            @RequestParam(required = false) String battlefield) {
        return recollectionService.search(sword, battlefield);
    }

    @GetMapping("/{id}")
    public RecollectionDetailDTO detail(@PathVariable Long id) {
        return recollectionService.getDetail(id);
    }
}
