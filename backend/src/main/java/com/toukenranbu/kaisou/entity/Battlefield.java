package com.toukenranbu.kaisou.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 合戦場マスタ。
 *
 * 「鳥羽」のように同じ name が複数の時代にまたがって存在するケースがあるため、
 * name 自体は一意ではない。データ参照・検索には必ず一意な {@link #code} を用いる。
 */
@Entity
@Table(name = "battlefields")
public class Battlefield {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 合戦場の一意識別子(JSONの "id"、recollections.json からの参照キー)。
     * 通常は name と同じ値だが、name が重複する場合のみ "名前(時代)" の形式にする。
     */
    @Column(nullable = false, unique = true)
    private String code;

    /** 画面表示用の合戦場名。重複あり得るため、絞り込みキーには使わない。 */
    @Column(nullable = false)
    private String name;

    /** 時代区分(維新の記憶、江戸の記憶 等)。 */
    private String era;

    /** ゲーム内のマップ番号(例: "1-4")。未確認の場合は空文字。表示用に加え、sortOrderの計算元にもなる。 */
    private String mapNumber;

    /**
     * 並び替え専用の数値。値が小さいほど先に表示される。
     * JSONには持たせず、{@link #mapNumber} から DataInitializer が起動時に計算して設定する。
     */
    private Integer sortOrder;

    public Battlefield() {
    }

    public Battlefield(String code, String name, String era, String mapNumber) {
        this.code = code;
        this.name = name;
        this.era = era;
        this.mapNumber = mapNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
