package com.techelevator.dao;

import com.techelevator.model.FlashCardDeck;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class JdbcFlashCardDeckDao implements FlashCardDeckDao {
    private JdbcTemplate template;

    public JdbcFlashCardDeckDao(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }

    private RowMapper<FlashCardDeck> flashCardDeckRowMapper = (rs, rowNum) -> {
        FlashCardDeck deck = new FlashCardDeck();
        deck.setDeckId(rs.getInt("deck_id"));
        deck.setName(rs.getString("name"));
        deck.setCreator(rs.getInt("creator"));
        deck.setCoverImage(rs.getString("cover_image"));
        deck.setDescription(rs.getString("description"));
        return deck;
    };

    @Override
    public void updateDeck(FlashCardDeck deck) {
        String sql = "UPDATE flash_card_deck SET name = ?, creator = ?, cover_image = ?, description = ? WHERE deck_id = ?";
        template.update(sql, deck.getName(), deck.getCreator(), deck.getCoverImage(), deck.getDescription(), deck.getDeckId());
    }

    @Override
    public void deleteDeck(int deckId) {
        String sql = "DELETE FROM flash_card_deck WHERE deck_id = ?";
        template.update(sql, deckId);
    }

    @Override
    public void createDeck(FlashCardDeck deck) {
        String sql = "INSERT into flash_card_deck(name, creator, cover_image, description) VALUES(?, ?, ?, ?)";
        template.update(sql, deck.getName(), deck.getCreator(), deck.getCoverImage(), deck.getDescription());
    }

    @Override
    public FlashCardDeck getDeckbyId(int deckId) {
        String sql = "SELECT * FROM flash_card_deck WHERE deck_id = ?";
        return template.queryForObject(sql, flashCardDeckRowMapper, deckId);
    }

    @Override
    public List<FlashCardDeck> getAllDecks() {
        String sql = "SELECT * FROM flash_card_deck";
        return template.query(sql, flashCardDeckRowMapper);
    }

    @Override
    public List<FlashCardDeck> getDecksByCreator(int creator) {
        String sql = "SELECT * FROM flash_card_deck WHERE creator = ?";
        return template.query(sql, flashCardDeckRowMapper, creator);
    }
}
