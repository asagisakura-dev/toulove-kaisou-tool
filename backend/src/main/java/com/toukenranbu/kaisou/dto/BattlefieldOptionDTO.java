package com.toukenranbu.kaisou.dto;

/**
 * 合戦場プルダウンの選択肢用DTO。
 * id は検索APIの battlefield パラメータにそのまま渡す一意識別子。
 */
public class BattlefieldOptionDTO {

    private String id;
    private String name;
    private String era;
    private String mapNumber;

    public BattlefieldOptionDTO() {
    }

    public BattlefieldOptionDTO(String id, String name, String era, String mapNumber) {
        this.id = id;
        this.name = name;
        this.era = era;
        this.mapNumber = mapNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEra() {
        return era;
    }

    public void setEra(String era) {
        this.era = era;
    }

    public String getMapNumber() {
        return mapNumber;
    }

    public void setMapNumber(String mapNumber) {
        this.mapNumber = mapNumber;
    }
}
