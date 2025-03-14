package com.runner.ai.controller;

import com.runner.ai.tools.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @since 2025/3/3 15:04
 */
@RestController
@RequestMapping("/ds")
public class DSController {

    private final OpenAiChatModel chatModel;

    private final ChatClient chatClient;

    public DSController(OpenAiChatModel chatModel, ChatClient chatClient) {
        this.chatModel = chatModel;
        this.chatClient = chatClient;
    }

    @GetMapping("/ai/generate")
    public Map<String, String> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message, String voice) {
        chatClient.prompt().system(sp -> sp.param("voice", voice))
                .user(message)
                .call().content();
        Map<String, String> map = new HashMap<>();
        ChatClient chatClient = ChatClient.builder(chatModel).defaultTools(new DateTimeTools()).build();

//        map.put("generation", this.chatModel.call(message));
        map.put("generation", chatClient.prompt(message).call().content());
        return map;
    }
}
