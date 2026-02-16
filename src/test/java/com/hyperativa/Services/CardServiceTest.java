package com.hyperativa.Services;

import com.hyperativa.Dtos.CardDTO;
import com.hyperativa.Entities.Card;
import com.hyperativa.Mapper.CardMapper;
import com.hyperativa.Repositories.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private CryptoService cryptoService;

    @InjectMocks
    private CardService cardService;

    private Card card;
    private CardDTO cardDTO;

    @BeforeEach
    void setUp() {
        card = new Card();
        card.setId(1L);
        card.setIdentifier("C");
        card.setNumberInBatch("000001");
        card.setCardNumber("encryptedCardNumber");

        cardDTO = new CardDTO();
        cardDTO.setId(1L);
        cardDTO.setIdentifier("C");
        cardDTO.setNumberInBatch("000001");
        cardDTO.setCardNumber("decryptedCardNumber");
    }

    @Test
    void getAllCards_ShouldReturnListOfCardDTOs() {
        when(cardRepository.findAll()).thenReturn(Collections.singletonList(card));
        when(cardMapper.toDTO(any(Card.class))).thenReturn(cardDTO);

        List<CardDTO> result = cardService.getAllCards();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cardDTO.getCardNumber(), result.get(0).getCardNumber());
        verify(cardRepository, times(1)).findAll();
    }

    @Test
    void getCardById_WhenCardExists_ShouldReturnCardDTO() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.toDTO(card)).thenReturn(cardDTO);

        Optional<CardDTO> result = cardService.getCardById(1L);

        assertTrue(result.isPresent());
        assertEquals(cardDTO.getId(), result.get().getId());
        verify(cardRepository, times(1)).findById(1L);
    }

    @Test
    void getCardById_WhenCardDoesNotExist_ShouldReturnEmptyOptional() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<CardDTO> result = cardService.getCardById(1L);

        assertFalse(result.isPresent());
        verify(cardRepository, times(1)).findById(1L);
    }

    @Test
    void createCard_ShouldReturnCreatedCardDTO() {
        when(cardMapper.toEntity(any(CardDTO.class))).thenReturn(card);
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toDTO(any(Card.class))).thenReturn(cardDTO);

        CardDTO result = cardService.createCard(cardDTO);

        assertNotNull(result);
        assertEquals(cardDTO.getCardNumber(), result.getCardNumber());
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void createCard_WhenCardNumberIsNull_ShouldThrowException() {
        CardDTO invalidCardDTO = new CardDTO();
        invalidCardDTO.setCardNumber(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cardService.createCard(invalidCardDTO);
        });

        assertEquals("Card number is required", exception.getMessage());
        verify(cardRepository, never()).save(any());
    }

    @Test
    void updateCard_WhenCardExists_ShouldReturnUpdatedCardDTO() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cryptoService.encrypt(anyString())).thenReturn("newEncryptedCardNumber");
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toDTO(any(Card.class))).thenReturn(cardDTO);

        CardDTO result = cardService.updateCard(1L, cardDTO);

        assertNotNull(result);
        verify(cardRepository, times(1)).findById(1L);
        verify(cryptoService, times(1)).encrypt(cardDTO.getCardNumber());
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void updateCard_WhenCardDoesNotExist_ShouldReturnNull() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        CardDTO result = cardService.updateCard(1L, cardDTO);

        assertNull(result);
        verify(cardRepository, times(1)).findById(1L);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void deleteCard_ShouldCallRepositoryDelete() {
        doNothing().when(cardRepository).deleteById(1L);

        cardService.deleteCard(1L);

        verify(cardRepository, times(1)).deleteById(1L);
    }

    @Test
    void processBatchFile_ShouldProcessValidLinesAndSkipInvalid() {
        String fileContent = "DESAFIO-HYPERATIVA           20180524LOTE0001000002\n" +
                             "C2     4456897999999999\n" +
                             "C10    4456897919999999\n" +
                             "Invalid Line\n" +
                             "LOTE0001000002";
        InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));

        // Setup mocks for the calls made inside processBatchFile
        when(cardMapper.toEntity(any(CardDTO.class))).thenReturn(card);
        
        // Use any() instead of anyList() to be safer, and return a list with 2 items
        when(cardRepository.saveAll(any())).thenReturn(Arrays.asList(card, card));
        
        when(cardMapper.toDTO(any(Card.class))).thenReturn(cardDTO);

        List<CardDTO> result = cardService.processBatchFile(inputStream);

        assertNotNull(result);
        assertEquals(2, result.size());
        // Verify repository saveAll was called once
        verify(cardRepository, times(1)).saveAll(any());
    }
}
