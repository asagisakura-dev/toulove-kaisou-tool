package com.toukenranbu.kaisou.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 刀剣男士マスタ。
 */
@Entity
@Table(name = "swords")
public class Sword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /**
     * 読み仮名(ひらがな)。五十音順ソートに使用する。
     * AIによる推測は行わず、未確認の場合は空文字のままにしておく。
     */
    private String reading;

    /** 刀種(太刀、打刀、短刀 等)。任意項目。 */
    private String type;

    private String school;

    public Sword() {
    }

    public Sword(String name, String reading, String type, String school) {
        this.name = name;
        this.reading = reading;
        this.type = type;
        this.school = school;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReading() {
        return reading;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
}
