package com.hyperativa.Controllers;

import java.util.Map;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.ProducerTemplate;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;

import com.hyperativa.Dtos.CheckPasswordDTO;
import com.hyperativa.Entities.User;
import com.hyperativa.Exceptions.EmailAlreadyExistsException;
import com.hyperativa.Exceptions.UserNotFoundException;
import com.hyperativa.Exceptions.VerificationCodeMismatchException;
import com.hyperativa.Routes.UserRoute;
import com.hyperativa.Services.TokenService;


@RestController
@RequestMapping("/users")
public class UserController {
    private final ProducerTemplate producerTemplate;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    public TokenService tokenService;

    @Autowired
    public UserController(ProducerTemplate producerTemplate, TokenService tokenService) {
        this.producerTemplate = producerTemplate;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            // Sends the message to the Camel route and waits for the response
            User createdUser = producerTemplate.requestBody(UserRoute.CREATE_USER_ROUTE, user, User.class);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (CamelExecutionException e) {
            // Checks if the root cause is an EmailAlreadyExistsException
            Throwable cause = e.getCause();
            if (cause instanceof EmailAlreadyExistsException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cause.getMessage());
            }
            // If not, propagates the error as a 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }


    @GetMapping("/auth/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        boolean isValid = tokenService.validateToken(token);
        return isValid ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    @PostMapping("/check-password")
    public ResponseEntity<?> checkPassword(@RequestBody User user) {
        try {
        // Sends the message to the Camel route and waits for the response
            CheckPasswordDTO result =  producerTemplate.requestBody(UserRoute.CHECK_PASSWORD_ROUTE, user, CheckPasswordDTO.class);
            return ResponseEntity.ok(result);
        } catch (CamelExecutionException e) {
        // Checks if the root cause is a UserNotFoundException or VerificationCodeMismatchException
            Throwable cause = e.getCause();
            if (cause instanceof UserNotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cause.getMessage());
            }
            // If not, propagates the error as a 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PutMapping("/update")    
    public ResponseEntity<?> updateUser(@RequestBody Map<String, String> updates) {
        try {
            // Extracts data from the request body
            String email = updates.get("email");
            String verificationCode = updates.get("code");
            String username = updates.get("username");
            String password = updates.get("password");

            // Creates a User object with the fields to be updated
            User user = new User();
            user.setEmail(email);
            user.setVerificationCode(verificationCode);
            user.setName(username);
            user.setPassword(password);
            logger.info(email+username);
            // Sends the message to the Camel route and waits for the response
            User updatedUser = producerTemplate.requestBody(UserRoute.UPDATE_USER_ROUTE, user, User.class);
            return ResponseEntity.ok(updatedUser);
        } catch (CamelExecutionException e) {
            // Checks if the root cause is a UserNotFoundException or VerificationCodeMismatchException
            Throwable cause = e.getCause();
            if (cause instanceof UserNotFoundException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cause.getMessage());
            }
            if (cause instanceof VerificationCodeMismatchException) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cause.getMessage());
            }
            // If not, propagates the error as a 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}
