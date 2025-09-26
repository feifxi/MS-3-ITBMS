package sit.int204.itbmsbackend.controller.v2;

import jakarta.validation.Valid;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int204.itbmsbackend.dto.chatbot.ChatbotRequestDTO;
import sit.int204.itbmsbackend.dto.chatbot.ChatbotResponseDTO;

@RestController
@RequestMapping("/v2/chatbot")
public class ChatbotController {
    private final ChatClient openAiChatClient;
    private final ChatClient ollamaChatClient;

    public ChatbotController(@Qualifier("openAiChatClient") ChatClient openAiChatClient,
                             @Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {
        this.openAiChatClient = openAiChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    @PostMapping("/openai")
    public ResponseEntity<ChatbotResponseDTO> chatWithOpenAI(@Valid @RequestBody ChatbotRequestDTO chatbotRequestDTO) {
        String response = openAiChatClient.prompt()
                .user(chatbotRequestDTO.getMessage())
                .call()
                .content();
        return ResponseEntity.ok(new ChatbotResponseDTO(response));
    }

    @PostMapping("/ollama")
    public ResponseEntity<ChatbotResponseDTO> chatWithOllama(@Valid @RequestBody ChatbotRequestDTO chatbotRequestDTO) {
        String response = ollamaChatClient.prompt()
                .user(chatbotRequestDTO.getMessage())
                .call()
                .content();
        return ResponseEntity.ok(new ChatbotResponseDTO(response));
    }
}
