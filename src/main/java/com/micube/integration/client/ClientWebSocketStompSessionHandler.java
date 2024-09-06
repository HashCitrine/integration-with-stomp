package com.micube.integration.client;

import com.micube.integration.server.vo.Message;
import com.micube.integration.server.vo.HelloMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

@Slf4j
public class ClientWebSocketStompSessionHandler extends StompSessionHandlerAdapter {

    private StompSession stompSession;

    @Override
    public void handleFrame(@NonNull StompHeaders headers, Object payload) {

        // 구독한 채널의 메세지 받기
        log.info("SpringStompSessionHandler.handleFrame");
        log.info("headers = " + headers);
        log.info("payload = " + new String((byte[]) payload));
    }

    @Override
    public @NonNull Type getPayloadType(@NonNull StompHeaders headers) {
        return Object.class;
    }

    @Override
    public void afterConnected(@NonNull StompSession session, @NonNull StompHeaders connectedHeaders) {

        stompSession = session;
        sendMessage("client service test");
    }

    public void sendMessage(String message) {
        // 구독
//        stompSession.subscribe("/topic/greetings", this);
        stompSession.subscribe("/app/hello", this);

        Message greeting = new Message(message);
        HelloMessage hello = new HelloMessage(message);
        // 메세지 보냄
        stompSession.send("/app/hello", hello);

        log.info("params = " + greeting);
    }

    @Override
    public void handleException(@NonNull StompSession session, StompCommand command,
                                @NonNull StompHeaders headers, byte @NonNull [] payload, @NonNull Throwable exception) {
        log.info("SpringStompSessionHandler.handleException");
        log.info("exception = " + exception);
    }

    @Override
    public void handleTransportError(@NonNull StompSession session, @NonNull Throwable exception) {
        log.info("SpringStompSessionHandler.handleTransportError");
    }
}
