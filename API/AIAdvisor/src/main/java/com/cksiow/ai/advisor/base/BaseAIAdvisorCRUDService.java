package com.cksiow.ai.advisor.base;

import com.universal.core.library.base.model.BaseModel;
import com.universal.core.library.base.repository.BaseCRUDRepository;
import com.universal.core.library.base.service.BaseCRUDService;

public abstract class BaseAIAdvisorCRUDService<R extends BaseCRUDRepository<MS, MR>
        , MS extends BaseModel
        , MR extends BaseModel
        > extends BaseCRUDService<R, MS, MR> {

    public BaseAIAdvisorCRUDService(R repo) {
        super(repo);
    }
    
}
