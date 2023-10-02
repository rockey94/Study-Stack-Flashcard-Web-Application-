<template>
  <div id="register" class="text-center">
    <img id="s-logo" src="S-logo.png" alt="Study Stack logo" />
    <form @submit.prevent="register">
      <h1>Create a Study Stack Account</h1>
      <div role="alert" v-if="registrationErrors">
        {{ registrationErrorMsg }}
      </div>
      <div class="form-input-group">
        <label for="username">Email</label>
        <input
          type="email"
          id="username"
          v-model="user.username"
          placeholder="example@email.com"
          required
          autofocus
        />
      </div>
      <div class="form-input-group">
        <label for="password">Password</label>
        <input
          type="password"
          id="password"
          v-model="user.password"
          placeholder="******"
          required
        />
      </div>
      <div class="form-input-group">
        <label for="confirmPassword">Confirm Password</label>
        <input
          type="password"
          id="confirmPassword"
          placeholder="******"
          v-model="user.confirmPassword"
          required
        />
      </div>
      <button type="submit">Create Account</button>
      <p>
        <router-link :to="{ name: 'login' }"
          >Already have an account? Log in.</router-link
        >
      </p>
    </form>
  </div>
</template>

<script>
import authService from "../services/AuthService";
export default {
  name: "register",
  data() {
    return {
      user: {
        username: "",
        password: "",
        confirmPassword: "",
        role: "user",
      },
      registrationErrors: false,
      registrationErrorMsg: "*There were problems registering this user.*",
    };
  },
  methods: {
    register() {
      if (this.user.password != this.user.confirmPassword) {
        this.registrationErrors = true;
        this.registrationErrorMsg =
          "*Password & Confirm Password do not match.*";
      } else {
        authService
          .register(this.user)
          .then((response) => {
            if (response.status == 201) {
              this.$router.push({
                path: "/login",
                query: { registration: "success" },
              });
            }
          })
          .catch((error) => {
            const response = error.response;
            this.registrationErrors = true;
            if (response.status === 400) {
              this.registrationErrorMsg = "*Email already in use.*";
            } else if (response.status === 403) {
              this.registrationErrorMsg = "*Must be valid email.*";
            }
          });
      }
    },
    clearErrors() {
      this.registrationErrors = false;
      this.registrationErrorMsg =
        "*There were problems registering this user.*";
    },
  },
};
</script>

<style scoped>
#register {
  background-color: #cdecd7;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  font-family: Verdana;
}

#s-logo {
  position: absolute;
  top: 20px;
  right: 20px;
  height: 100px;
}

h1 {
  color: #3498db;
  font-size: 24px;
  margin-bottom: 20px;
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

div[role="alert"] {
  color: rgb(185, 48, 48);
  font-size: 16px;
  margin-bottom: 10px;
}

.router-link {
  color: rgb(101, 72, 134);
  font-size: 14px;
  text-decoration: none;
}

.router-link:hover {
  color: #3498db;
}
</style>
