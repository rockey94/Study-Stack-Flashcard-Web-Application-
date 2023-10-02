<template>
  <div id="login">
    <img id="s-logo" src="S-logo.png" alt="Study Stack logo" />
    <form @submit.prevent="login">
      <h1 >Sign in to Study Stack</h1>
      <div role="alert" v-if="invalidCredentials">
        *Invalid email and password!*
      </div>
      <div role="registered-alert" v-if="this.$route.query.registration">
        Thank you for registering! Please sign in.
      </div>
      <div class="form-input-group">
        <label for="username">Email</label>
        <input type="email" id="username" v-model="user.username" required autofocus />
      </div>
      <div class="form-input-group">
        <label for="password">Password</label>
        <input type="password" id="password" v-model="user.password" required />
      </div>
      <button type="submit">Sign in</button>
      <p>
      <router-link :to="{ name: 'register' }">Ready to get stacking? Sign up here.</router-link></p>
    </form>
  </div>
</template>

<script>
import authService from "../services/AuthService";

export default {
  name: "login",
  components: {},
  data() {
    return {
      user: {
        username: "",
        password: ""
      },
      invalidCredentials: false
    };
  },
  methods: {
    login() {
      authService
        .login(this.user)
        .then(response => {
          if (response.status == 200) {
            this.$store.commit("SET_AUTH_TOKEN", response.data.token);
            this.$store.commit("SET_USER", response.data.user);
            this.$router.push("/");
          }
        })
        .catch(error => {
          const response = error.response;

          if (response.status === 401) {
            this.invalidCredentials = true;
          }
        });
    }
  }
};
</script>

<style scoped>
#login {
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

div[role=alert] {
  color: rgb(185, 48, 48);
  font-size: 16px;
  margin-bottom: 10px;
}

div[role=registered-alert] {
  color: #3498db;
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