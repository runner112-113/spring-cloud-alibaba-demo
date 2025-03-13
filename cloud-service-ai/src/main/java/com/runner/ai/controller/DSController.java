package com.runner.ai.controller;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
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

    public DSController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/ai/generate")
    public Map<String, String> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Map<String, String> map = new HashMap<>();
        map.put("generation", this.chatModel.call(message));
        return map;
    }
}
