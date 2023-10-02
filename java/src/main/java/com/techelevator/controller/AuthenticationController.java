package com.techelevator.controller;

import javax.validation.Valid;

import com.techelevator.model.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.techelevator.dao.UserDao;
import com.techelevator.security.jwt.JWTFilter;
import com.techelevator.security.jwt.TokenProvider;

// Logging imports
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin
public class AuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private UserDao userDao;

    public AuthenticationController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, UserDao userDao) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDao = userDao;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginDto loginDto) {

        // Log an informational message that a login attempt is being made for a specific user
        LOGGER.info("Attempting to login user: {}", loginDto.getUsername());

        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.createToken(authentication, false);

            User user = userDao.findByUsername(loginDto.getUsername());

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
            LOGGER.info("Login successful for user: {}", loginDto.getUsername());
            return new ResponseEntity<>(new LoginResponseDto(jwt, user), httpHeaders, HttpStatus.OK);
        } catch (Exception e) {

            // Log an error message if any exception arises during login, capturing the exception details
            LOGGER.error("Error during login for user: {}", loginDto.getUsername(), e);
            throw e; // Re-throw the exception so your application can handle it as it normally would
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void register(@Valid @RequestBody RegisterUserDto newUser) {

        // Log an informational message that a registration attempt is being made for a specific user
        LOGGER.info("Attempting to register user: {}", newUser.getUsername());

        try {
            if (!newUser.getUsername().contains("@") || !newUser.getUsername().contains(".")) {

                // Log a warning if an invalid email format is attempted to be registered
                LOGGER.warn("Registration attempt with invalid email: {}", newUser.getUsername());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Must Be Valid Email");
            } else {
                User user = userDao.findByUsername(newUser.getUsername());
                // Log a warning if a registration attempt is made for an already existing user
                LOGGER.warn("Registration attempt for existing user: {}", newUser.getUsername());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Already Exists.");
            }
        } catch (UsernameNotFoundException e) {
            userDao.create(newUser.getUsername().toLowerCase(),newUser.getPassword(), newUser.getRole());

            // Log an informational message once the user has been successfully registered
            LOGGER.info("User registered successfully: {}", newUser.getUsername());
        } catch (Exception e) {

            // Log an error message if any exception arises during registration, capturing the exception details
            LOGGER.error("Error during registration for user: {}", newUser.getUsername(), e);
            throw e; // Re-throw the exception so your application can handle it as it normally would
        }
    }
}
