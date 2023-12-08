package com.universal.core.library.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
@SuperBuilder
@MappedSuperclass
public class BaseModel implements Serializable {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "createdby")
    private String createdBy;

    @Column(name = "modifyby")
    private String modifyBy;


    @Column(name = "createdate", insertable = false, updatable = false)
    private Instant createDate;

    @Column(name = "modifydate", insertable = false)
    private Instant modifyDate;

    @PreUpdate
    protected void onUpdate() {
        modifyDate = Instant.now().plus(ZonedDateTime.now().getOffset().getTotalSeconds() / 60 / 60, ChronoUnit.HOURS);
    }

    public BaseModel(BaseModel data) {
        this.setCreatedBy(data.getCreatedBy());
        this.setModifyBy(data.getModifyBy());
        this.setCreateDate(data.getCreateDate());
        this.setModifyDate(data.getModifyDate());
    }
}
