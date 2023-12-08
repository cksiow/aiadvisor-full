package com.cksiow.ai.advisor.user.model;

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
@Entity(name = "user_response")
@Table(name = "user")
@SuperBuilder
public class UserResponse extends UserData {
    private static final long serialVersionUID = 1L;


}

