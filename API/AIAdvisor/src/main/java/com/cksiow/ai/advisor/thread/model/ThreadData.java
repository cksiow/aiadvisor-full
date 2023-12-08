package com.cksiow.ai.advisor.thread.model;

import com.universal.core.library.base.model.BaseModel;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Data
@SuperBuilder
@MappedSuperclass
public class ThreadData extends BaseModel {
    private static final long serialVersionUID = 1L;
    @Column(updatable = false)
    private String threadId;

    @Column(updatable = false)
    private String subject;
}
