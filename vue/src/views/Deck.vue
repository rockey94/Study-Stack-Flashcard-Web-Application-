<template>
  <div id="deck">
    <!-- VERTICAL CARD SELECTION BOX -->
    <div id="card-selection">
      <button id="new-flashcard-button" v-on:click="openModal">
        New Flashcard
      </button>
      <div class="card-preview-scroll">
        <div
          class="card-preview"
          v-for="card in cards"
          :key="card.cardId"
          v-on:click="viewCard(card)"
        >
          {{
            card.question.length > 50
              ? card.question.substring(0, 50) + "..."
              : card.question
          }}
        </div>
      </div>
    </div>

    <!-- HOME BUTTON AND LOGO -->
    <button
      id="home-button"
      v-if="$store.state.token != ''"
      @click="backToHome"
    >
      Back to Home
    </button>
    <img id="s-logo" src="../../public/S-logo.png" alt="Study Stack logo" />
    <button
      id="hidden-keyword"
      class="tooltip"
      :data-tooltip="tooltipContent"
    ></button>
    <!-- CREATE FLASHCARD MODAL -->
    <div v-if="showCreateCardModal" class="modal-overlay">
      <div class="modal-content">
        <h3>Create a New Flashcard</h3>
        <div class="modal-input-group">
          <label for="question">Front: </label>
          <input
            type="text"
            v-model="newCard.question"
            placeholder="Enter the front of the flashcard..."
          />
        </div>
        <div class="modal-input-group">
          <label for="answer">Back: </label>
          <input
            type="text"
            v-model="newCard.answer"
            placeholder="Enter the back of the flashcard..."
          />
        </div>
        <div class="modal-input-group">
          <label for="keyword">Keyword: </label>
          <input
            type="text"
            v-model="newCard.keyword"
            placeholder="Enter a keyword (optional)..."
          />
        </div>
        <div class="modal-actions">
          <button id="create-button" @click="createCardFromModal">
            Create
          </button>
          <button id="cancel-create-button" @click="closeModal">Cancel</button>
        </div>
      </div>
    </div>
    <!-- DELETE DECK MODAL -->
    <div
      id="delete-modal"
      class="modal-overlay"
      ref="deleteModal"
      v-if="deleteMessage"
    >
      <div class="modal-content">
        <p>Are you sure you want to delete this stack?</p>
        <div class="modal-buttons">
          <button class="confirm-delete" @click="confirmDelete">
            Yes, delete!
          </button>
          <button
            class="cancel-delete"
            @click="cancelDelete"
            v-on:click="deleteMessage = false"
          >
            No, keep it.
          </button>
        </div>
      </div>
    </div>
    <!-- MAIN SECTION -->
    <section id="main-section">
      <div id="white-box">
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
              @click="startEditingDeck"
              v-show="!isEditingDeck"
            />
            <div v-if="!isEditingDeck">
              <h1>{{ deck.name }}</h1>
              <p>{{ deck.description }}</p>
            </div>
            <div id="deck-edit" v-show="isEditingDeck">
              <div class="deck-edit-input">
                <label for="name">Name: </label>
                <input
                  type="text"
                  maxlength="30"
                  v-model="deck.name"
                  @keyup.enter="saveChanges"
                  @keyup.escape="cancelEditingDeck"
                  placeholder="Study Stack"
                />
                <label for="cover-image">Cover Image URL: </label>
                <input
                  type="text"
                  v-model="deck.coverImage"
                  @keyup.enter="saveChanges"
                  @keyup.escape="cancelEditingDeck"
                  placeholder="http://example.png"
                />
                <label for="description">Description: </label>
                <input
                  type="text"
                  v-model="deck.description"
                  @keyup.enter="saveChanges"
                  @keyup.escape="cancelEditingDeck"
                  placeholder="Study Stack is Awesome!"
                />
              </div>
              <div id="savebutton">
                <button id="save" @click="editDeck(deck.deckId, deck)">
                  Save
                </button>
                <button class="cancel" @click="cancelEditingDeck">
                  Cancel
                </button>
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
            <th class="card-content">Front:</th>
            <th class="card-content">Back:</th>
            <th class="keywords-cell">Keywords:</th>
            <th class="button-cell"></th>
            <th class="button-cell"></th>
          </tr>
          <tr
            class="card"
            v-for="card in cards"
            :key="card.cardId"
            :class="{ 'highlighted-row': card === cardToDelete }"
          >
            <td class="card-content" v-show="!card.isEditing">
              {{ card.question }}
            </td>
            <td class="card-content" v-show="card.isEditing">
              <div class="growable-container">
                <textarea
                  class="growable-text"
                  v-model="card.question"
                  @keydown.enter.prevent="saveChanges(card)"
                  @keyup.escape="cancelEditing(card)"
                />
              </div>
            </td>
            <td class="card-content" v-show="!card.isEditing">
              {{ card.answer }}
            </td>
            <td class="card-content" v-show="card.isEditing">
              <div class="growable-container">
                <textarea
                  class="growable-text"
                  v-model="card.answer"
                  @keydown.enter.prevent="saveChanges(card)"
                  @keyup.escape="cancelEditing(card)"
                />
              </div>
            </td>
            <td class="card-content" v-show="!card.isEditing">
              {{ card.keyword }}
            </td>
            <td class="keywords-cell" v-show="card.isEditing">
              <div class="growable-container">
                <textarea
                  class="growable-text"
                  v-model="card.keyword"
                  @keydown.enter.prevent="saveChanges(card)"
                  @keyup.escape="cancelEditing(card)"
                />
              </div>
            </td>

            <td class="button-cell">
              <img
                class="pencil"
                id="edit-card-button"
                src="../../public/pencil-color.png"
                alt="Edit Card Button"
                @click="startEditingCard(card)"
                v-if="!card.isEditing"
              />
            </td>
            <td class="button-cell">
              <img
                class="trash-can"
                id="delete-card-button"
                src="../../public/trash-can.png"
                alt="Delete Card Button"
                @click="
                  cardToDelete = card;
                  deleteCardMessage = true;
                "
              />
            </td>
            <!-- DELETE CARD MODAL -->
            <div
              id="delete-card-modal"
              class="card-modal-overlay"
              ref="deleteCardModal"
              v-if="deleteCardMessage"
            >
              <div class="modal-content">
                <p>Are you sure you want to delete this flashcard?</p>
                <div class="modal-buttons">
                  <button
                    class="confirm-delete"
                    @click="
                      deleteFlashCard(
                        cardToDelete.deckId,
                        cardToDelete.cardId,
                        cardToDelete
                      )
                    "
                  >
                    Yes, delete!
                  </button>
                  <button
                    class="cancel-delete"
                    @click="cancelDeleteCard"
                    v-on:click="
                      deleteCardMessage = false;
                      cardToDelete = null;
                    "
                  >
                    No, keep it.
                  </button>
                </div>
              </div>
            </div>
          </tr>
        </table>
        <br /><br /><br />
      </div>
      <div id="study-button-div">
        <button
          id="study-button"
          v-if="cards.length > 0"
          v-on:click="goToStudySession(deck.deckId)"
        >
          Study this Stack
        </button>
      </div>
    </section>
    <!-- VERTICAL CARD SEARCH BOX -->
    <div id="card-search">
      <div class="search-header">
        <!-- This is the non-scrollable search bar area -->
        <p>
          Search All Flashcards by
          <span>Keyword</span>:
        </p>
        <input
          type="text"
          v-model="searchKeyword"
          placeholder="Search by keyword..."
        />
        <br /><br /><small>Double click to add card to your stack:</small>
      </div>
      <!-- Scrollable Search Results Div -->
      <div class="search-preview-scroll">
        <div v-if="filteredCards.length > 0">
          <div
            class="search-card-preview"
            v-for="card in filteredCards"
            :key="`${card.deckId}-${card.cardId}`"
            @click="viewCard(card)"
            @dblclick.stop.prevent="handleDoubleClick(card)"
          >
            <div class="flipper">
              <div class="front">
                <p>
                  {{
                    card.question.length > 50
                      ? card.question.substring(0, 50) + "..."
                      : card.question
                  }}
                </p>
              </div>
              <div class="back">
                {{
                  card.answer.length > 50
                    ? card.answer.substring(0, 50) + "..."
                    : card.answer
                }}
              </div>
            </div>
          </div>
        </div>
        <div v-else>No matching cards found.</div>
      </div>
    </div>
  </div>
</template>
<script>
import CardService from "../services/CardService";
import DeckService from "../services/DeckService";

export default {
  data() {
    return {
      keywords: [],
      showCreateCardModal: false,
      tooltipContent: "", // Initialize tooltip content as an empty string
      searchKeyword: "",
      keyword: "",
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
        question: "",
        answer: "",
        keyword: "",
        creator: -1,
      },
      cards: [],
      allCards: [],
      isEditingDeck: false,
      deleteMessage: false,
      deleteCardMessage: false,
      cardToDelete: null,
    };
  },
  methods: {
    fetchKeywords() {
      // Extract keywords from the cards and convert the array to a Set
      const keywordsSet = new Set(
        this.allCards.map((card) => card.keyword).filter(Boolean)
      );

      // Convert the Set back to an array (if needed) to maintain consistency in your data structure
      this.keywords = Array.from(keywordsSet);

      // Update tooltipContent with the comma-separated keywords
      if (this.keywords.length > 2) {
        const suggestions = this.keywords.slice(0, 3);
        this.tooltipContent = suggestions.join(", ") + ", ...";
      } else {
        this.tooltipContent = this.keywords.join(", ");
      }
    },
    resetCardForm() {
      this.newCard = {
        question: "",
        answer: "",
        keyword: "",
      };
    },
    handleDoubleClick(card) {
      // Handle double-click functionality here
      this.deck.deckId = this.$route.params.deckId;

      // Call the createCard() method and pass the clicked card's data
      this.createCardFromDoubleClick(card);
    },
    createCardFromDoubleClick(cardData) {
      const question = cardData.question;
      const answer = cardData.answer;
      const keyword = cardData.keyword;
      // Check if the extracted question and answer are not empty

      const newCardData = {
        creator: this.$store.state.user.id,
        deckId: this.$route.params.deckId,
        question: question,
        answer: answer,
        keyword: keyword || "", // Set keyword to empty string if user cancels the prompt
      };

      // Call the CardService.createCard() method with newCardData
      CardService.createCard(newCardData)
        .then((response) => {
          if (response.status === 200) {
            console.log("Card created");
            // Fetch the updated list of cards after the new card has been added
            this.viewCardsByDeckId();
            location.reload();
          }
        })
        .catch((error) => {
          console.error("Error creating card:", error);
        });
    },

    async addKeyword(deckId, cardId) {
      const newKeyword = window.prompt("Enter the new keyword:");

      // If the user provided a new keyword, update the keyword property
      if (newKeyword !== null) {
        this.keyword = newKeyword; // Update the keyword property with the new value

        try {
          // Call the backend API to add the keyword
          const response = await CardService.addKeyword(
            deckId,
            cardId,
            this.keyword
          );

          // Handle the response as needed
          console.log(response.data); // You can log the response or perform other actions

          // Re-fetch the updated card list from the backend
          this.viewCardsByDeckId();
        } catch (error) {
          // Handle any errors that occur during the request
          console.error("Error adding keyword:", error);
        }
      } else {
        // Handle the case where the user cancels the prompt
        console.log("Add keyword operation cancelled by the user.");
      }
    },
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
    openModal() {
      this.showCreateCardModal = true;
    },
    closeModal() {
      this.showCreateCardModal = false;
    },
    createCardFromModal() {
      if (this.newCard.question && this.newCard.answer) {
        // This will use the existing createCard functionality but from the modal.
        this.createCard();
        this.resetCardForm();
        this.showCreateCardModal = false;
        location.reload();
      } else {
        alert("Both front and back are required for the flashcard.");
      }
    },
    createCard() {
      if (this.newCard.question && this.newCard.answer) {
        this.newCard.creator = this.$store.state.user.id;
        this.newCard.deckId = this.$route.params.deckId;
        CardService.createCard(this.newCard)
          .then((response) => {
            if (response.status === 200) {
              console.log("Card created");
              // Fetch the updated list of cards after the new card has been added
              this.currentCard = this.newCard;
              this.viewCardsByDeckId();
              CardService.getAllCards()
                .then((response) => {
                  this.allCards = response.data;
                  // Extract keywords from the cards and join them into a comma-separated string
                  this.fetchKeywords();
                })
                .catch((error) => {
                  console.error("Error fetching all cards:", error);
                });
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
    goToStudySession(deckId) {
      this.$router.push("/study/" + deckId);
    },
    startEditingDeck() {
      this.originalDeck = Object.assign({}, this.deck); // deep copy of the deck object
      this.isEditingDeck = true;
    },
    cancelEditingDeck() {
      this.deck = Object.assign({}, this.originalDeck);
      this.isEditingDeck = false;
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
    startEditingCard(card) {
      this.originalCard = JSON.parse(JSON.stringify(card)); // Deep copy of the card object
      card.isEditing = true;
    },
    cancelEditing(card) {
      Object.assign(card, this.originalCard);
      card.isEditing = false;
    },
    saveChanges(card) {
      const excess = card.question.length - 1000;
      if (card.question.length > 1000) {
        alert(
          "Front of the card is " +
            excess +
            " characters too long. You can enter 1000 characters only."
        );
        return;
      }

      if (card.answer.length > 1000) {
        alert(
          "Back of the card is " +
            excess +
            " characters too long. You can enter 1000 characters only."
        );
        return;
      }

      CardService.editCard(card.deckId, card.cardId, card)
        .then(() => {
          // Update the local cards array directly without refetching from the server
          const index = this.cards.findIndex((c) => c.cardId === card.cardId);
          if (index !== -1) {
            // Update the card in the local array
            this.$set(this.cards, index, card);
          }
          // Reset the card's editing state
          card.isEditing = false;

          CardService.getAllCards().then((response) => {
            this.allCards = response.data;
            // Extract keywords from the cards and join them into a comma-separated string
            this.fetchKeywords();
          });
        })
        .catch((error) => {
          console.error("Error editing flashcard:", error);
        });
    },
    deleteFlashCard(deckId, cardId, card) {
      CardService.deleteCard(deckId, cardId, card)
        .then(() => {
          // Successfully deleted the card, now update this.cards

          CardService.viewCardsByDeckId(deckId)
            .then((response) => {
              this.currentCard = {
                cardId: -1,
                deckId: -1,
                question: "Create a flashcard to get started!",
                answer: "Create a flashcard to get started!",
                keyword: "",
                creator: -1,
              };
              this.cards = response.data;
              this.allCards = response.data;
              if (this.cards.length > 0) {
                this.currentCard = this.cards[0];
              } else {
                this.currentCard = {
                  cardId: -1,
                  deckId: -1,
                  question: "Create a flashcard to get started!",
                  answer: "Create a flashcard to get started!",
                  keyword: "",
                  creator: -1,
                };
              }
              // Fetch all cards
              CardService.getAllCards()
                .then((response) => {
                  this.allCards = response.data;
                  // Extract keywords from the cards and join them into a comma-separated string
                  this.fetchKeywords();
                })
                .catch((error) => {
                  console.error("Error fetching all cards:", error);
                });

              // Update tooltipContent with the updated keywords
              this.fetchKeywords();

              // Get rid of the delete card modal
              this.deleteCardMessage = false;
            })
            .catch((error) => {
              console.error("Error fetching updated card list:", error);
            });
        })
        .catch((error) => {
          console.error("Error deleting flashcard:", error);
        });
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
    cancelDeleteCard() {
      // Hide the confirmation modal if the user cancels the delete
      this.hideDeleteConfirmation();
      this.deleteCardMessage = false;
    },
  },
  created() {
    this.deck.deckId = this.$route.params.deckId;

    // Fetch the deck based on the route parameter
    DeckService.getDeckById(this.deck.deckId)
      .then((response) => {
        this.deck.name = response.data.name;
        this.deck.creator = response.data.creator;
        this.deck.coverImage = response.data.coverImage;
        this.deck.description = response.data.description;
      })
      .catch((error) => {
        console.error("Error fetching deck:", error);
      });

    // Fetch the cards for the deck
    CardService.viewCardsByDeckId(this.deck.deckId)
      .then((response) => {
        this.cards = response.data.map((card) => ({
          ...card,
          isEditing: false,
        }));

        // Set currentCard to the first card in the cards array
        if (this.cards.length > 0) {
          this.currentCard = this.cards[0];
        } else {
          // Handle the case where no cards are found for the deck
          console.warn("No cards found in the deck.");
        }
      })
      .catch((error) => {
        console.error("Error fetching cards by deckId:", error);
      });

    // Fetch all cards
    CardService.getAllCards()
      .then((response) => {
        this.allCards = response.data;

        // Extract keywords from the cards and convert the array to a Set
        this.fetchKeywords();
      })
      .catch((error) => {
        console.error("Error fetching all cards:", error);
      });
  },
  computed: {
    filteredCards() {
      if (this.searchKeyword.trim() === "") {
        // If searchKeyword is empty, filter and return unique cards by question and answer
        return this.allCards.filter(
          (card, index, self) =>
            index ===
            self.findIndex(
              (c) =>
                c.question === card.question &&
                c.answer === card.answer &&
                c.keyword === card.keyword
            )
        );
      } else {
        // If searchKeyword is not empty, filter and return unique cards by keyword, question, and answer
        const keyword = this.searchKeyword.toLowerCase().trim();
        return this.allCards.filter(
          (card, index, self) =>
            card.keyword.toLowerCase().includes(keyword) &&
            index ===
              self.findIndex(
                (c) => c.question === card.question && c.answer === card.answer
              )
        );
      }
    },
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
  justify-content: centert;
  align-items: center;
  min-height: 100vh;
  font-family: Verdana;
  padding-left: 20px;
  height: 100%;
}
#new-flashcard-button {
  width: 100%;
  height: 50px;
  margin-top: 10px;
  z-index: 100;
  align-self: flex-start;
  position: sticky;
  top: 0; /* Stick to the top of the container */
  z-index: 1; /* Ensure the button appears above the cards */
}
#new-flashcard-button:hover {
  background-color: #2e7db2;
}
#card-selection {
  width: 325px;
  height: 85vh;
  border: 1px solid #ccc;
  background-color: rgb(255, 255, 253);
  box-shadow: 10px 5px 5px #ccc;
  border-radius: 15px;
  margin: 20px;
  padding: 10px;
  padding-left: 17px !important;
  padding-right: 17px !important;
  display: flex;
  flex-direction: column;
  align-items: center;
  max-height: 1050px;
  align-self: flex-start;
  margin-top: 100px;
  overflow-y: auto;
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
  cursor: pointer;
  margin-bottom: 10px;
  margin-right: -5;
  margin-left: 5;
  flex-shrink: 0;
  margin-right: 40px;
  align-items: center;
  display: flex;
  flex-grow: 1;
  flex-direction: column;
  justify-content: center;
  transition: box-shadow 0.3s ease, transform 0.3s ease;
}

.card-preview:hover {
  box-shadow: 6px 4px 4px #ccc; /* Increase the box shadow to make it look like it's coming to the foreground */
  transform: scale(
    1.05
  ); /* Scale the element slightly to create a zoom effect */
}

#hidden-keyword {
  position: absolute;
  width: 75px;
  height: 25px;
  top: 132px;
  right: 202px;
  background: none;
  /* z-index: -1; */
}
.search-card-preview {
  position: relative;
  width: 200px; /* Set the width of the card preview */
  height: 75px; /* Set the height of the card preview */
  cursor: pointer;
  margin-top: 10px;
  perspective: 1000px;
}
.search-card-preview .flipper {
  max-height: calc(
    85vh - 50px
  ); /* Subtract button's height from container's max height */
  overflow-y: visible; /* Enable vertical scrolling if content overflows */
  margin-top: 42px;
  margin-left: 35px;
  transform-style: preserve-3d;
  transition: transform 0.6s;
  transform-origin: 50% 50%;
}
.search-card-preview .front,
.search-card-preview .back {
  backface-visibility: hidden;
  position: absolute;
  top: 0;
  left: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 15px;
  background-color: #ceeafc;
  border: 1px solid #ccc;
  border-radius: 5px;
  width: 100%;
  height: 100%;
  box-shadow: 4px 2px 2px #ccc;
}
.search-card-preview .back {
  transform: rotateY(180deg) translateX(13.5%);
}
.search-card-preview:hover .flipper {
  transform: rotateY(180deg); /* Rotate around the Y-axis for 3D flip effect */
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
  max-width: 80%;
  background-color: #f5e5fa;
  padding-left: 20px;
  padding-right: 20px;
  padding-bottom: 20px;
  border-radius: 15px;
  margin-bottom: 50px;
  display: flex;
  align-items: center;
}
#deck-edit {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.deck-edit-input {
  display: flex;
  flex-direction: column;
  margin-top: 50px;
}
.deck-edit-input input {
  width: 300px;
  padding: 5px 10px;
  background: #ffffff;
  border-radius: 5px;
  margin-top: 5px;
  margin-bottom: 10px;
}
.card-preview-scroll {
  max-height: calc(
    85vh - 50px
  ); /* Subtract button's height from container's max height */
  overflow-y: auto; /* Enable vertical scrolling if content overflows */
  margin-top: 50px; /* Create space for the fixed button */
  margin-left: 35px;
}
.search-preview-scroll {
  max-height: calc(
    85vh - 50px
  ); /* Subtract button's height from container's max height */
  overflow-y: auto; /* Enable vertical scrolling if content overflows */
  margin-top: 10px; /* Create space for the fixed button */
  width: 320px;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  justify-content: space-between;
}
#name-description {
  text-align: center;
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
.pencil:hover,
.trash-can:hover {
  transform: scale(1.1);
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
#current-card .back {
  margin-left: -40px;
  padding-right: 30px;
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
  table-layout: fixed;
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
  white-space: normal;
  word-wrap: break-word;
}
.card-content {
  width: 50%;
}
.keywords-cell {
  width: 20%;
}
.button-cell {
  width: 5%;
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
.growable-container {
  width: 100%; /* Ensure the container takes the full width */
}
.growable-text {
  width: 100%;
  box-sizing: border-box;
  resize: none;
  word-wrap: break-word;
  height: auto !important; /* Set the height to auto to allow it to grow with content */
  font-family: Verdana;
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
#home-button:hover {
  background-color: #bd76ec;
}
#savebutton {
  display: flex;
}
#savebutton button {
  font-size: 14px;
  border-radius: 3px;
  padding: 5px 10px;
  margin-top: 30px;
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
  background-color: #dd7f5a;
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
  border-radius: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow-wrap: break-word;
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
  width: 35px;
  height: 35px;
  position: absolute;
  bottom: -90px;
  right: -60px;
}
#delete-modal {
  text-align: center;
  position: fixed;
}
.modal-buttons {
  font-size: 10px;
  padding: 8px;
  cursor: pointer;
  display: flex;
  justify-content: center;
}
.confirm-delete {
  background-color: grey;
  margin-right: 30px;
}
.confirm-delete:hover {
  background-color: #dd7f5a;
}
.cancel-delete {
  background-color: #3498db;
}
.cancel-delete:hover {
  background-color: #2e7db2;
}
#delete-card-modal {
  text-align: center;
  position: fixed;
}
.highlighted-row {
  background-color: #dda792; /* Change to your preferred color */
}
#card-selection,
#card-search {
  width: 325px;
  height: 85vh;
  border: 1px solid #ccc;
  background-color: rgb(255, 255, 253);
  box-shadow: 10px 5px 5px #ccc;
  border-radius: 15px;
  margin: 20px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  max-height: 1050px;
  align-self: flex-start;
  margin-top: 100px;
  overflow-y: auto;
  overflow-x: hidden;
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
  width: 28px;
}

#add-keyword-button {
  max-width: 28px;
}
.tooltip {
  position: relative;
  cursor: pointer;
  border-bottom: 1px dashed #ccc; /* Add an underline to indicate interactivity */
}

.tooltip::after {
  content: attr(data-tooltip);
  position: absolute;
  background-color: #333;
  color: #fff;
  padding: 5px;
  border-radius: 5px;
  font-size: 14px;
  opacity: 0;
  visibility: hidden;
  transition: opacity 0.3s ease, visibility 0.3s ease;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  white-space: nowrap; /* Prevent tooltip from breaking into multiple lines */
}
.tooltip:hover::after {
  opacity: 1;
  visibility: visible;
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.card-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.modal-content {
  width: 400px;
  padding: 20px;
  background: #ffffff;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
}
.modal-content h3 {
  color: #3498db;
}
.modal-input-group input[type="text"] {
  width: 350px;
  padding: 10px;
  margin: 5px 0;
  border-radius: 5px;
  margin-bottom: 10px;
}
.modal-actions {
  display: flex;
  justify-content: space-between;
}
#create-button {
  padding: 5px 10px;
  margin-top: 5px;
  background-color: #3498db;
}
#create-button:hover {
  background-color: #2e7db2;
}
#cancel-create-button {
  padding: 5px 10px;
  margin-top: 5px;
  background-color: grey;
}
#cancel-create-button:hover {
  background-color: #dd7f5a;
}
#study-button-div {
  display: flex;
  justify-content: center;
}
#study-button {
  background-color: #c184e9;
  color: #fff;
  padding: 8px 12px;
  border: none;
  border-radius: 5px;
  font-size: 18px;
  cursor: pointer;
  margin-bottom: 100px;
}
#study-button:hover {
  background-color: #bd76ec;
}
</style>
