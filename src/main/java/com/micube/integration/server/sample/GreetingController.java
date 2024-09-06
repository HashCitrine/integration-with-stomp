package com.micube.integration.server.sample;

import com.micube.integration.server.ExecService;
import com.micube.integration.server.vo.HelloMessage;
import com.micube.integration.server.vo.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.nio.file.Path;

@Controller
public class GreetingController {

    @Autowired
    ExecService execService;

    @MessageMapping("/hello")
    @SendTo("/topic/request")
    public Message sendMessage(HelloMessage message) throws Exception {
        return new Message(HtmlUtils.htmlEscape(message.getName()));
    }

    @GetMapping("/test")
    public String test() throws IOException {
        execService.execPythonScript(Path.of("test"));

        return "test";
    }

}
