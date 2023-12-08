package com.cksiow.ai.advisor.user.service;


import com.cksiow.ai.advisor.base.BaseAIAdvisorCRUDService;
import com.cksiow.ai.advisor.user.model.UserCreate;
import com.cksiow.ai.advisor.user.model.UserResponse;
import com.cksiow.ai.advisor.user.repository.UserRepository;
import com.universal.core.library.base.model.BaseModel;
import com.universal.core.library.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService extends BaseAIAdvisorCRUDService<UserRepository, UserCreate, UserResponse> {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;

    public UserService(UserRepository repo) {
        super(repo);
    }

    @Override
    protected void assignCreateInfo(BaseModel data) {

    }

    @Override
    protected void validateDelete(String id) {

    }

    @Override
    protected void validateBatch(List<UserCreate> list) {

    }

    @Override
    protected void validate(UserCreate data) {
        if (data.getUniqueId() == null) {
            throw new BadRequestException("Unable to detect your device information. You may try resolving the issue by restarting the application.");
        }
    }

    @Override
    protected Object validateBefore(UserCreate data) {

        return data;
    }

    @Override
    protected UserCreate afterSave(UserCreate data, Object beforeSave) {
        return data;
    }

    @Override
    public UserCreate save(UserCreate data) {
        //check if the unique Id exists
        var exists = this.getRepository().findByUniqueId(data.getUniqueId());
        if (exists == null) {
            return super.save(data);
        } else {
            //update the modify date and build number
            this.getRepository().updateById(exists.getId(), data.getBuildNumber(), data.getLanguageCode());
            return exists;
        }

    }

    public UserCreate findByUniqueId(String uniqueId) {
        return this.getRepository().findByUniqueId(uniqueId);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deductCredit(String id) {
        this.getRepository().deductCredit(id);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void increaseCredit(String id, int credit) {
        this.getRepository().increaseCredit(id, credit);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void rewardUser(String uniqueId) {
        var user = this.findByUniqueId(uniqueId);
        if (user != null) {
            this.increaseCredit(user.getId(), 1);
            logger.info("{} earning 1 credit", uniqueId);
        }
    }
}