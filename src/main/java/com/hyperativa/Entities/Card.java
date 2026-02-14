package com.hyperativa.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identifier", nullable = false)
    private String identifier;

    @Column(name = "number_in_batch", nullable = false)
    private String numberInBatch;

    @Column(name = "card_number", nullable = false)
    private String cardNumber;
}
