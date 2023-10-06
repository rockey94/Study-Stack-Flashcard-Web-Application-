package com.techelevator.dao;

import com.techelevator.model.FlashCard;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class JdbcFlashCardDao implements FlashCardDao {

    private JdbcTemplate template;

    public JdbcFlashCardDao(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }

    private RowMapper<FlashCard> flashCardRowMapper = (rs, rowNum) -> {
        FlashCard card = new FlashCard();
        card.setCardId(rs.getInt("card_id"));
        card.setDeckId(rs.getInt("deck_id"));
        card.setQuestion(rs.getString("question"));
        card.setAnswer(rs.getString("answer"));
        card.setKeyword(rs.getString("keyword"));
        card.setCreator(rs.getInt("creator"));

        return card;
    };

    @Override
    public void updateCard(FlashCard card) {
        String updateFlashCardSql = "UPDATE flash_card SET deck_id = ?, question = ?, answer = ?, creator = ? WHERE deck_id = ? AND card_id = ?";
        template.update(updateFlashCardSql, card.getDeckId(), card.getQuestion(), card.getAnswer(), card.getCreator(), card.getDeckId(), card.getCardId());
    }

    //TODO why is this here twice?
    @Override
    public void deleteCard(FlashCard card) {
        String sql = "DELETE FROM flash_card WHERE card_id = ?";
        template.update(sql, card.getCardId());
    }

    public FlashCard createCard(FlashCard card) {
        // fetch the max card_id for the deck and increment by 1
        String getMaxCardIdForDeckSql = "SELECT COALESCE(MAX(card_id), 0) + 1 FROM flash_card WHERE deck_id = ?";
        int newCardId = template.queryForObject(getMaxCardIdForDeckSql, Integer.class, card.getDeckId());

        // insert the card with the calculated card_id
        String sql = "INSERT INTO flash_card(card_id, deck_id, question, answer, creator) VALUES(?, ?, ?, ?, ?)";
        template.update(sql, newCardId, card.getDeckId(), card.getQuestion(), card.getAnswer(), card.getCreator());
        return card;
    }

    @Override
    public FlashCard getCardById(int deckId, int cardId) {
        String sql = "SELECT f.card_id, f.deck_id, f.question, f.answer, f.creator, k.keyword \n" +
                "FROM flash_card f \n" +
                "LEFT JOIN keywords k ON f.deck_id = k.deck_id AND f.card_id = k.card_id \n" +
                "WHERE deck_id = ? AND card_id = ?";
        return template.queryForObject(sql, flashCardRowMapper, deckId, cardId);
    }

    @Override
    public List<FlashCard> getAllCards() {
        String sql = "SELECT f.card_id, f.deck_id, f.question, f.answer, f.creator, k.keyword " +
                "FROM flash_card f " +
                "LEFT JOIN keywords k ON f.deck_id = k.deck_id AND f.card_id = k.card_id ORDER BY f.card_id";
        return template.query(sql, flashCardRowMapper);
    }

    @Override
    public List<FlashCard> getCardsByCreator(int creator) {
        String sql = "SELECT f.card_id, f.deck_id, f.question, f.answer, f.creator, k.keyword " +
                "FROM flash_card f " +
                "LEFT JOIN keywords k ON f.deck_id = k.deck_id AND f.card_id = k.card_id " +
                "WHERE f.creator = ? ORDER BY f.card_id";
        return template.query(sql, flashCardRowMapper, creator);
    }

    @Override
    public List<FlashCard> getCardsByDeckId(int deckId) {
        String sql = "SELECT f.card_id, f.deck_id, f.question, f.answer, f.creator, k.keyword " +
                "FROM flash_card f " +
                "LEFT JOIN keywords k ON f.deck_id = k.deck_id AND f.card_id = k.card_id " +
                "WHERE f.deck_id = ? ORDER BY f.card_id";
        return template.query(sql, flashCardRowMapper, deckId);
    }

    @Override
    public int getMaxCardIdForDeck(int deckId) {
        String sql = "SELECT COALESCE(MAX(card_id), 0) FROM flash_card WHERE deck_id = ?";
        return template.queryForObject(sql, Integer.class, deckId);
    }

    @Override
    public void deleteCard(int deckId, int cardId) {
        String sql = "DELETE FROM flash_card WHERE deck_id = ? AND card_id = ?";
        template.update(sql, deckId, cardId);
    }


}
