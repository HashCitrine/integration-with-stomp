package com.micube.integration.server.runner;

import com.micube.integration.server.ExecService;
import com.micube.integration.server.vo.Message;
import com.micube.integration.server.vo.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

@Component
@Slf4j
@RequiredArgsConstructor
public class SendRunner implements ApplicationRunner {
    private final ExecService execService;

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void run(ApplicationArguments args) {
        log.info(">>> Sender Start");
        new Thread(this::send).start();
    }

    public void send() {
        LinkedBlockingQueue<Ticket> channel = execService.getChannel();
        while (true) {
            Ticket ticket;
            try {
                ticket = channel.take();
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                continue;
            }
            messagingTemplate.convertAndSend("/topic/request", new Message(ticket.getImageName()));
            messagingTemplate.convertAndSend("/topic/request", new Message(ticket.getMessage()));
        }
    }
}
