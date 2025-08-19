package sit.int204.itbmsbackend.dtos.chat;

import lombok.Data;

@Data
public class ChatMessage {
    private String from;
    private String text;
}
