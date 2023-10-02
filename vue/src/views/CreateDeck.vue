<template>
  <div id="create-deck">
    <button
      id="home-button"
      v-if="$store.state.token != ''"
      @click="backToHome"
    >
      Back to Home
    </button>
    <img id="s-logo" src="S-logo.png" alt="Study Stack logo" />
    <div role="created-alert" v-if="created">
      {{ creationMessage }}
    </div>
    <div role="alert" v-if="error">
      {{ errorMessage }}
    </div>
    <form v-on:submit.prevent="createDeck">
      <p id="max-length-message">{{ maxLengthMessage }}</p>
      <div class="form-input-group">
        <label for="name">Name: </label>
        <input type="text" maxlength="30" v-model="newDeck.name" required />
      </div>
      <div class="form-input-group">
        <label for="coverImage">Cover Image URL: </label>
        <input
          type="text"
          v-model="newDeck.coverImage"
          placeholder="Optional"
        />
      </div>
      <div class="form-input-group">
        <label for="description">Stack Description: </label>
        <input
          type="text"
          maxlength="999"
          v-model="newDeck.description"
          placeholder="Optional"
        />
      </div>
      <button>Create Stack</button>
    </form>
  </div>
</template>
<script>
import DeckService from "../services/DeckService";
export default {
  data() {
    return {
      newDeck: {},
      created: false,
      creationMessage: "Stack created!",
      error: false,
      errorMessasge: "*Error creating stack.*",
      maxLengthMessage: "",
    };
  },
  methods: {
    createDeck() {
      this.newDeck.creator = this.$store.state.user.id;
      DeckService.createDeck(this.newDeck)
        .then((response) => {
          if (response.status == 200) {
            this.created = true;
            this.creationMessage = `Stack "${this.newDeck.name}" created!`;
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
      this.$router.push("/");
    },
  },
  watch: {
    "newDeck.name"(value) {
      if (value.length === 30) {
        this.maxLengthMessage = "You can enter 30 characters only.";
      } else {
        this.maxLengthMessage = "";
      }
    },
  },
};
</script>

<style scoped>
#create-deck {
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

div[role="alert"] {
  color: rgb(185, 48, 48);
  font-size: 16px;
  margin-bottom: 10px;
}

div[role="created-alert"] {
  color: #3498db;
  font-size: 18px;
  margin-bottom: 10px;
}

#max-length-message {
  color: rgb(185, 48, 48);
  font-size: 12px;
}
</style>
