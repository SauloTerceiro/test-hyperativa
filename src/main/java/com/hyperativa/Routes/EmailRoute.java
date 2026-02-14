package com.hyperativa.Routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class EmailRoute extends RouteBuilder {

    public static final String SEND_EMAIL_VERIFICATION = "direct:sendVerificationEmail";

    public static final String VERIFY_CODE = "direct:checkVerificationCode";

    @Override
    public void configure() {
        from(SEND_EMAIL_VERIFICATION)
            .routeId("sendVerificationEmailRoute")
            .log("Recebendo requisicao para envio de codigo de verificao para ${body}")
            .bean("emailService", "generateAndSendVerificationCode");

    
            from(VERIFY_CODE)
            .routeId("verifyCode")
            .log("Recebendo requisicao para check de codigo de verificao para ${body}")
            .bean("emailService", "verifyCode");
    }
}
