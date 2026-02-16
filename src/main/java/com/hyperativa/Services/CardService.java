package com.hyperativa.Services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.camel.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hyperativa.Dtos.CardDTO;
import com.hyperativa.Entities.Card;
import com.hyperativa.Mapper.CardMapper;
import com.hyperativa.Repositories.CardRepository;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final CryptoService cryptoService;
    private static final int BATCH_SIZE = 1000;

    public CardService(CardRepository cardRepository, CardMapper cardMapper, CryptoService cryptoService) {
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
        this.cryptoService = cryptoService;
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
        // The mapper already handles encryption in toEntity
        Card card = cardMapper.toEntity(cardDTO);
        Card savedCard = cardRepository.save(card);
        // The mapper handles decryption in toDTO
        return cardMapper.toDTO(savedCard);
    }

    public CardDTO updateCard(@Header("id") Long id, CardDTO cardDetails) {
        Optional<Card> card = cardRepository.findById(id);
        if (card.isPresent()) {
            Card existingCard = card.get();
            existingCard.setIdentifier(cardDetails.getIdentifier());
            existingCard.setNumberInBatch(cardDetails.getNumberInBatch());
            // We need to encrypt manually here because we are updating the entity directly
            existingCard.setCardNumber(cryptoService.encrypt(cardDetails.getCardNumber()));

            Card updatedCard = cardRepository.save(existingCard);
            return cardMapper.toDTO(updatedCard);
        }
        return null;
    }

    public void deleteCard(Long id) {
        cardRepository.deleteById(id);
    }

    @Transactional
    public List<CardDTO> processBatchFile(InputStream inputStream) {
        List<CardDTO> createdCards = new ArrayList<>();
        List<Card> batch = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines or lines that don't start with 'C' (like header/footer)
                if (line.trim().isEmpty() || !line.startsWith("C")) {
                    continue;
                }

                if (line.length() < 8) {
                    continue; // Skip invalid lines
                }

                String identifier = line.substring(0, 1);
                String numberInBatch = line.substring(1, Math.min(line.length(), 7)).trim();
                String cardNumber = "";
                
                if (line.length() >= 8) {
                     // Take up to index 27 (exclusive) or end of line
                     cardNumber = line.substring(7, Math.min(line.length(), 27)).trim();
                }

                CardDTO cardDTO = new CardDTO();
                cardDTO.setIdentifier(identifier);
                cardDTO.setNumberInBatch(numberInBatch);
                cardDTO.setCardNumber(cardNumber);

                // Convert to entity (handles encryption)
                Card card = cardMapper.toEntity(cardDTO);
                batch.add(card);

                // If batch size reached, save and clear
                if (batch.size() >= BATCH_SIZE) {
                    saveBatch(batch, createdCards);
                }
            }
            
            // Save remaining items
            if (!batch.isEmpty()) {
                saveBatch(batch, createdCards);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error processing batch file", e);
        }
        return createdCards;
    }

    private void saveBatch(List<Card> batch, List<CardDTO> createdCards) {
        List<Card> savedCards = cardRepository.saveAll(batch);
        // Convert saved entities back to DTOs (handles decryption)
        for (Card savedCard : savedCards) {
            createdCards.add(cardMapper.toDTO(savedCard));
        }
        batch.clear();
    }
}
