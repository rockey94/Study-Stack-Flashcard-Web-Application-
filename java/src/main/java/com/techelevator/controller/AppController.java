package com.techelevator.controller;

import com.techelevator.dao.FlashCardDeckDao;
import com.techelevator.dao.FlashCardDao;
import com.techelevator.dao.KeywordDao;
import com.techelevator.model.FlashCard;
import com.techelevator.model.FlashCardDeck;
import com.techelevator.model.Keyword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Key;
import java.util.List;


@RestController
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class AppController {

    @Autowired
    private FlashCardDeckDao deckDao;
    @Autowired
    private FlashCardDao cardDao;
    @Autowired
    private KeywordDao keywordDao;

    // DECK METHODS

    @RequestMapping(path = "/create-deck", method = RequestMethod.POST)
    public void createDeck(@Valid @RequestBody FlashCardDeck deck) {

        deckDao.createDeck(deck);
    }

    @RequestMapping(path = "/view-decks/{creator}", method = RequestMethod.GET)
    public ResponseEntity<List<FlashCardDeck>> viewDecksByCreator(@PathVariable int creator) {
        List<FlashCardDeck> decks = deckDao.getDecksByCreator(creator);

        return ResponseEntity.ok(decks);
    }

    @RequestMapping(path = "/delete-deck/{deckId}", method = RequestMethod.DELETE)
    public void deleteDeck(@PathVariable int deckId) {

        deckDao.deleteDeck(deckId);
    }

    @RequestMapping(path = "/edit-deck/{deckId}", method = RequestMethod.PUT)
    public void updateDeck(@Valid @RequestBody FlashCardDeck deck, @PathVariable int deckId) {
        deck.setDeckId(deckId);
        deckDao.updateDeck(deck);
    }

    @RequestMapping(path = "/stack/{deckId}", method = RequestMethod.GET)
    public FlashCardDeck getDeckById(@PathVariable int deckId) {

        return deckDao.getDeckbyId(deckId);
    }

    // CARD METHODS

    @RequestMapping(path = "/edit-card/{deckId}/{cardId}", method = RequestMethod.PUT)
    public void updateCard(@Valid @RequestBody FlashCard card, @PathVariable int deckId, @PathVariable int cardId) {
        card.setCardId(cardId);
        card.setDeckId(deckId);
        card.setKeyword(card.getKeyword());
        cardDao.updateCard(card);
        keywordDao.updateKeyword(card);
    }

    @RequestMapping(path = "/create-card", method = RequestMethod.POST)
    public FlashCard createCard(@Valid @RequestBody FlashCard card) {
        // Fetch the maximum card ID for the given deck and assign next ID to the new card
        int nextCardId = cardDao.getMaxCardIdForDeck(card.getDeckId()) + 1;
        card.setCardId(nextCardId);
        FlashCard createdCard = cardDao.createCard(card);
        keywordDao.addKeyword(card);
        return createdCard;
    }


    @RequestMapping(path = "/view-cards/{deckId}", method = RequestMethod.GET)
    public ResponseEntity<List<FlashCard>> viewCardsByDeckId(@PathVariable int deckId) {
        List<FlashCard> cards = cardDao.getCardsByDeckId(deckId);

        return ResponseEntity.ok(cards);
    }

    @RequestMapping(path = "/card/{deckId}/{cardId}", method = RequestMethod.GET)
    public FlashCard getCardById(@PathVariable int deckId, @PathVariable int cardId) {

        return cardDao.getCardById(deckId, cardId);
    }

    @RequestMapping(path = "/delete-card/{deckId}/{cardId}", method = RequestMethod.DELETE)
    public void deleteCard(@PathVariable int deckId, @PathVariable int cardId) {
        cardDao.deleteCard(deckId, cardId);
    }

    @GetMapping("/all-cards")
    public List<FlashCard> getAllCards() {
        return cardDao.getAllCards(); 
    }

    // KEYWORD METHODS

    @RequestMapping(path = "/add-keyword/{deckId}/{cardId}", method = RequestMethod.POST)
    public ResponseEntity<String> addKeyword(@PathVariable int deckId, @PathVariable int cardId, @RequestBody String keyword) {
        FlashCard card = cardDao.getCardById(deckId, cardId); // Fetch the card from the database
        if (card != null) {
            card.setDeckId(deckId);
            card.setCardId(cardId);
            card.setKeyword(keyword);
            keywordDao.addKeyword(card); // Add the keyword to the database
            return ResponseEntity.ok("Keyword added successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Card not found.");
        }
    }
}

