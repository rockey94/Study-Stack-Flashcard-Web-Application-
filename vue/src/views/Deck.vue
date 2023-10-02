<template>
  <div id="deck">
    <!-- VERTICAL CARD SELECTION BOX -->
    <div id="card-selection">
      <button id="new-flashcard-button" v-on:click="createCard">
        New Flashcard
      </button>
      <div class="card-scroll-container">
        <div
          class="card-preview"
          v-for="card in cards"
          :key="card.cardId"
          v-on:click="viewCard(card)"
        >
          {{ card.cardId }}. {{ card.question }}
        </div>
      </div>
    </div>
    <!-- HOME BUTTON -->
    <button
      id="home-button"
      v-if="$store.state.token != ''"
      @click="backToHome"
    >
      Back to Home
    </button>
    <img id="s-logo" src="../../public/S-logo.png" alt="Study Stack logo" />
    <!-- MAIN SECTION -->
    <section id="main-section">
      <div id="white-box">
        <!-- DELETE DECK MODAL -->
        <div
          id="delete-modal"
          class="modal"
          ref="deleteModal"
          v-if="deleteMessage"
        >
          <p>Are you sure you want to delete this stack?</p>
          <div class="modal-buttons">
            <button id="confirm-delete" @click="confirmDelete">
              Yes, delete!
            </button>
            <button
              id="cancel-delete"
              @click="cancelDelete"
              v-on:click="deleteMessage = false"
            >
              No, keep it.
            </button>
          </div>
        </div>
        <!-- PURPLE DECK INFO BOX -->
        <div v-show="!deleteMessage" id="deck-info">
          <img
            id="cover-image"
            v-if="deck.coverImage"
            :src="deck.coverImage"
            alt="Deck Cover"
          />
          <div id="name-description">
            <img
              class="pencil"
              id="edit-deck-button"
              src="../../public/pencil-color.png"
              alt="Edit Button"
              @click="startEditing"
            />
            <div v-if="!isEditing">
              <h1>{{ deck.name }}</h1>
              <p>{{ deck.description }}</p>
            </div>
            <div v-show="isEditing">
              <input
                type="text"
                v-model="deck.name"
                @keyup.enter="saveChanges"
                placeholder="Study Stack"
              />
              <input
                type="text"
                v-model="deck.coverImage"
                @keyup.enter="saveChanges"
                placeholder="http://example.png"
              />
              <input
                type="text"
                v-model="deck.description"
                @keyup.enter="saveChanges"
                placeholder="Study Stack is Awesome!"
              />
              <div id="savebutton">
                <button @click="editDeck(deck.deckId, deck)">Save</button>
                <button class="cancel" @click="cancelEditing">Cancel</button>
              </div>
            </div>
          </div>
        </div>
        <!-- BLUE CURRENT CARD -->
        <div
          class="card-flip-container"
          @click="flipCard"
          ref="cardFlipContainer"
        >
          <div id="current-card">
            <div class="flipper">
              <div class="front">
                <p>{{ currentCard.question }}</p>
              </div>
              <div class="back">
                <p>{{ currentCard.answer }}</p>
              </div>
            </div>
          </div>
          <!-- DELETE DECK BUTTON -->
          <img
            class="trash-can"
            id="delete-deck-button"
            src="../../public/trash-can.png"
            alt="Delete Button"
            @click="deleteDeck"
            v-on:click="deleteMessage = true"
          />
        </div>
      </div>
      <!-- ALL FLASHCARDS TABLE -->
      <div id="flashcards-table" v-if="cards.length > 0">
        <h2>Stack Flashcards</h2>
        <table>
          <tr>
            <th>Front:</th>
            <th>Back:</th>
          </tr>
          <tr class="card" v-for="card in cards" :key="card.cardId">
            <td class="card-content">{{ card.question }}</td>
            <td class="card-content">{{ card.answer }}</td>
            <td class="button-cell">
              <img
                class="pencil"
                id="edit-card-button"
                src="../../public/pencil-color.png"
                alt="Edit Card Button"
                @click="editFlashCard(card.deckId, card.cardId, card)"
              />
            </td>
            <td class="button-cell">
              <img
                class="trash-can"
                id="delete-card-button"
                src="../../public/trash-can.png"
                alt="Delete Card Button"
                @click="deleteFlashCard(card.deckId, card.cardId, card)"
              />
            </td>
          </tr>
        </table>
        <br /><br /><br />
      </div>
    </section>
  </div>
</template>
<script>
import CardService from "../services/CardService";
import DeckService from "../services/DeckService";
export default {
  data() {
    return {
      originalDeck: null,
      originalCard: null,
      currentCard: {
        cardId: -1,
        deckId: -1,
        question: "Create a flashcard to get started!",
        answer: "Create a flashcard to get started!",
        keyword: "",
        creator: -1,
      },
      deck: {
        deckId: -1,
        name: "",
        creator: -1,
        coverImage: "",
        description: "",
      },
      newCard: {
        cardId: -1,
        deckId: -1,
        question: "Front of the card. Double click to type.",
        answer: "Back of the card. Double click to type.",
        keyword: "",
        creator: -1,
      },
      cards: [],
      isEditing: false,
      maxLengthMessage: "",
      deleteMessage: false,
    };
  },
  methods: {
    flipCard() {
      console.log("flipCard method triggered!"); // This is the line to add
      this.$refs.cardFlipContainer.classList.toggle("flipped");
    },
    viewCard(card) {
      console.log("You clicked on card #: " + card.cardId);
      this.currentCard = card;
      console.log("The current card is card #: " + card.cardId);
    },
    backToHome() {
      this.$router.push("/");
    },

    createCard() {
      const question = prompt("Front of the new flashcard:");
      const answer = prompt("Back of the new flashcard:");
      const keyword = prompt(
        "If desired, add a keyword (such as math, science, etc.):"
      );

      // Check if the user provided both question and answer
      if (question && answer) {
        this.newCard.creator = this.$store.state.user.id;
        this.newCard.deckId = this.$route.params.deckId;
        this.newCard.question = question;
        this.newCard.answer = answer;
        this.newCard.keyword = keyword;
        CardService.createCard(this.newCard)
          .then((response) => {
            if (response.status === 200) {
              console.log("Card created");
              // Fetch the updated list of cards after the new card has been added
              this.currentCard = this.newCard;
              this.viewCardsByDeckId();
            }
          })
          .catch((error) => {
            if (error.response) {
              console.log(error);
            }
          });
      } else {
        // Handle case where user didn't provide question or answer
        alert("Both front and back are required for the flashcard.");
      }
    },
    startEditing() {
      this.originalDeck = Object.assign({}, this.deck); // deep copy of the deck object
      this.isEditing = true;
    },
    cancelEditing() {
      this.deck = Object.assign({}, this.originalDeck);
      this.isEditing = false;
    },
    editDeck(deckId, deck) {
      // Call the backend API to delete the deck
      DeckService.editDeck(deckId, deck)
        .then(() => {
          // Re-fetch the updated deck list from the backend
          location.reload();
        })
        .catch((error) => {
          console.error("Error deleting deck:", error);
        });
    },
    editFlashCard(deckId, cardId, card) {
      const newQuestion = window.prompt(
        "Enter the new question:",
        card.question
      );
      const newAnswer = window.prompt("Enter the new answer:", card.answer);
      // If the user provided new question and answer, update the card
      const newKeyword = window.prompt("Enter the new keyword:", card.keyword);
      if (newQuestion !== null && newAnswer !== null) {
        // If the user provided new question and answer, update the card
        const updatedCard = {
          ...card,
          question: newQuestion,
          answer: newAnswer,
          keyword: newKeyword,
        };
        // Call the backend API to edit the flashcard
        CardService.editCard(deckId, cardId, updatedCard)
          .then(() => {
            // Re-fetch the updated card list from the backend
            this.viewCardsByDeckId();
          })
          .catch((error) => {
            console.error("Error editing flashcard:", error);
          });
      } else {
        // Handle the case where the user cancels the prompt
        console.log("Edit operation cancelled by the user.");
      }
    },
    deleteFlashCard(deckId, cardId, card) {
      const confirmDelete = confirm(
        "Are you sure you want to delete this flashcard?"
      );
      if (confirmDelete) {
        CardService.deleteCard(deckId, cardId, card)
          .then(() => {
            // Flashcard successfully deleted, you might want to update the UI or fetch the updated card list.
            this.viewCardsByDeckId();
          })
          .catch((error) => {
            console.error("Error deleting flashcard:", error);
          });
      }
    },
    viewCardsByDeckId() {
      CardService.viewCardsByDeckId(this.$route.params.deckId)
        .then((response) => {
          this.cards = response.data;
        })
        .catch((error) => {
          console.error("Error fetching cards by deckId:", error);
        });
    },
    deleteDeck(event) {
      event.stopPropagation();
      this.showDeleteConfirmation();
    },
    showDeleteConfirmation() {
      this.$refs.deleteModal.style.display = "block";
    },
    hideDeleteConfirmation() {
      this.$refs.deleteModal.style.display = "none";
    },
    confirmDelete() {
      // Call the backend API to delete the deck
      DeckService.deleteDeck(this.deck.deckId)
        .then(() => {
          // Re-fetch the updated deck list from the backend
          this.hideDeleteConfirmation();
          this.$router.push("/");
        })
        .catch((error) => {
          console.error("Error deleting deck:", error);
        });
    },
    cancelDelete() {
      // Hide the confirmation modal if the user cancels the delete
      this.hideDeleteConfirmation();
      this.deleteMessage = false;
    },
  },
  watch: {
    "card.question"(value) {
      if (value.length === 30) {
        this.maxLengthMessage = "You can enter 99 characters only.";
      } else {
        this.maxLengthMessage = "";
      }
    },
    "card.answer"(value) {
      if (value.length === 30) {
        this.maxLengthMessage = "You can enter 99 characters only.";
      } else {
        this.maxLengthMessage = "";
      }
    },
  },
  created() {
    this.deck.deckId = this.$route.params.deckId;
    DeckService.getDeckById(this.deck.deckId).then((response) => {
      this.deck.name = response.data.name;
      this.deck.creator = response.data.creator;
      this.deck.coverImage = response.data.coverImage;
      this.deck.description = response.data.description;
      if (this.cards.length > 0) {
        this.CurrentCard = this.cards[0];
      }
    });
    this.viewCardsByDeckId();
  },
  computed: {
    displayedCards() {
      return this.cards.slice(0, 10);
    },
  },
};
</script>
<style scoped>
#deck {
  background-color: #cdecd7;
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  align-items: center;
  min-height: 100vh;
  font-family: Verdana;
  padding-left: 20px; /* Add some padding to the left to prevent #card-selection sticking directly to the edge */
  height: 100%;
}
#card-selection {
  width: 300px;
  height: 85vh;
  border: 1px solid #ccc;
  background-color: rgb(255, 255, 253);
  box-shadow: 10px 5px 5px #ccc;
  border-radius: 15px;
  margin: 20px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  max-height: 1050px; /* Height to decent amount of 22cards */
  align-self: flex-start;
  margin-top: 100px;
}
.card-preview {
  border: 1px solid #ccc;
  border-radius: 5px;
  box-shadow: 4px 2px 2px #ccc;
  margin: 10px;
  background-color: #ceeafc;
  padding: 15px;
  width: 200px;
  height: 75px;
  cursor: pointer; /* This line changes the cursor to a hand pointer */
}
.card-scroll-container {
  overflow-y: auto;
}
#white-box {
  width: 900px;
  height: fit-content;
  padding-top: 50px;
  border: 1px solid #ccc;
  background-color: rgb(255, 255, 253);
  box-shadow: 10px 5px 5px #ccc;
  border-radius: 15px;
  margin-left: 20px;
  margin-right: 20px;
  margin-top: 100px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
#deck-info {
  height: fit-content;
  width: fit-content;
  background-color: #f5e5fa;
  padding-left: 20px;
  padding-right: 20px;
  padding-bottom: 20px;
  border-radius: 15px;
  margin-bottom: 50px;
  display: flex;
  align-items: center;
}
#name-description {
  display: flex;
  flex-direction: column;
  align-items: center;
}
#cover-image {
  max-height: 100px;
  max-width: 200px;
  margin-top: 10px;
  padding-right: 20px;
}
.pencil {
  max-width: 20px;
  cursor: pointer;
}
.pencil:hover {
  background-color: #f1f1f1;
  border-radius: 100%;
  transition: background-color 0.3s ease;
}
#edit-deck-button {
  margin-left: 95%;
  padding-top: 10px;
}
#current-card {
  width: 100%;
  height: 100%;
  transition: transform 0.6s;
  transform-style: preserve-3d;
  border-radius: 15px;
  background-color: #ceeafc;
  box-shadow: 10px 5px 5px #ccc;
}
h1 {
  color: #c184e9;
  text-align: center;
}
h2 {
  color: #3498db;
  text-align: center;
  margin-top: 50px;
}
h3 {
  margin-right: 5px;
}
table {
  width: 80%;
  border-radius: 10px;
  background-color: rgb(255, 255, 253);
  box-shadow: 10px 5px 5px #ccc;
  border-collapse: collapse;
}
th {
  padding: 10px;
  text-align: left;
  width: 50%;
}
td {
  padding: 10px;
  border-bottom: 1px solid #fff;
  width: 50%;
}
button {
  background-color: #3498db;
  color: #fff;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  font-size: 18px;
  cursor: pointer;
}
#home-button {
  background-color: #c184e9;
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
#savebutton button {
  font-size: 14px;
  border-radius: 3px;
  padding: 5px 10px;
  margin-top: 30px;
  margin-left: 500px;
  transition: background-color 0.3s ease;
}
#savebutton button:hover {
  background-color: #2e7db2;
  color: white;
}
#savebutton button.cancel {
  background-color: grey;
  color: white;
  margin-left: 10px;
  transition: background-color 0.3s ease;
}
#savebutton button.cancel:hover {
  background-color: red;
}
#s-logo {
  position: absolute;
  top: 20px;
  right: 20px;
  height: 100px;
}
#test-image img {
  max-height: 10px;
  max-width: 100px;
}
.card-flip-container {
  perspective: 1000px;
  width: 750px; /* Width of #current-card */
  height: 350px; /* Height of #current-card */
  cursor: pointer; /* Make it obvious it can be clicked */
  margin-bottom: 100px;
  margin-right: 50px;
  margin-left: 50px;
}
.flipper {
  width: 100%;
  height: 100%;
  transition: transform 0.6s;
  transform-style: preserve-3d;
}
.front,
.back {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  border-radius: 15px; /* Retain rounded corners when flipped */
  display: flex; /* Center content */
  align-items: center;
  justify-content: center;
}
.front {
  z-index: 2;
}
.back {
  transform: rotateY(180deg);
}
.card-flip-container.flipped #current-card {
  transform: rotateY(180deg);
}
.trash-can {
  max-width: 35px;
  cursor: pointer;
}
#delete-deck-button {
  align-self: flex-end;
  padding-left: 103%;
  padding-top: 45px;
}
#delete-modal {
  text-align: center;
  padding-left: 20px;
  padding-right: 20px;
  padding-bottom: 20px;
  border-radius: 15px;
  margin-bottom: 50px;
  background-color: #f5e5fa;
}
.modal-buttons {
  font-size: 10px;
  padding: 8px;
  cursor: pointer;
  display: flex;
  justify-content: center;
}
#confirm-delete {
  background-color: #dd7f5a;
  margin-right: 30px;
}
#cancel-delete {
  background-color: #3498db;
}
#flashcards-table {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex-grow: 1;
  margin-left: 20px;
  margin-right: 20px;
}
table {
  width: 900px;
}
.button-cell {
  text-align: right;
  padding-right: 10px;
}
#delete-card-button {
  max-width: 28px;
}
</style>
