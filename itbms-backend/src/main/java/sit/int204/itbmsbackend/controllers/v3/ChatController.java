package sit.int204.itbmsbackend.controllers.v3;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import sit.int204.itbmsbackend.dtos.chat.ChatMessage;

@Controller
public class ChatController {

    @MessageMapping("/chat")  // /app/chat
    @SendTo("/topic/messages") // send to all subscribers
    public ChatMessage send(ChatMessage message) {
        return message; // broadcast
    }
}