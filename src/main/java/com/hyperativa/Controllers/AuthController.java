package com.hyperativa.Controllers;


import com.hyperativa.Dtos.VerificationRequest;
import com.hyperativa.Routes.EmailRoute;
import com.hyperativa.Services.EmailService;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ProducerTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ProducerTemplate producerTemplate;

    public AuthController(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @PostMapping("/send-code")
    public ResponseEntity<?> sendVerificationCode(@RequestParam String email) {
        try {
            producerTemplate.sendBody(EmailRoute.SEND_EMAIL_VERIFICATION, email);
            return ResponseEntity.ok("Código de verificação enviado para: " + email); 
        } catch (CamelExecutionException e) {
            Throwable cause = e.getCause();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cause.getMessage());
        }
    }

    @PostMapping("/check-code")
    public ResponseEntity<?> checkVerificationCode(@RequestParam String email, @RequestParam String code) {
        String isValid = producerTemplate.requestBody(EmailRoute.VERIFY_CODE, new VerificationRequest(email, code), String.class);
        if (isValid.equals(EmailService.noEmailMessage)) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", EmailService.noEmailMessage));
        }
        
        return ResponseEntity.ok(isValid);
    }
}
