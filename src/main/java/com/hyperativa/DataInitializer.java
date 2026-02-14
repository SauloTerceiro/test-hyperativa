package com.hyperativa;

import com.hyperativa.Entities.User;
import com.hyperativa.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        final String defaultEmail = "admin@hyperativa.com";
        final String defaultVerificationCode = "123456";
        final String defaultPassword = "admin123";
        final String defaultName = "Admin Hyperativa";

        try {
            User userToUpdate = new User();
            userToUpdate.setEmail(defaultEmail);
            userToUpdate.setVerificationCode(defaultVerificationCode);
            userToUpdate.setPassword(defaultPassword);
            userToUpdate.setName(defaultName);

            userService.updateUser(userToUpdate);
            logger.info("Default user ('admin@hyperativa.com') updated successfully!");

        } catch (Exception e) {
            // If the user is already updated, updateUser will throw an exception.
            // We can ignore this, as the goal is to ensure it is updated.
            logger.info("Default user ('admin@hyperativa.com') is already updated. No action needed.");
        }
    }
}
