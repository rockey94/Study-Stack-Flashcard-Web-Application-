import axios from 'axios';
export default {

    createDeck(newDeck) {
        return axios.post("/create-deck", newDeck);
    },

    deleteDeck(deckId) {
        return axios.delete("/delete-deck/" + deckId);
    },

    editDeck(deckId, deck) {
        return axios.put('/edit-deck/' + deckId, deck);
    },
    
    viewDecksByCreator(creatorId) {
        return axios.get(`/view-decks/${creatorId}`);
    },

    getDeckById(deckId) {
        return axios.get(`/stack/${deckId}`)
    },
}