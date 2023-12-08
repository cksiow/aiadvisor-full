package com.cksiow.ai.advisor.iap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
@Data
@Entity(name = "iap_insert")
@Table(name = "iap")
@SuperBuilder
@JsonIgnoreProperties(value = {"id", "createdBy", "modifyBy", "createDate", "modifyDate"})
public class IAPCreate extends IAPData {
    private static final long serialVersionUID = 1L;
}
