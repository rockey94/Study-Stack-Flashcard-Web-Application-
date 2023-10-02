<template>
  <div class="home">
    <button id="logout-button" v-if="$store.state.token != ''" @click="logout">
      Log Out
    </button>
    <img id="s-logo" src="S-logo.png" alt="Study Stack logo" />
    <div id="welcome">
      <h1>Welcome to Study Stack!</h1>
      <p>What do you want to study today?</p>
      <br /><br />
      <button id="create-deck-button" @click="goToCreateDeck">
        Create a Flashcard Stack</button
      ><br />
    </div>

    <div id="created-decks">
      <!-- Display decks by creator -->
      <div id="created-decks-header">
        <h2>Created Stacks</h2>
      </div>
      <div class="created-decks-container">
        <div class="deck-list">
          <div class="deck" v-for="deck in decks" :key="deck.deck_id">

            <div 
            class="deck-item" 
            @mouseover="hover = true"
            @mouseleave="hover = false">
              <div class="deck-title">{{ deck.name }}</div>
              <div class="deck-container">
                <button id="edit-button" v-if="hover" v-on:click="goToDeck(deck.deckId)">Edit</button>
                <div v-if="deck.coverImage" class="cover-image">
                  <img :src="deck.coverImage" alt="Deck Cover" />
                </div>
                <div v-else class="cover-image">
                  <img src="default-cover-image.png" alt="Deck Cover" />
                </div>
                <button id="study-button" v-if="hover" v-on:click="goToStudySession(deck.deckId)">Study</button>
              </div>
            </div>

          </div>
        </div>
        <p v-if="decks.length === 0" class="no-decks-message">
          Your stacks will appear here. Get going!
        </p>
        <div
          class="deck"
          v-for="deck in decks"
          :key="deck.deck_id"
          @click="handleDeckClick(deck)"
        ></div>
      </div>
    </div>
  </div>
</template>
<script>
import DeckService from "../services/DeckService";

export default {
  name: "home",
  data() {
    return {
      decks: [],
      hover: false,
    };
  },
  computed: {
    creatorId() {
      return this.$store.state.user.id;
    },
  },
  created() {
    this.fetchDecksByCreator();
  },
  methods: {
    handleDeckClick(deck) {
      console.log(deck.name); // For now, this just logs the deck name.
    },
    goToCreateDeck() {
      this.$router.push("/create-stack");
    },
    fetchDecksByCreator() {
      DeckService.viewDecksByCreator(this.creatorId)
        .then((response) => {
          this.decks = response.data;
        })
        .catch((error) => {
          console.error("Error fetching decks for creator:", error);
        });
    },
    logout() {
      this.$router.push("/logout");
    },
    goToDeck(deckId) {
      this.$router.push("/stack/" + deckId);
    },
    goToStudySession(deckId) {
      this.$router.push("/study/" + deckId);
    }
  },
};
</script>

<style scoped>
.home {
  background-color: #cdecd7;
  min-height: 100vh;
  font-family: Verdana;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.deck-item:hover {
  background-color: rgb(245, 245, 243);
}

.deck-container {
  display: flex;
  justify-content: center;
}

#edit-button, #study-button {
  color: #fff;
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  font-size: 12px;
  max-height: 25px;
  align-items: bottom;
  cursor: pointer;
}

#edit-button {
  background-color: #3498db;
}

#study-button {
  background-color: #66c985;
}

#logout-button {
  background-color: #df8d6d;
  color: #fff;
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  position: absolute;
  top: 20px;
  left: 20px;
}

#s-logo {
  position: absolute;
  top: 20px;
  right: 20px;
  height: 100px;
}

#welcome {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
}

#created-decks {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 80%;
  background-color: #f5e5fa;
  padding: 20px;
  border-radius: 15px;
  text-align: center;
  margin: 20px;
  /* test */
  overflow-x: auto; /* Enable horizontal scrolling */
  white-space: nowrap; /* Prevent line breaks within the container */
}

h1 {
  color: #3498db;
  font-size: 24px;
  margin-bottom: 20px;
}

h2 {
  color: #c184e9;
  /* test */
  text-align: center;
}

#create-deck-button {
  background-color: #3498db;
  color: #fff;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  font-size: 18px;
  cursor: pointer;
  position: relative;
}

button:hover {
  background-color: #2580b3;
}

.v-alert {
  margin-bottom: 20px;
  border-radius: 5px;
}

.created-decks-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 100%;
  position: relative;
}

.deck-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
  width: 100%;
}

.deck-item {
  display: flex;
  flex-direction: column; /* arrange children vertically */
  justify-content: space-between; /* distribute space between children */
  text-align: center;
  padding: 10px;
  border: 1px solid #ccc;
  background-color: rgb(255, 255, 253);
  box-shadow: 10px 5px 5px #ccc;
  border-radius: 5px;
  margin-bottom: 20px;
  margin-left: 5px;
  margin-right: 5px;
  height: 175px;
  width: 285px;
  flex: 1;
  max-width: calc(100% - 10px);
}

.cover-image img {
  max-width: 275px;
  max-height: 150px;
  margin-top: 10px;
}

#welcome img {
  display: block;
  margin: 0 auto; /* Centers the image */
  max-width: 1000px; /* You can adjust this value as per your requirement */
}

#created-decks-header {
  background-color: #f5e5fa; /* Set a background color for better visibility */
  padding: 10px 0; /* Add padding for better spacing */
  position: sticky;
  top: 0;
  z-index: 1;
  width: 100%; /* Take up the full width of the container */
}
</style>
