<template>
  <div id="study-session">
    <!-- HOME BUTTON AND LOGO -->
    <button
      id="home-button"
      v-if="$store.state.token != ''"
      @click="backToHome"
    >
      Back to Home
    </button>
    <img id="s-logo" src="../../public/S-logo.png" alt="Study Stack logo" />

    <!-- STACK NAME -->
    <h1> {{ deck.name }} </h1>

    <div id="session">
      <!-- BLUE CURRENT CARD -->
      <div
        class="card-flip-container"
        @click="flipCard"
        ref="cardFlipContainer"
        v-if="studying"
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
      </div>

      <!-- END SESSION BUTTON -->
      <img v-on:click="studying = false, ended = true" v-if="studying" id="end-button" src="../../public/end-session.png" alt="End Session" />
    </div>

    <!-- RIGHT/WRONG BUTTONS -->
    <div id="right-wrong-buttons" v-if="studying">
      <img v-on:click="markRight" id="right-button" src="../../public/blue-check.png" alt="Mark Card Correct" />
      <img v-on:click="markWrong" id="wrong-button" src="../../public/red-x.png" alt="Mark Card Incorrect" />
    </div>

    <!-- END SESSION MESSAGE -->
    <div id="end-session" v-if="!studying">
      <p>Great job! Do you want to study again?</p>
      <div id="study-buttons">
        <button id="stack-study" v-on:click="restartSession">Study the whole stack</button>
        <button id="wrong-study" v-on:click="studyWrongOnly" v-if="!((sessionRestarted && (wrongCount == 0 || rightCount == 0)) || this.ended)">Just the ones that need more review</button>
      </div>
    </div>

    <!-- RIGHT/WRONG DATA -->
    <div id="right-wrong-data">
      <h2 id="right-message">You've got it down: {{ rightCount }}</h2>
      <h2 id="wrong-message">Needs more review: {{ wrongCount }}</h2>
    </div>
  </div>
</template>

<script>
import CardService from '../services/CardService';
import DeckService from '../services/DeckService';

export default {
  data() {
    return {
      currentCard: {
        cardId: -1,
        deckId: -1,
        question: "",
        answer: "",
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
      cards: [],
      rightCards: [],
      wrongCards: [],
      cardNumber: 0,
      rightCount: 0,
      wrongCount: 0,
      studying: true,
      sessionRestarted: true,
      ended: false,
      flipCount: 0
    };
  },
  methods: {
    backToHome() {
      this.$router.push("/");
    },
    flipCard() {
      console.log("flipCard method triggered!");
      this.$refs.cardFlipContainer.classList.toggle("flipped");
      this.flipCount++;
    },
    viewCardsByDeckId() {
      CardService.viewCardsByDeckId(this.$route.params.deckId)
        .then((response) => {
          this.cards = response.data;
          console.log(this.cards);
          // Set the current card equal to the first card
          this.currentCard = this.cards[0];
        })
        .catch((error) => {
          console.error("Error fetching cards by deckId:", error);
        });
    },
    markRight() {
      this.rightCards[this.rightCount] = this.currentCard;
      this.rightCount++;
      this.showNextCard();

      console.log("right:" + this.rightCount);
    },
    markWrong() {
      if (this.wrongCount == 0) {
        this.wrongCards = [];
      }
      this.wrongCards[this.wrongCount] = this.currentCard;
      this.wrongCount++;
      this.showNextCard();

      console.log("wrong:" + this.wrongCount);
    },
    showNextCard() {
      if (this.flipCount%2 != 0) {
        this.flipCard();
      }
      this.$refs.cardFlipContainer.classList.add("slide-off"); // Add the animation class
      setTimeout(() => {
        this.$refs.cardFlipContainer.classList.remove("slide-off"); // Remove the animation class after it completes
        this.cardNumber++;
        if (this.cardNumber < this.cards.length) {
          this.currentCard = this.cards[this.cardNumber];
        } else {
          // End the study session
          this.studying = false;
        }
      }, 600); // 0.6s, should match the animation duration
    },
    restartSession() {
      this.sessionRestarted = true;
      this.viewCardsByDeckId();

      this.flipCount = 0;
      this.wrongCount = 0;
      this.rightCount = 0;
      this.cardNumber = 0;
      this.ended = false;
      this.studying = true;
    },
    studyWrongOnly() {
      this.sessionRestarted = false;
      this.cards = this.wrongCards;
      this.currentCard = this.wrongCards[0];

      this.flipCount = 0;
      this.wrongCount = 0;
      this.rightCount = 0;
      this.cardNumber = 0;
      this.ended = false;
      this.studying = true;
    }
  },
  created() {
    this.deck.deckId = this.$route.params.deckId;
    this.viewCardsByDeckId();
    DeckService.getDeckById(this.deck.deckId).then((response) => {
      this.deck.name = response.data.name;
      this.deck.creator = response.data.creator;
      this.deck.coverImage = response.data.coverImage;
      this.deck.description = response.data.description;
    });
  }
}
</script>

<style scoped>
#study-session {
    background-color: #cdecd7;
    height: 100vh;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    font-family: Verdana;
}

h1 {
  color: #c184e9;
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

#s-logo {
  position: absolute;
  top: 20px;
  right: 20px;
  height: 100px;
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

.card-flip-container {
  perspective: 1000px;
  width: 750px; /* Width of #current-card */
  height: 350px; /* Height of #current-card */
  cursor: pointer; /* Make it obvious it can be clicked */
  margin-top: 10px;
  margin-bottom: 10px;
  margin-right: 15px;
  margin-left: 65px;
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
  margin-left: -30px;
  padding-right: 20px;
}

.card-flip-container.flipped #current-card {
  transform: rotateY(180deg);
}

#session {
  display: flex;
  align-items: flex-end;
}

#end-button {
  max-width: 40px;
  max-height: 50px;
  cursor: pointer;
  margin-bottom: 10px;
  margin-left: 10px;
}

#right-wrong-buttons {
  display: flex;
  align-items: center;
}

#right-button {
  max-width: 60px;
  cursor: pointer;
}

#wrong-button {
  max-width: 38px;
  cursor: pointer;
}

#right-button:hover, #wrong-button:hover, #end-button:hover {
  transform: scale(1.1);
}

#right-wrong-data {
  font-size: 12px;
}

#right-message {
  color: rgb(38, 111, 206);
}

#wrong-message {
  color: #dd695a;
}

.card-flip-container.slide-off {
  animation: slideOff 0.6s ease-in-out;
}

@keyframes slideOff {
  0% {
    transform: translateX(0) translateY(0);
    opacity: 1;
  }
  100% {
    transform: translateX(-100%) translateY(100%);
    opacity: 0;
  }
}

#end-session {
  height: fit-content;
  width: fit-content;
  background-color: #f5e5fa;
  padding-left: 20px;
  padding-right: 20px;
  padding-bottom: 20px;
  border-radius: 15px;
  margin-bottom: 50px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

#study-buttons button {
  color: #fff;
  padding: 8px 10px;
  margin-bottom: 5px;
  margin-left: 5px;
  margin-right: 5px;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
}

#stack-study {
  background-color: #c184e9;
}

#stack-study:hover {
  background-color: #bd76ec;
}

#wrong-study {
  background-color: #e27b6e;
}

#wrong-study:hover {
  background-color: #dd695a;
}
</style>