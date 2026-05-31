package main.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ChatController {

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage message) {
        message.setTimestamp(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        );
        return message;
    }

    @MessageMapping("/chat.join")
    @SendTo("/topic/public")
    public ChatMessage joinChat(@Payload ChatMessage message,
                                SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes()
                .put("username", message.getSender());

        message.setType(ChatMessage.MessageType.JOIN);
        message.setContent(message.getSender() + " joined the chat");
        message.setTimestamp(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        );
        return message;
    }
}
