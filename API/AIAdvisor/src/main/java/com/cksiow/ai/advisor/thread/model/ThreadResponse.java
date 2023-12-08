package com.cksiow.ai.advisor.thread.model;

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
@Entity(name = "thread_response")
@Table(name = "thread")
@SuperBuilder
public class ThreadResponse extends ThreadData {
    private static final long serialVersionUID = 1L;


}

