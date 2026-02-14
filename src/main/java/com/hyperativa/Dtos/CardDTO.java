package com.hyperativa.Dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardDTO {
    private Long id;
    private String identifier;
    private String numberInBatch;
    private String cardNumber;
}
