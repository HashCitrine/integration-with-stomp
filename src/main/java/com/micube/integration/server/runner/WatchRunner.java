package com.micube.integration.server.runner;

import com.micube.integration.server.ExecService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Slf4j
@RequiredArgsConstructor
public class WatchRunner implements ApplicationRunner {

    //    @Value("${dir}")
    private String dir = "C://micube/qubix/imageDir";
    private final ExecService execService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info(">>> Watcher Start");
        new Thread(this::watch).start();
    }

    //    @PostConstruct
    public void watch() {
        WatchService watchService;
        try {
            watchService = FileSystems.getDefault().newWatchService();

            Path path = Paths.get(dir);
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        } catch (IOException e) {
            log.error(e.getMessage());
            return;
        }

        log.info(">>> Start Watching Image Dir : {}", dir);

        ConcurrentLinkedQueue<String> waitLine = execService.getWaitLine();
        while (true) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                log.error("Watch Error : {}", e.getMessage());
                break;
            }

            List<WatchEvent<?>> events = key.pollEvents();

            for (WatchEvent<?> event : events) {
//                WatchEvent.Kind<?> kind = event.kind();
                Path imageFile = (Path) event.context();

                log.info("Create Image : {}", imageFile.getFileName());

                try {
                    waitLine.add(imageFile.getFileName().toString());
                    execService.execPythonScript(imageFile.getFileName());
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
                key.reset();
            }
        }
    }


}
