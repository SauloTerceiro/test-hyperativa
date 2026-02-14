package com.hyperativa.Mapper;

import com.hyperativa.Dtos.CardDTO;
import com.hyperativa.Entities.Card;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public CardDTO toDTO(Card card) {
        if (card == null) {
            return null;
        }
        CardDTO dto = new CardDTO();
        dto.setId(card.getId());
        dto.setIdentifier(card.getIdentifier());
        dto.setNumberInBatch(card.getNumberInBatch());
        dto.setCardNumber(card.getCardNumber());
        return dto;
    }

    public Card toEntity(CardDTO dto) {
        if (dto == null) {
            return null;
        }
        Card card = new Card();
        card.setId(dto.getId());
        card.setIdentifier(dto.getIdentifier());
        card.setNumberInBatch(dto.getNumberInBatch());
        card.setCardNumber(dto.getCardNumber());
        return card;
    }
}
