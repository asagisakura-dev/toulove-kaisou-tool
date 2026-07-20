package com.toukenranbu.kaisou.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

/**
 * 回想エンティティ。
 * 合戦場(N:1)、必要刀剣男士(N:N)、前提回想(自己参照 N:1) を持つ。
 */
@Entity
@Table(name = "recollections")
public class Recollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ゲーム内の回想番号(表示用)。 */
    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "battlefield_id")
    private Battlefield battlefield;

    /** 発生条件。DBの列名は condition が予約語と衝突しやすいため condition_text とする。 */
    @Column(name = "condition_text", columnDefinition = "TEXT")
    private String condition;

    @Column(name = "boss_required")
    private Boolean bossRequired = false;

    /** 前提となる回想(なければ null)。 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prerequisite_id")
    private Recollection prerequisite;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "recollection_swords",
            joinColumns = @JoinColumn(name = "recollection_id"),
            inverseJoinColumns = @JoinColumn(name = "sword_id")
    )
    private Set<Sword> swords = new HashSet<>();

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

    public Battlefield getBattlefield() {
        return battlefield;
    }

    public void setBattlefield(Battlefield battlefield) {
        this.battlefield = battlefield;
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

    public Recollection getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(Recollection prerequisite) {
        this.prerequisite = prerequisite;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Set<Sword> getSwords() {
        return swords;
    }

    public void setSwords(Set<Sword> swords) {
        this.swords = swords;
    }
}
