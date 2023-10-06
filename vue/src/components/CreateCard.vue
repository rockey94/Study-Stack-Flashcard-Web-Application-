<template>
  <div id="create-card">
    <button id="home-button" v-if="$store.state.token != ''" @click="backToHome">Back to Home</button>
    <img id="s-logo" src="S-logo.png" alt="Study Stack logo" />
    <div role="created-alert" v-if="created">
        {{ creationMessage }}
    </div>
    <div role="alert" v-if="error">
      {{ errorMessage }}
    </div>
    <form v-on:submit.prevent="createCard">
      <div class="form-input-group">
        <label for="question">Question: </label>
        <input type="text" v-model="newCard.question" required />
      </div>
      <div class="form-input-group">
        <label for="answer">Answer: </label>
        <input type="text" v-model="newCard.answer" required />
      </div>
      <button>Create Flashcard</button>
    </form>
  </div>
</template>
<script>
import CardService from "../services/CardService";
export default {
  data() {
    return {
      newCard: {},
      created: false,
      creationMessage: 'Flashcard created!',
      error: false,
      errorMessasge: '*Error creating flashcard.*',
    };
  },
  methods: {
    createCard() {
      this.newCard.creator = this.$store.state.user.id;
      CardService.createCard(this.newCard)
        .then((response) => {
          if (response.status == 200) {
            this.created = true;
            this.creationMessage = `Flashcard created for "${this.newCard.question}"!`
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
    backToHome() {
      this.$router.push('/');
    }
  },
  computed: {
    creatorId() {
      return this.$store.state.user.id;
    },
  },
};
</script>

<style scoped>
#create-card {
  background-color: #cdecd7;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 100vh; 
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

#s-logo {
  position: absolute;
  top: 20px;
  right: 20px;
  height: 100px;
}

.form-input-group {
  margin-bottom: 20px;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 16px;
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

button:hover {
  background-color: #2580b3; 
}

.router-link {
  color: rgb(101, 72, 134);
  font-size: 14px;
  text-decoration: none;
}

.router-link:hover {
  color: #3498db;
}

div[role=alert] {
  color: rgb(185, 48, 48);
  font-size: 16px;
  margin-bottom: 10px;
}

div[role=created-alert] {
  color: #3498db;
  font-size: 18px;
  margin-bottom: 10px;
}
</style>
