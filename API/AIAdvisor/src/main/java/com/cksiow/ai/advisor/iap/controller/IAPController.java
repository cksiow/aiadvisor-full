package com.cksiow.ai.advisor.iap.controller;


import com.cksiow.ai.advisor.iap.dto.IAPPurchaseRequest;
import com.cksiow.ai.advisor.iap.model.IAPCreate;
import com.cksiow.ai.advisor.iap.model.IAPResponse;
import com.cksiow.ai.advisor.iap.repository.IAPRepository;
import com.cksiow.ai.advisor.iap.service.IAPService;
import com.universal.core.library.base.controller.BaseCRUDController;
import com.universal.core.library.exception.BadRequestException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/iap")
public class IAPController extends BaseCRUDController<IAPService, IAPRepository, IAPCreate, IAPResponse> {
    public IAPController(IAPService service) {
        super(service);
    }


    //not allow
    @Override
    public List<IAPResponse> getAll() {
        throw new BadRequestException("Method not allow");
    }

    @Override
    public IAPCreate insert(IAPCreate table) {
        throw new BadRequestException("Method not allow");
    }

    @Override
    public boolean deleteById(String id) {
        throw new BadRequestException("Method not allow");
    }

    @Override
    public List<IAPCreate> insertBatch(List<IAPCreate> tableList) {
        throw new BadRequestException("Method not allow");
    }

    @Override
    public List<IAPResponse> search(Optional<String> q) {
        throw new BadRequestException("Method not allow");
    }

    @Override
    public IAPResponse getById(String id) {
        throw new BadRequestException("Method not allow");
    }

    @PostMapping("/token")
    public void purchaseRequest(@RequestBody IAPPurchaseRequest request) {
        this.getService().purchaseRequest(request);
    }

}
