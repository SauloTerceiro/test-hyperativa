package com.hyperativa.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hyperativa.Entities.Card;

public interface CardRepository extends JpaRepository<Card, Long> {
}
