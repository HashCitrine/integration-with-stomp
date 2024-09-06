package com.micube.integration.server;

import com.google.gson.Gson;
import com.micube.integration.server.vo.Ticket;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExecService {

    private final SimpMessagingTemplate messagingTemplate;

    @Getter
    private LinkedBlockingQueue<Ticket> channel;

    @Getter
    private ConcurrentLinkedQueue<String> waitLine;

    @Getter
    private LinkedBlockingQueue<Ticket> finishLine;

    @PostConstruct
    public void init() {
        channel = new LinkedBlockingQueue<>();
        waitLine = new ConcurrentLinkedQueue<>();
        finishLine = new LinkedBlockingQueue<>();
    }

    @Async
    public void execPythonScript(Path imageFileName) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C", "python", Path.of("C://micube/qubix/py/test.py").toString());
//        processBuilder.directory(new File("C://micube/qubix/py"));

        String python = "C://Program Files/Python312/python.exe";
//        String command = "python %s".formatted(Path.of("test.py"));
//        String command = "cmd.exe /C java -version";
//        log.info("Exec Command : {}", command);

//        processBuilder.command(command);
        processBuilder.redirectErrorStream(true);

        Process process;
        try {
            process = processBuilder.start();
            process.waitFor();
        } catch(IOException | InterruptedException e) {
            log.error("{}", e.getMessage());
            throw new RuntimeException(e);
        }

        log.info(">>> Start Printing Exec Result");
        List<String> results;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            results = br.lines()
                    .filter(line -> !(line.isEmpty() || line.isBlank()))
                    .toList();
        }
        log.info("Result : {}", results);
        log.info(">>> End Printing Exec Result");

//        messagingTemplate.convertAndSend("/topic/request", new Message(imageFileName.toString()));

        try {
            finishLine.put(Ticket.builder()
                    .imageName(imageFileName.toString())
                    .message(new Gson().toJson(results))
                    .build());
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }

        process.destroy();
    }


}
