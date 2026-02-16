package com.hyperativa.Mapper;

import com.hyperativa.Dtos.CardDTO;
import com.hyperativa.Entities.Card;
import com.hyperativa.Services.CryptoService;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    private final CryptoService cryptoService;

    public CardMapper(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    public CardDTO toDTO(Card card) {
        if (card == null) {
            return null;
        }
        CardDTO dto = new CardDTO();
        dto.setId(card.getId());
        dto.setIdentifier(card.getIdentifier());
        dto.setNumberInBatch(card.getNumberInBatch());
        // Decrypts when reading from database to show to user
        dto.setCardNumber(cryptoService.decrypt(card.getCardNumber()));
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
        // Encrypts before saving to database
        card.setCardNumber(cryptoService.encrypt(dto.getCardNumber()));
        return card;
    }
}
