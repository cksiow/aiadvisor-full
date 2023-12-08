package com.cksiow.ai.advisor.thread.controller;


import com.cksiow.ai.advisor.thread.model.ThreadCreate;
import com.cksiow.ai.advisor.thread.model.ThreadResponse;
import com.cksiow.ai.advisor.thread.repository.ThreadRepository;
import com.cksiow.ai.advisor.thread.service.ThreadService;
import com.universal.core.library.base.controller.BaseCRUDController;
import com.universal.core.library.exception.BadRequestException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/thread")
public class ThreadController extends BaseCRUDController<ThreadService, ThreadRepository, ThreadCreate, ThreadResponse> {
    public ThreadController(ThreadService service) {
        super(service);
    }


    //not allow
    @Override
    public List<ThreadResponse> getAll() {
        throw new BadRequestException("Method not allow");
    }

    @Override
    public ThreadCreate insert(ThreadCreate table) {
        throw new BadRequestException("Method not allow");
    }

    @Override
    public boolean deleteById(String id) {
        throw new BadRequestException("Method not allow");
    }

    @Override
    public List<ThreadCreate> insertBatch(List<ThreadCreate> tableList) {
        throw new BadRequestException("Method not allow");
    }

    @Override
    public List<ThreadResponse> search(Optional<String> q) {
        throw new BadRequestException("Method not allow");
    }

    @Override
    public ThreadResponse getById(String id) {
        throw new BadRequestException("Method not allow");
    }

    @GetMapping("/byUniqueId")
    public List<ThreadResponse> findByUniqueId(@RequestParam String uniqueId) {
        return this.getService().findByUniqueId(uniqueId);
    }

    @GetMapping("/last/byUniqueId")
    public ThreadResponse findLastThreadByUniqueId(@RequestParam String uniqueId) {
        return this.getService().findLastThreadByUniqueId(uniqueId);
    }
}
