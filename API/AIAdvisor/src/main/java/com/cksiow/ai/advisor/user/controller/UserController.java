package com.cksiow.ai.advisor.user.controller;


import com.cksiow.ai.advisor.user.model.UserCreate;
import com.cksiow.ai.advisor.user.model.UserResponse;
import com.cksiow.ai.advisor.user.repository.UserRepository;
import com.cksiow.ai.advisor.user.service.UserService;
import com.universal.core.library.base.controller.BaseCRUDController;
import com.universal.core.library.exception.BadRequestException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController extends BaseCRUDController<UserService, UserRepository, UserCreate, UserResponse> {
    public UserController(UserService service) {
        super(service);
    }


    //not allow
    @Override
    public List<UserResponse> getAll() {
        throw new BadRequestException("Method not allow");
    }


    @PostMapping("/rewarded")
    public void rewardUser(@RequestParam String uniqueId) {
        this.getService().rewardUser(uniqueId);
    }
}
