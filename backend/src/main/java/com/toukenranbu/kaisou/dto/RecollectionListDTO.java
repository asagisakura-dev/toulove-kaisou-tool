package com.toukenranbu.kaisou.dto;

import java.util.List;

/**
 * 一覧表示用DTO。
 * 回想番号 / タイトル / 合戦場 / 必要刀剣男士 / 発生条件 を保持する。
 */
public class RecollectionListDTO {

    private Long id;
    private Integer number;
    private String title;
    private String battlefieldName;
    /** 合戦場の時代区分。同名の合戦場(例:鳥羽)を画面上で区別する際に使う。 */
    private String battlefieldEra;
    private List<String> swordNames;
    private String condition;
    /** ボスマス関連の条件を含むかどうかの補助フラグ。正確な条件は condition を参照すること。 */
    private Boolean bossRequired;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBattlefieldName() {
        return battlefieldName;
    }

    public void setBattlefieldName(String battlefieldName) {
        this.battlefieldName = battlefieldName;
    }

    public String getBattlefieldEra() {
        return battlefieldEra;
    }

    public void setBattlefieldEra(String battlefieldEra) {
        this.battlefieldEra = battlefieldEra;
    }

    public List<String> getSwordNames() {
        return swordNames;
    }

    public void setSwordNames(List<String> swordNames) {
        this.swordNames = swordNames;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Boolean getBossRequired() {
        return bossRequired;
    }

    public void setBossRequired(Boolean bossRequired) {
        this.bossRequired = bossRequired;
    }
}
