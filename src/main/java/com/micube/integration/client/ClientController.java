package com.micube.integration.client;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ClientController {

    @Autowired
    ClientWebSocketStompSessionHandler handler;

    @GetMapping("/call/**")
    public ResponseEntity<String> test(HttpServletRequest request) {
        log.info(request.getRequestURI());
        handler.sendMessage("test controller");
        return ResponseEntity.ok("test");
    }

}
