package com.techelevator.dao;

import com.techelevator.model.FlashCardDeck;

import java.util.List;

public interface FlashCardDeckDao {

    void updateDeck(FlashCardDeck deck);

    void deleteDeck(int deckId);

    void createDeck(FlashCardDeck deck);

    FlashCardDeck getDeckbyId(int deckId);

    List<FlashCardDeck> getAllDecks();

    List<FlashCardDeck> getDecksByCreator(int creator);
}
