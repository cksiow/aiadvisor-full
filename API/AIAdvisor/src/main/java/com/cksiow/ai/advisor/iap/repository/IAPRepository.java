package com.cksiow.ai.advisor.iap.repository;

import com.cksiow.ai.advisor.iap.model.IAPCreate;
import com.cksiow.ai.advisor.iap.model.IAPResponse;
import com.universal.core.library.base.repository.BaseCRUDRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAPRepository extends BaseCRUDRepository<IAPCreate, IAPResponse> {


    @Override
    @Query("SELECT c FROM iap_response c " +
            "WHERE c.productId LIKE %?1% " +
            "ORDER BY c.id")
    List<IAPResponse> findSearch(String searchKey);


    @Query("SELECT c FROM iap_insert c"
            + " WHERE c.token = ?1")
    IAPCreate findByToken(String token);
}
