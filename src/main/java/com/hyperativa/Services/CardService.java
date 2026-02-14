package com.hyperativa.Services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.camel.Header;
import org.springframework.stereotype.Service;

import com.hyperativa.Dtos.CardDTO;
import com.hyperativa.Entities.Card;
import com.hyperativa.Mapper.CardMapper;
import com.hyperativa.Repositories.CardRepository;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    public CardService(CardRepository cardRepository, CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
    }

    public List<CardDTO> getAllCards() {
        return cardRepository.findAll().stream()
                .map(cardMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<CardDTO> getCardById(Long id) {
        return cardRepository.findById(id).map(cardMapper::toDTO);
    }

    public CardDTO createCard(CardDTO cardDTO) {
        if (cardDTO.getCardNumber() == null || cardDTO.getCardNumber().isEmpty()) {
            throw new IllegalArgumentException("Card number is required");
        }
        Card card = cardMapper.toEntity(cardDTO);
        Card savedCard = cardRepository.save(card);
        return cardMapper.toDTO(savedCard);
    }

    public CardDTO updateCard(@Header("id") Long id, CardDTO cardDetails) {
        Optional<Card> card = cardRepository.findById(id);
        if (card.isPresent()) {
            Card existingCard = card.get();
            existingCard.setIdentifier(cardDetails.getIdentifier());
            existingCard.setNumberInBatch(cardDetails.getNumberInBatch());
            existingCard.setCardNumber(cardDetails.getCardNumber());
            Card updatedCard = cardRepository.save(existingCard);
            return cardMapper.toDTO(updatedCard);
        }
        return null;
    }

    public void deleteCard(Long id) {
        cardRepository.deleteById(id);
    }
}
