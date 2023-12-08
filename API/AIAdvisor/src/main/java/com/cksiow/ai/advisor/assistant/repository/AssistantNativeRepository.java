package com.cksiow.ai.advisor.assistant.repository;

import com.cksiow.ai.advisor.assistant.dto.OpenAIAssistantResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssistantNativeRepository extends org.springframework.data.repository.Repository<OpenAIAssistantResponse, String> {

    @Query(value = "SELECT a.assistantId as id, a.name FROM assistant a " +
            "WHERE (a.createdBy IS NULL OR a.createdBy = ?1) " +
            "ORDER BY a.name", nativeQuery = true)
    List<OpenAIAssistantResponse> findAvailableAssistants(String userId);

    @Query(value = "SELECT a.assistantId as id, a.name FROM assistant a " +
            "ORDER BY a.createDate DESC " +
            "LIMIT 1", nativeQuery = true)
    OpenAIAssistantResponse findLastAssistant();

    @Query(value = "select a.assistantId as id, a.name from assistant a " +
            "where a.createdBy IN (SELECT id FROM user WHERE uniqueId = ?1) " +
            "ORDER BY a.name"
            , nativeQuery = true)
    List<OpenAIAssistantResponse> findByUniqueId(String uniqueId);
}
