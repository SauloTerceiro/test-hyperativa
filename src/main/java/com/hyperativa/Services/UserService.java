package com.hyperativa.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.hyperativa.Dtos.CheckPasswordDTO;
import com.hyperativa.Entities.User;
import com.hyperativa.Exceptions.EmailAlreadyExistsException;
import com.hyperativa.Exceptions.EmailAlreadyRegisteredException;
import com.hyperativa.Exceptions.UserNotFoundException;
import com.hyperativa.Exceptions.VerificationCodeMismatchException;
import com.hyperativa.Repositories.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final SecretKey secretKey = Keys.hmacShaKeyFor("Yh7sJ9kTkq2XvA2dL3eZsNcBfT6uWrQx".getBytes(StandardCharsets.UTF_8));


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        logger.info("Starting user creation with email: {}", user.getEmail());
        Optional<User> userEmail =userRepository.findByEmail(user.getEmail());
        // Check if email is already in use
        if (userEmail.isPresent()) {
            throw new EmailAlreadyExistsException("Email is already in use: " + user.getEmail());
        }
        if(user.getPassword()!=null){
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
        }
        // Encode password before saving
        // Save user to database
        user.setEmail(user.getEmail().toLowerCase());
        return saveUser(user);
    }

    private User saveUser(User user) {
        logger.info("Saving user to database: {}", user.getEmail());
        return userRepository.save(user);
    }

    public User getUserById(Long id){
        return userRepository.getReferenceById(id);
    }

    

    
    public CheckPasswordDTO checkPassword(User user) {
        User existingUser = findUserByEmail(user.getEmail());
        boolean matches = existingUser != null && passwordEncoder.matches(user.getPassword(), existingUser.getPassword());

        CheckPasswordDTO dto = new CheckPasswordDTO();
        dto.setCorrectPassword(matches);

        if (matches) {
            String token = Jwts.builder()
                .setSubject(existingUser.getEmail())
                .claim("id", existingUser.getId())
                .claim("role", existingUser.getRole())  
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7200_000)) // 2h
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
            dto.setJwtToken(token);
        }

        return dto;
    }

    private User findUserByEmail(String email) {
        // Logic to find a user by email
        // This can be a database query
        
        User userEmail = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        return userEmail; // Replace with real database query
    }

    public User updateUser(User user) {
        String email = user.getEmail();
        String verificationCode = user.getVerificationCode();
        String username = user.getName();
        String password = user.getPassword();
        logger.info("Updating user with email: {}", email);

        // Find user by email
        User userEmail = findUserByEmail(user.getEmail());


        if(userEmail.getName()!=null){
            logger.info("Name: {}", userEmail.getName());
            throw new EmailAlreadyRegisteredException("Email has already registered");
        }

        if(userEmail.getPassword()!=null&&!("".equals(userEmail.getPassword()))){
            logger.info("Password", userEmail.getPassword());
            throw new EmailAlreadyRegisteredException("Email has already registered");
        }


        // Check if verification code matches
        if (!verificationCode.equals(userEmail.getVerificationCode())) {
            throw new VerificationCodeMismatchException("Invalid verification code");
        }

        // Update username (if provided)
        if (username != null && !username.isEmpty()) {
            userEmail.setName(username);
        }

        // Update password (if provided)
        if (password != null && !password.isEmpty()) {
            String encodedPassword = passwordEncoder.encode(password);
            userEmail.setPassword(encodedPassword);
        }

        // Save changes to database
        return userRepository.save(userEmail);
    }
}
