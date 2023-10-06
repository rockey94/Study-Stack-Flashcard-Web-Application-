<template>
  
</template>

<script>
import DeckService from "../services/DeckService";
export default {
  data() {
    return {
      
    };
  },
  methods: {
    deleteDeck() {
      DeckService.deleteDeck(this.newDeck)
        .then((response) => {
          if (response.status == 200) {
            this.created = true;
            this.creationMessage = `Deck "${this.newDeck.name}" created!`
          }
        })
        .catch((error) => {
          if (error.response) {
            this.error = true;
          } else if (error.request) {
            this.error = true;
          }
        });
    },
</script>

<style>

</style>