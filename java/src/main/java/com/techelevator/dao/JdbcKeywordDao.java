package com.techelevator.dao;

import com.techelevator.model.FlashCard;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class JdbcKeywordDao implements KeywordDao {
    private JdbcTemplate template;

    public JdbcKeywordDao(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }

    @Override
    public void addKeyword(FlashCard card) {
        String sql = "INSERT INTO keywords(deck_id, card_id, keyword) VALUES (?, ?, ?)";
        template.update(sql, card.getDeckId(), card.getCardId(), card.getKeyword());
    }

    @Override
    public void updateKeyword(FlashCard card) {
        String sql = "UPDATE keywords SET keyword = ? WHERE deck_id = ? AND card_id = ?";
        template.update(sql, card.getKeyword(), card.getDeckId(), card.getCardId() );
    }

    @Override
    public String getKeywordById(FlashCard card) {
        String sql = "SELECT keyword FROM keywords WHERE deck_id = ? AND card_id = ?";
        int deckId = card.getDeckId();
        int cardId = card.getCardId();
        return template.queryForObject(sql, String.class, deckId, cardId);
    }
}
