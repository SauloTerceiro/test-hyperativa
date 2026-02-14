package com.hyperativa.Services;


import com.hyperativa.Dtos.VerificationRequest;
import com.hyperativa.Entities.User;
import com.hyperativa.Exceptions.EmailAlreadyRegisteredException;
import com.hyperativa.Exceptions.EmailNotFoundException;
import com.hyperativa.Repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;


    public static final String noEmailMessage= "E-mail não registrado.";

    public EmailService(JavaMailSender mailSender, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    public void generateAndSendVerificationCode(String email) throws MessagingException {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new EmailNotFoundException("E-mail nao registrado.");
        }

        User user = userOptional.get();
        if(user.getName()!=null&&user.getPassword()!=null){
            throw new EmailAlreadyRegisteredException("E-mail ja fez o cadastro");
        }
        String verificationCode = String.valueOf(new Random().nextInt(900000) + 100000); // Código de 6 dígitos
        user.setVerificationCode(verificationCode);
        userRepository.save(user); // Salva o código no banco

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(email);
        helper.setSubject("Código de Verificação");
        helper.setText("Seu código de verificação é: " + verificationCode, true);

        mailSender.send(message);
    }

    public String verifyCode(VerificationRequest verificationRequest) {
        Optional<User> userOptional = userRepository.findByEmail(verificationRequest.getEmail());
   
        if (userOptional.isEmpty()) {
            return noEmailMessage;
        }
        User user = userOptional.get();
        
        return ""+verificationRequest.getCode().equals(user.getVerificationCode());
    }
}