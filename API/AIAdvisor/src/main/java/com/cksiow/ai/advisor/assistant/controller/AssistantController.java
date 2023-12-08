package com.cksiow.ai.advisor.assistant.controller;


import com.cksiow.ai.advisor.assistant.dto.OpenAIAssistantResponse;
import com.cksiow.ai.advisor.assistant.dto.PersonalAssistantDeleteRequest;
import com.cksiow.ai.advisor.assistant.dto.PersonalAssistantRequest;
import com.cksiow.ai.advisor.assistant.model.AssistantCreate;
import com.cksiow.ai.advisor.assistant.model.AssistantResponse;
import com.cksiow.ai.advisor.assistant.repository.AssistantRepository;
import com.cksiow.ai.advisor.assistant.service.AssistantService;
import com.universal.core.library.base.controller.BaseCRUDController;
import com.universal.core.library.exception.BadRequestException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/assistant")
public class AssistantController extends BaseCRUDController<AssistantService, AssistantRepository, AssistantCreate, AssistantResponse> {
    public AssistantController(AssistantService service) {
        super(service);
    }


    //not allow
    @Override
    public List<AssistantResponse> getAll() {
        throw new BadRequestException("Method not allow");
    }

    @Override
    public AssistantCreate insert(AssistantCreate table) {
        throw new BadRequestException("Method not allow");
    }

    @Override
    public boolean deleteById(String id) {
        throw new BadRequestException("Method not allow");
    }

    @Override
    public List<AssistantCreate> insertBatch(List<AssistantCreate> tableList) {
        throw new BadRequestException("Method not allow");
    }

    @Override
    public List<AssistantResponse> search(Optional<String> q) {
        throw new BadRequestException("Method not allow");
    }

    @Override
    public AssistantResponse getById(String id) {
        throw new BadRequestException("Method not allow");
    }

    @PostMapping("/personal")
    public OpenAIAssistantResponse createPersonalAssistant(@RequestBody PersonalAssistantRequest request) {
        return this.getService().createPersonalAssistant(request);
    }

    @GetMapping("/personal")
    public List<OpenAIAssistantResponse> getPersonalAssistant(@RequestParam String uniqueId) {
        return this.getService().getPersonalAssistant(uniqueId);
    }

    @DeleteMapping("/personal")
    public void deletePersonalAssistant(@RequestBody PersonalAssistantDeleteRequest request) {
        this.getService().deletePersonalAssistant(request);
    }

}
