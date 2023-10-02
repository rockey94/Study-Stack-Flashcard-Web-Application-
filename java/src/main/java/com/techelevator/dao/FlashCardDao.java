package com.techelevator.dao;

import com.techelevator.model.FlashCard;

import java.util.List;

public interface FlashCardDao {

    void updateCard(FlashCard card);

    void deleteCard(FlashCard card);

    FlashCard createCard(FlashCard card);

    FlashCard getCardById(int cardId);

    List<FlashCard> getAllCards();

    List<FlashCard> getCardsByCreator(int creator);

    List<FlashCard> getCardsByDeckId(int deckId);

    int getMaxCardIdForDeck(int deckId);

    void deleteCard(int deckId, int cardId);
}
