package com.techelevator.dao;

import com.techelevator.model.FlashCard;
import com.techelevator.model.Keyword;

public interface KeywordDao {
    void addKeyword(FlashCard card);

    void updateKeyword(FlashCard card);

    String getKeywordById(FlashCard card);
}
