package com.cksiow.ai.advisor.thread.repository;

import com.cksiow.ai.advisor.thread.model.ThreadResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThreadNativeRepository extends org.springframework.data.repository.Repository<ThreadResponse, String> {

    @Query(value = "select t.* from thread t " +
            "where t.createdBy IN (SELECT id FROM user WHERE uniqueId = ?1) " +
            "ORDER BY t.ModifyDate DESC LIMIT 1"
            , nativeQuery = true)
    ThreadResponse findLastThreadByUniqueId(String uniqueId);

    @Query(value = "select t.* from thread t " +
            "where t.createdBy IN (SELECT id FROM user WHERE uniqueId = ?1) " +
            "ORDER BY t.ModifyDate DESC"
            , nativeQuery = true)
    List<ThreadResponse> findByUniqueId(String userId);
}
