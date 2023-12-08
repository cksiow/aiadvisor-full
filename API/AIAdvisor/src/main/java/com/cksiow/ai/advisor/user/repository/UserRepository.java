package com.cksiow.ai.advisor.user.repository;

import com.cksiow.ai.advisor.user.model.UserCreate;
import com.cksiow.ai.advisor.user.model.UserResponse;
import com.universal.core.library.base.repository.BaseCRUDRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends BaseCRUDRepository<UserCreate, UserResponse> {
    @Query("SELECT c FROM user_insert c"
            + " WHERE c.uniqueId = ?1")
    UserCreate findByUniqueId(String uniqueId);

    @Override
    @Query("SELECT c FROM user_response c " +
            "WHERE c.uniqueId LIKE %?1% " +
            "ORDER BY c.id")
    List<UserResponse> findSearch(String searchKey);

    @Modifying
    @Query("UPDATE user_insert u SET u.credit = u.credit - 1, u.usedCredit = u.usedCredit + 1 WHERE u.id = :id")
    void deductCredit(@Param("id") String id);

    @Modifying
    @Query("UPDATE user_insert u SET u.credit = u.credit + :credit WHERE u.id = :id")
    void increaseCredit(@Param("id") String id, @Param("credit") int credit);

    @Modifying
    @Query("UPDATE user_insert u SET u.modifyDate = CURRENT_TIMESTAMP, u.buildNumber = ?2, u.languageCode = ?3 WHERE u.id = ?1")
    void updateById(String id, String buildNumber, String languageCode);
}
