package com.toukenranbu.kaisou.repository;

import com.toukenranbu.kaisou.entity.Recollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecollectionRepository extends JpaRepository<Recollection, Long> {

    /**
     * 刀剣男士名・合戦場コード(Battlefield.code)による検索。
     * 両方 null の場合は全件、片方のみ指定の場合はその条件のみ、両方指定の場合は AND 検索となる。
     *
     * 合戦場は name だけでは一意に定まらない(例:「鳥羽」が複数の時代に存在する)ため、
     * 検索キーには必ず一意な code を使用する。
     */
    @Query("SELECT DISTINCT r FROM Recollection r "
            + "LEFT JOIN r.swords s "
            + "LEFT JOIN r.battlefield b "
            + "WHERE (:swordName IS NULL OR s.name = :swordName) "
            + "AND (:battlefieldCode IS NULL OR b.code = :battlefieldCode) "
            + "ORDER BY r.number ASC")
    List<Recollection> search(@Param("swordName") String swordName,
                               @Param("battlefieldCode") String battlefieldCode);
}
