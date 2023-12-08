package com.universal.core.library.base.repository;

import com.universal.core.library.base.model.BaseModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;

@NoRepositoryBean
public interface BaseCRUDRepository<MS extends BaseModel, MR extends BaseModel> extends Repository<MS, String> {

    List<MR> findSearch(String searchKey);

    //why we use @query but not let spring data auto generate the query?
    //because if auto generate the query, it will return MS type instead of MR type due the generic type we pass is MS
    @Query("SELECT c FROM #{#entityName} c"
            + " WHERE c.id = ?1")
    MR findById(String var1);

    <S extends MS> S save(S var1);

    <S extends MS> Iterable<S> saveAll(Iterable<S> var1);

    boolean existsById(String var1);

    @Query("SELECT c FROM #{#entityName} c")
    List<MR> findAll();

    long count();

    void deleteById(String var1);

    void delete(MS var1);

    void deleteAll(Iterable<? extends MS> var1);

    void deleteAll();


    <S extends MS> S saveAndFlush(S var1);

    <S extends MS> Iterable<S> saveAllAndFlush(Iterable<S> var1);

}
