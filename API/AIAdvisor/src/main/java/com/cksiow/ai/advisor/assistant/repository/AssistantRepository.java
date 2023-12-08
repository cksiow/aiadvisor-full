package com.cksiow.ai.advisor.assistant.repository;

import com.cksiow.ai.advisor.assistant.model.AssistantCreate;
import com.cksiow.ai.advisor.assistant.model.AssistantResponse;
import com.universal.core.library.base.repository.BaseCRUDRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssistantRepository extends BaseCRUDRepository<AssistantCreate, AssistantResponse> {
    @Query("SELECT c FROM assistant_insert c"
            + " WHERE c.assistantId = ?1")
    AssistantCreate findByAssistantId(String uniqueId);

    @Override
    @Query("SELECT c FROM assistant_response c " +
            "WHERE c.name LIKE %?1% " +
            "ORDER BY c.id")
    List<AssistantResponse> findSearch(String searchKey);

    @Query("SELECT c FROM assistant_response c " +
            "WHERE c.createdBy IS NULL " +
            "ORDER BY c.name")
    List<AssistantResponse> findSystemAssistants();
}
