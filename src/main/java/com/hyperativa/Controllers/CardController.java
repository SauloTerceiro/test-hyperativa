package com.hyperativa.Controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.hyperativa.Dtos.CardDTO;
import com.hyperativa.Routes.CardRoute;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final ProducerTemplate producerTemplate;

    @Autowired
    public CardController(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @PostMapping
    public ResponseEntity<CardDTO> createCard(@RequestBody Map<String, String> payload) {
        String cardNumber = payload.get("cardNumber");
        String identifier = payload.get("identifier");
        String numberInBatch = payload.get("numberInBatch");

        CardDTO cardDTO = new CardDTO();
        cardDTO.setCardNumber(cardNumber);
        cardDTO.setIdentifier(identifier);
        cardDTO.setNumberInBatch(numberInBatch);

        CardDTO createdCard = producerTemplate.requestBody(CardRoute.CREATE_CARD, cardDTO, CardDTO.class);
        return ResponseEntity.ok(createdCard);
    }

    @PostMapping("/upload")
    public ResponseEntity<List<CardDTO>> uploadBatchFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<CardDTO> createdCards = producerTemplate.requestBody(CardRoute.PROCESS_BATCH_FILE, file.getInputStream(), List.class);
        return ResponseEntity.ok(createdCards);
    }

    @GetMapping
    public ResponseEntity<List<CardDTO>> getAllCards() {
        List<CardDTO> cards = producerTemplate.requestBody(CardRoute.GET_ALL_CARDS, null, List.class);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardDTO> getCardById(@PathVariable Long id) {
        Optional<CardDTO> card = producerTemplate.requestBody(CardRoute.GET_CARD_BY_ID, id, Optional.class);
        return card.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardDTO> updateCard(@PathVariable Long id, @RequestBody CardDTO cardDetails) {
        CardDTO updatedCard = producerTemplate.requestBodyAndHeader(CardRoute.UPDATE_CARD, cardDetails, "id", id, CardDTO.class);
        return ResponseEntity.ok(updatedCard);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        producerTemplate.sendBody(CardRoute.DELETE_CARD, id);
        return ResponseEntity.noContent().build();
    }
}
