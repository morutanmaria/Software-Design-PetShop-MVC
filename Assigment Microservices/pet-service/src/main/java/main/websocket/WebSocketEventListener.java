package main.websocket;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) accessor.getSessionAttributes()
                .get("username");

        if (username != null) {
            ChatMessage leaveMsg = new ChatMessage(
                    username,
                    username + " left the chat",
                    ChatMessage.MessageType.LEAVE
            );
            leaveMsg.setTimestamp(
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            );
            messagingTemplate.convertAndSend("/topic/public", leaveMsg);
        }
    }
}
