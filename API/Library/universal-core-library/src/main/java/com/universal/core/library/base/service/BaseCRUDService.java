package com.universal.core.library.base.service;


import com.universal.core.library.base.model.BaseModel;
import com.universal.core.library.base.repository.BaseCRUDRepository;
import com.universal.core.library.pagination.OffsetPageService;
import com.universal.core.library.snowflake.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class BaseCRUDService<R extends BaseCRUDRepository<MS, MR>
        , MS extends BaseModel
        , MR extends BaseModel
        > {

    R repo;

    @Autowired
    OffsetPageService page;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public BaseCRUDService(R repo) {
        this.repo = repo;
    }

    @Autowired
    SnowflakeIdGenerator snowflakeIdGenerator;

    public R getRepository() {
        return repo;
    }

    public OffsetPageService getPagination() {
        return page;
    }

    // search

    public List<MR> search(String searchKey) {
        return repo.findSearch(searchKey);
    }

    // by id select
    public MR findById(String id) {
        return repo.findById(id);
    }

    // add
    // edit
    @Transactional
    public MS save(MS data) {
        var objectBefore = validateBefore(data);
        assignSystemInfo(data);
        validate(data);
        assignId(data);
        return afterSave(repo.saveAndFlush(data), objectBefore);
    }

    public void assignId(BaseModel data) {
        if (data != null) {
            if (data.getId() == null) {
                //only for insert new record need set id
                data.setId(snowflakeIdGenerator.nextId().toString());
            }
        }
    }

    public void assignSystemInfo(BaseModel data) {
        if (data != null) {
            assignCreateInfo(data);
        }
    }

    protected abstract void assignCreateInfo(BaseModel data);

    protected abstract void validateDelete(String id);

    // delete
    @Transactional
    public boolean deleteById(String id) {
        validateDelete(id);
        repo.deleteById(id);
        return true;
    }
    // addBatch
    // editBatch

    // deleteBatch
    @Transactional
    public boolean deleteAll() {
        repo.deleteAll();
        return true;
    }

    public List<MR> findAll() {
        return repo.findAll();
    }

    @Transactional(timeout = 1500)
    public List<MS> saveAll(List<MS> tableList) {
        tableList.forEach(this::validateBefore);
        tableList.forEach(this::assignSystemInfo);
        validateBatchInternal(tableList);
        tableList.forEach(this::assignId);
        return (List<MS>) repo.saveAllAndFlush(tableList);

    }

    private void validateBatchInternal(List<MS> list) {
        list.forEach(this::validate);
        validateBatch(list);
    }

    protected abstract void validateBatch(List<MS> list);

    protected abstract void validate(MS data);

    protected abstract Object validateBefore(MS data);

    protected abstract MS afterSave(MS data, Object beforeSave);

}
