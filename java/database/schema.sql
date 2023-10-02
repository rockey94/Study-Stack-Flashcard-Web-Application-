BEGIN TRANSACTION;

DROP TABLE IF EXISTS keywords;
DROP TABLE IF EXISTS flash_card;
DROP TABLE IF EXISTS flash_card_deck;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
   user_id SERIAL PRIMARY KEY,
   username varchar(50) NOT NULL UNIQUE,
   password_hash varchar(200) NOT NULL,
   role varchar(50) NOT NULL
);

CREATE TABLE flash_card_deck(
    deck_id SERIAL PRIMARY KEY,
    name varchar(255) NOT NULL,
    creator INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    cover_image varchar(4096),
    description varchar(999)
);

CREATE TABLE flash_card(
    card_id INT,
    deck_id INT NOT NULL,
    question varchar(255) NOT NULL,
    answer varchar(255) NOT NULL,
    creator INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    PRIMARY KEY (deck_id, card_id)
);

-- Add the CASCADE delete on the foreign key
ALTER TABLE flash_card
ADD FOREIGN KEY (deck_id)
REFERENCES flash_card_deck(deck_id) ON DELETE CASCADE;

CREATE TABLE keywords(
    keyword_id SERIAL PRIMARY KEY,
    deck_id INT NOT NULL,
    card_id INT NOT NULL,
    keyword varchar(255),
    FOREIGN KEY (deck_id, card_id) REFERENCES flash_card(deck_id, card_id) ON DELETE CASCADE
);


COMMIT TRANSACTION;

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE users TO final_capstone_appuser;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE flash_card_deck TO final_capstone_appuser;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE flash_card TO final_capstone_appuser;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE keywords TO final_capstone_appuser;
GRANT USAGE, SELECT ON SEQUENCE users_user_id_seq TO final_capstone_appuser;
GRANT USAGE, SELECT ON SEQUENCE flash_card_deck_deck_id_seq TO final_capstone_appuser;
GRANT USAGE, SELECT ON SEQUENCE flash_card_card_id_seq TO final_capstone_appuser;
GRANT USAGE, SELECT ON SEQUENCE keywords_keyword_id_seq TO final_capstone_appuser;