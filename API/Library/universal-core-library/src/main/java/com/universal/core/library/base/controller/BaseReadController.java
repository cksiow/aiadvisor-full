package com.universal.core.library.base.controller;


import com.universal.core.library.base.model.BaseModel;
import com.universal.core.library.base.repository.BaseCRUDRepository;
import com.universal.core.library.base.service.BaseCRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

public abstract class BaseReadController<S extends BaseCRUDService<R, MS, MR>, R extends BaseCRUDRepository<MS, MR>
        , MS extends BaseModel
        , MR extends BaseModel
        > {

    S service;

    @Autowired
    public BaseReadController(S service) {
        this.service = service;
    }

    public S getService() {
        return service;
    }

    // all
    @GetMapping()
    public List<MR> getAll() {
        return service.findAll();
    }

    // search
    @GetMapping(value = {"/search"})
    public List<MR> search(@RequestParam("q") Optional<String> q) {
        return service.search(q.orElse(""));
    }

    // by id select
    @GetMapping(value = "/{id}")
    public MR getById(@PathVariable("id") String id) {
        return service.findById(id);
    }

}
