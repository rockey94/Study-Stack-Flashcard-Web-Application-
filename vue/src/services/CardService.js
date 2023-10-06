import axios from 'axios';
export default {

    createCard(card) {
        return axios.post("/create-card", card);
    },

    deleteCard(deckId, cardId, card) {
        return axios.delete(`/delete-card/${deckId}/${cardId}`, card);
    },

    editCard(deckId, cardId, card) {
        return axios.put(`/edit-card/${deckId}/${cardId}`, card);
    },

    viewCardsByDeckId(deckId) {
        return axios.get(`/view-cards/${deckId}`);
    },

    getCardById(deckId, cardId,) {
        return axios.get(`/card/${deckId}/${cardId}`);
    },
    getAllCards() {
        return axios.get("/all-cards");
    },

    addKeyword(deckId, cardId, keyword) {
        // Replace placeholders with actual values in the URL
        return axios.post(`/add-keyword/${deckId}/${cardId}`, keyword);
    }
}