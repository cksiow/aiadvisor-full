package com.universal.core.library.base.controller;


import com.universal.core.library.base.model.BaseModel;
import com.universal.core.library.base.repository.BaseCRUDRepository;
import com.universal.core.library.base.service.BaseCRUDService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public abstract class BaseCRUDController<S extends BaseCRUDService<R, MS, MR>, R extends BaseCRUDRepository<MS, MR>
        , MS extends BaseModel
        , MR extends BaseModel
        >
        extends BaseReadController<S, R, MS, MR> {


    public BaseCRUDController(S service) {
        super(service);
    }

    // add + edit
    @PostMapping()
    public MS insert(@RequestBody MS table) {
        return service.save(table);
    }

    // delete
    @DeleteMapping("/{id:[a-f0-9 ]+}")
    public boolean deleteById(@PathVariable("id") String id) {
        return service.deleteById(id);
    }

    // add + edit batch
    @PostMapping("/list")
    public List<MS> insertBatch(@RequestBody List<MS> tableList) {
        return service.saveAll(tableList);
    }

}
