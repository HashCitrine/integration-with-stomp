package com.micube.integration.server.runner;

import com.micube.integration.server.ExecService;
import com.micube.integration.server.vo.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
@RequiredArgsConstructor
public class RotationRunner implements ApplicationRunner {

    private final ExecService execService;

    private final HashSet<Ticket> sendWaitLine = new HashSet<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info(">>> Rotation Start");
        new Thread(this::rotation).start();
    }

    public void rotation() {
        LinkedBlockingQueue<Ticket> channel = execService.getChannel();
        LinkedBlockingQueue<Ticket> finishLine = execService.getFinishLine();
        ConcurrentLinkedQueue<String> waitLine = execService.getWaitLine();

        Ticket ticket;
        while (true) {
            try {
                ticket = finishLine.take();
                sendWaitLine.add(ticket);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                continue;
            }

            sendWaitLine.forEach(finishTask -> {
                String prioritySendTask = waitLine.peek();

                if (prioritySendTask != null && prioritySendTask.equals((finishTask.getImageName()))) {
                    try {
                        channel.put(finishTask);
                        waitLine.poll();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                if(waitLine.size() == 1) {

                }

            });
        }


    }
}
