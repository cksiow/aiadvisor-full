package com.cksiow.ai.advisor.iap.service;


import com.cksiow.ai.advisor.base.BaseAIAdvisorCRUDService;
import com.cksiow.ai.advisor.iap.dto.IAPPurchaseRequest;
import com.cksiow.ai.advisor.iap.model.IAPCreate;
import com.cksiow.ai.advisor.iap.model.IAPResponse;
import com.cksiow.ai.advisor.iap.repository.IAPRepository;
import com.cksiow.ai.advisor.user.model.UserCreate;
import com.cksiow.ai.advisor.user.service.UserService;
import com.universal.core.library.base.model.BaseModel;
import com.universal.core.library.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IAPService extends BaseAIAdvisorCRUDService<IAPRepository, IAPCreate, IAPResponse> {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IAPService iapService;

    @Autowired
    UserService userService;

    public IAPService(IAPRepository repo) {
        super(repo);
    }

    @Override
    protected void assignCreateInfo(BaseModel data) {

    }

    @Override
    protected void validateDelete(String id) {

    }

    @Override
    protected void validateBatch(List<IAPCreate> list) {

    }

    @Override
    protected void validate(IAPCreate data) {
        if (data.getToken() == null) {
            throw new BadRequestException("Token not allow null");
        }
        if (data.getProductId() == null) {
            throw new BadRequestException("Product id not allow null");
        }
    }

    @Override
    protected Object validateBefore(IAPCreate data) {

        return data;
    }

    @Override
    protected IAPCreate afterSave(IAPCreate data, Object beforeSave) {
        return data;
    }

    @Override
    public IAPCreate save(IAPCreate data) {
        //check if the unique Id exists
        var exists = this.getRepository().findByToken(data.getToken());
        if (exists == null) {
            return super.save(data);
        } else {
            return exists;
        }

    }

    @Transactional(rollbackFor = Throwable.class)
    public void purchaseRequest(IAPPurchaseRequest request) {
        //get user by uniqueId
        var user = userService.findByUniqueId(request.getUniqueId());
        //validate
        validatePurchaseRequest(user);
        //save the iap
        this.save(
                IAPCreate.builder()
                        .token(request.getToken())
                        .productId(request.getProductId())
                        .quality(request.getQuality())
                        .createdBy(user.getId())
                        .modifyBy(user.getId())
                        .build()
        );
        //increase the credit point based on product id
        var credit = Integer.parseInt(request.getProductId().replaceAll("_credits", "")) * request.getQuality();
        userService.increaseCredit(user.getId(), credit);
    }

    private void validatePurchaseRequest(UserCreate user) {
        if (user == null) {
            throw new BadRequestException("Your device is not registered in our system.");
        }
    }
}