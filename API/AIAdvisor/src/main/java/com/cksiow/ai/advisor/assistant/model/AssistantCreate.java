package com.cksiow.ai.advisor.assistant.model;

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
@Entity(name = "assistant_insert")
@Table(name = "assistant")
@SuperBuilder
@JsonIgnoreProperties(value = {"id", "createdBy", "modifyBy", "createDate", "modifyDate", "firstInstruction"})
public class AssistantCreate extends AssistantData {
    private static final long serialVersionUID = 1L;
}
