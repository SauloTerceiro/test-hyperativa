package com.hyperativa.Routes;


import com.hyperativa.Entities.User;
import com.hyperativa.Services.UserService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class UserRoute extends RouteBuilder {

    public static final String CREATE_USER_ROUTE = "direct:createUser";
    public static final String CHECK_PASSWORD_ROUTE = "direct:checkPassword";
    public static final String UPDATE_USER_ROUTE = "direct:updateUser";



    private final UserService userService;

    public UserRoute(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void configure() throws Exception {
        // Rota Camel para ouvir uma URL e salvar um usuário
        from(CREATE_USER_ROUTE)
            .log("Creating user: ${body.email}")
            .process(exchange -> {
                // A criação do usuário vai ser feita aqui
                User user = exchange.getIn().getBody(User.class);
                User createdUser = userService.createUser(user);
                exchange.getMessage().setBody(createdUser);
            })
            .to("log:com.myapp.users?level=INFO");  // Apenas para logar a criação do usuário

        from(CHECK_PASSWORD_ROUTE)
            .log("Checking password for user: ${body.email}")
            .bean("userService", "checkPassword")
            .end();
        
        from(UPDATE_USER_ROUTE)
            .log("Atualizando usuario com ID: ${body.id}")
            .bean("userService", "updateUser")
            .end();
    }
}