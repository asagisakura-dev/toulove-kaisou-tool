package com.toukenranbu.kaisou.repository;

import com.toukenranbu.kaisou.entity.Sword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SwordRepository extends JpaRepository<Sword, Long> {
    Optional<Sword> findByName(String name);
}
