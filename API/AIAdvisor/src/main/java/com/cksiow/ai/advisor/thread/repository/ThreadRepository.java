package com.cksiow.ai.advisor.thread.repository;

import com.cksiow.ai.advisor.thread.model.ThreadCreate;
import com.cksiow.ai.advisor.thread.model.ThreadResponse;
import com.universal.core.library.base.repository.BaseCRUDRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThreadRepository extends BaseCRUDRepository<ThreadCreate, ThreadResponse> {
    @Query("SELECT c FROM thread_insert c"
            + " WHERE c.threadId = ?1")
    ThreadCreate findByThreadId(String threadId);

    @Override
    @Query("SELECT c FROM thread_response c " +
            "WHERE c.threadId LIKE %?1% " +
            "ORDER BY c.id")
    List<ThreadResponse> findSearch(String searchKey);


    @Modifying
    @Query("UPDATE thread_insert t SET t.modifyDate = CURRENT_TIMESTAMP WHERE t.id = ?1")
    void updateModifyDateById(String id);


}
