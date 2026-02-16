package com.hyperativa.Routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CardRoute extends RouteBuilder {

    public static final String CREATE_CARD = "direct:createCard";
    public static final String GET_ALL_CARDS = "direct:getAllCards";
    public static final String GET_CARD_BY_ID = "direct:getCardById";
    public static final String UPDATE_CARD = "direct:updateCard";
    public static final String DELETE_CARD = "direct:deleteCard";
    public static final String PROCESS_BATCH_FILE = "direct:processBatchFile";

    @Override
    public void configure() {
        from(CREATE_CARD)
            .routeId("createCard")
            .log("Creating Card")
            .bean("cardService", "createCard");

        from(GET_ALL_CARDS)
            .routeId("getAllCards")
            .log("Getting all Cards")
            .bean("cardService", "getAllCards");

        from(GET_CARD_BY_ID)
            .routeId("getCardById")
            .log("Getting Card by ID")
            .bean("cardService", "getCardById");

        from(UPDATE_CARD)
            .routeId("updateCard")
            .log("Updating Card")
            .bean("cardService", "updateCard");

        from(DELETE_CARD)
            .routeId("deleteCard")
            .log("Deleting Card")
            .bean("cardService", "deleteCard");

        from(PROCESS_BATCH_FILE)
            .routeId("processBatchFile")
            .log("Processing batch file")
            .bean("cardService", "processBatchFile");
    }
}
