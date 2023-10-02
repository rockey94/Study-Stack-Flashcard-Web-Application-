<!<template>
    <div>
        <h1>View Deck</h1>
        <div v-if="deck.length > 0">
            <ul>
                <li v-for="deck in decks" :key="deck.id">
                              <!-- Display deck names or metadata -->
        <div>
            <h3>{{ deck.name }}</h3>
            <p>Created by: {{ deck.creator }}</p>
            <!-- Add more metadata as needed -->
          </div>
        </li>
      </ul>
    </div>
    <div v-else>
      <p>No decks available.</p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      decks: [], // Assuming you have an array of decks to display
    };
  },
  // You can fetch the list of user's created decks from an API or Vuex store here
};
</script>

<style scoped>
/* Add your component-specific styles here */
</style>