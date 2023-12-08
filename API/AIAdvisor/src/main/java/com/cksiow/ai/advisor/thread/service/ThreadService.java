package com.cksiow.ai.advisor.thread.service;


import com.cksiow.ai.advisor.base.BaseAIAdvisorCRUDService;
import com.cksiow.ai.advisor.dto.HttpResult;
import com.cksiow.ai.advisor.dto.MessageResponse;
import com.cksiow.ai.advisor.service.OpenAIThreadService;
import com.cksiow.ai.advisor.thread.model.ThreadCreate;
import com.cksiow.ai.advisor.thread.model.ThreadResponse;
import com.cksiow.ai.advisor.thread.repository.ThreadNativeRepository;
import com.cksiow.ai.advisor.thread.repository.ThreadRepository;
import com.cksiow.ai.advisor.utils.ErrorUtils;
import com.universal.core.library.base.model.BaseModel;
import com.universal.core.library.exception.BadRequestException;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThreadService extends BaseAIAdvisorCRUDService<ThreadRepository, ThreadCreate, ThreadResponse> {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ThreadService threadService;

    @Autowired
    ThreadNativeRepository threadNativeRepository;

    public ThreadService(ThreadRepository repo) {
        super(repo);
    }

    @Autowired
    OpenAIThreadService openAIThreadService;

    @Override
    protected void assignCreateInfo(BaseModel data) {

    }

    @Override
    protected void validateDelete(String id) {

    }

    @Override
    protected void validateBatch(List<ThreadCreate> list) {

    }

    @Override
    protected void validate(ThreadCreate data) {
        if (data.getThreadId() == null) {
            throw new BadRequestException("thread id not allow null");
        }
        if (data.getCreatedBy() == null) {
            throw new BadRequestException("create user id not allow null");
        }
    }

    @Override
    protected Object validateBefore(ThreadCreate data) {

        return data;
    }

    @Override
    protected ThreadCreate afterSave(ThreadCreate data, Object beforeSave) {
        return data;
    }

    @Override
    public ThreadCreate save(ThreadCreate data) {
        //check if the unique Id exists
        var exists = this.getRepository().findByThreadId(data.getThreadId());
        if (exists == null) {
            return super.save(data);
        } else {
            //update updated date
            this.getRepository().updateModifyDateById(exists.getId());
            return exists;
        }

    }

    public ThreadCreate findByThreadId(String threadId) {
        return this.getRepository().findByThreadId(threadId);
    }

    public List<ThreadResponse> findByUniqueId(String uniqueId) {
        return this.threadNativeRepository.findByUniqueId(uniqueId);
    }

    public ThreadResponse findLastThreadByUniqueId(String uniqueId) {
        return this.threadNativeRepository.findLastThreadByUniqueId(uniqueId);
    }

    @SneakyThrows
    public JSONObject getThreadRunStatus(MessageResponse response) {
        var status = "in_progress";
        int tryTime = 0;
        boolean inProgress = false;
        JSONObject jsonObject = null;
        HttpResult httpResult = null;
        //once it is not in-progress, then
        while (status.equals("in_progress")) {
            if ((inProgress && tryTime >= 60) || (!inProgress && tryTime >= 5)) {
                logger.error("threadId:runId {}:{} is retry {} times, inProgress: {}, terminating"
                        , response.getThreadId(), response.getRunId(), tryTime, inProgress);
                openAIThreadService.cancelRun(response);
                throw new BadRequestException("Time out, please try again");
            }
            httpResult = openAIThreadService.getRunResult(response);
            ErrorUtils.throwIfMessageFailSubmit(httpResult, "getThreadRunStatus getRunId: {}", "The request cannot be process. Please try again.");
            jsonObject = new JSONObject(httpResult.getContent());
            if (jsonObject.getJSONArray("data").isEmpty()) {
                logger.info("threadId:runId {}:{} not yet starting, wait and continue, time: {}", response.getThreadId(), response.getRunId(), tryTime);
                Thread.sleep(3000);
                tryTime += 1;
                continue;
            }
            status = jsonObject.getJSONArray("data").getJSONObject(0).getString("status");
            if (status.equals("in_progress")) {
                inProgress = true;
                logger.info("threadId:runId {}:{} still in progress, time: {}", response.getThreadId(), response.getRunId(), tryTime);
                // Sleep for a while to avoid making too many requests in a short time
                Thread.sleep(3000); // Sleep for 1 second (adjust as needed)
            }
            tryTime += 1;
        }
        //if not completed then throw bad request
        if (!status.equals("completed")) {
            logger.error("status not completed: {}", httpResult);
            throw new BadRequestException("The message cannot be submitted. Please try again.");
        }
        return jsonObject;
    }


}