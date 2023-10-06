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
          <div class="deck-item" v-for="deck in decks" :key="deck.deck_id">
            <div class="flipper">
              <!-- Front side -->
              <div class="front">
                <div class="deck-title">{{ deck.name }}</div>
                <div v-if="deck.coverImage" class="cover-image">
                  <img :src="deck.coverImage" alt="Deck Cover" />
                </div>
                <div v-else class="cover-image">
                  <img src="default-cover-image.png" alt="Deck Cover" />
                </div>
              </div>
              <!-- Back side -->
              <div class="back">
                <button id="edit-button" @click="goToDeck(deck.deckId)">
                  Edit
                </button>
                <button
                  v-if="deck.cards.length > 0"
                  id="study-button"
                  @click="determineIfStudyable(deck.deckId)"
                >
                  Study
                </button>
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
import CardService from "../services/CardService";
import DeckService from "../services/DeckService";

export default {
  name: "home",
  data() {
    return {
      decks: [],
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
          this.decks = response.data.map((deck) => ({
            ...deck,
            hover: false,
            cards: [], // Initialize the cards array for each deck
          }));

          // Fetch cards for each deck and update the cards property
          this.decks.forEach((deck) => {
            CardService.viewCardsByDeckId(deck.deckId)
              .then((response) => {
                deck.cards = response.data;
              })
              .catch((error) => {
                console.error("Error fetching cards for deck:", error);
              });
          });
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
    },
    determineIfStudyable(deckId) {
      CardService.viewCardsByDeckId(deckId).then((response) => {
        const cards = response.data;
        if (cards.length == 0) {
          window.alert("No flashcards to study! Edit the stack first.");
        } else {
          this.goToStudySession(deckId);
        }
      });
    },
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

.deck-container {
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  height: 100%;
}

#edit-button,
#study-button {
  color: #fff;
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
}

#edit-button {
  margin-bottom: 10px;
  background-color: #3498db;
}

#edit-button:hover {
  background-color: #2e7db2;
}

#study-button {
  background-color: #66c985;
}

#study-button:hover {
  background-color: #32b65c;
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

#logout-button:hover {
  background-color: #dd7f5a;
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

#create-deck-button:hover {
  background-color: #2e7db2;
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
  perspective: 1000px;
  position: relative;
  width: 285px;
  height: 175px;
  margin: 10px; /* Apply margin as needed */
  margin-bottom: 50px;
}

.flipper {
  width: 100%;
  height: 100%;
  transition: transform 0.6s;
  transform-style: preserve-3d;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #ccc;
  border-radius: 5px;
}

.deck-item .front,
.deck-item .back {
  backface-visibility: hidden;
  position: absolute;
  top: 0;
  left: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 15px;
  border: 1px solid #ccc;
  background-color: rgb(255, 255, 253);
  box-shadow: 10px 5px 5px #ccc;
  border-radius: 5px;
  width: 100%;
  height: 100%;
  transform: translateX(-5%) translateY(-3%);
}

.deck-item .back {
  transform: rotateY(180deg) translateX(5%) translateY(-3%);
  border: 1px solid #ccc;
  background-color: rgb(255, 255, 253);
  box-shadow: 10px 5px 5px #ccc;
}

.deck-item:hover .flipper {
  transform: rotateY(180deg);
}

.cover-image img {
  max-width: 185px;
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
