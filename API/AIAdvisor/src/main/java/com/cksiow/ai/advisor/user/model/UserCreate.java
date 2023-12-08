package com.cksiow.ai.advisor.user.model;

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
@Entity(name = "user_insert")
@Table(name = "user")
@SuperBuilder
@JsonIgnoreProperties(value = {"id", "createdBy", "modifyBy", "createDate", "modifyDate", "credit"})
public class UserCreate extends UserData {
    private static final long serialVersionUID = 1L;
}
