package sit.int204.itbmsbackend.dto.chatbot;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatbotRequestDTO {
    @NotBlank
    private String message;
}
