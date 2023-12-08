package com.cksiow.ai.advisor.assistant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class AssistantData extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Column(updatable = false)
    String name;
    @Column(updatable = false)
    String assistantId;


    @JsonIgnore
    String firstInstruction;

    @JsonIgnore
    String instruction;


}
