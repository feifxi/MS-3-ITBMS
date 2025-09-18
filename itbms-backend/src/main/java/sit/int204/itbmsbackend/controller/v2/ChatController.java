package sit.int204.itbmsbackend.controller.v2;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import sit.int204.itbmsbackend.dto.chat.ChatMessage;

@Controller
public class ChatController {

    @MessageMapping("/chat")  // /app/chat
    @SendTo("/topic/messages") // send to all subscribers
    public ChatMessage send(ChatMessage message) {
        return message; // broadcast
    }
}