package com.micube.integration.server;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

    @MessageMapping("/hello")
    @SendTo("/topic/request")
    public Greeting greeting(HelloMessage message) throws Exception {
        return new Greeting(HtmlUtils.htmlEscape(message.getName()));
    }

}
