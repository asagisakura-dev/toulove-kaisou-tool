package com.toukenranbu.kaisou.repository;

import com.toukenranbu.kaisou.entity.Battlefield;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BattlefieldRepository extends JpaRepository<Battlefield, Long> {
    Optional<Battlefield> findByCode(String code);

    List<Battlefield> findAllByOrderBySortOrderAsc();
}
