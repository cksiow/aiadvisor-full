package com.cksiow.ai.advisor.controller;


import com.cksiow.ai.advisor.assistant.dto.OpenAIAssistantResponse;
import com.cksiow.ai.advisor.dto.MessageRequest;
import com.cksiow.ai.advisor.dto.MessageResponse;
import com.cksiow.ai.advisor.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/openai")
public class OpenAIController {
    public OpenAIController() {

    }

    @Autowired
    OpenAIService openAIService;

    @GetMapping("/assistants")
    public List<OpenAIAssistantResponse> getAssistants(@RequestParam(required = false) String uniqueId) {
        return openAIService.getAssistants(uniqueId);
    }

    @PostMapping("/message")
    public MessageResponse sendMessage(@RequestBody MessageRequest message) {
        return openAIService.sendMessage(message);
    }


    @GetMapping("/byThreadId")
    public List<MessageResponse> getThreadMessage(@RequestParam String threadId, @RequestParam String uniqueId) {
        return openAIService.getThreadMessage(threadId, uniqueId);
    }

    @PostMapping("/assistants/instructions/update")
    public void updateAssistantsInstructions(@RequestParam String key) {
        openAIService.updateAssistantsInstructions(key);
    }
}
