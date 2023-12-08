package com.cksiow.ai.advisor.assistant.model;

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
@Entity(name = "assistant_response")
@Table(name = "assistant")
@SuperBuilder
public class AssistantResponse extends AssistantData {
    private static final long serialVersionUID = 1L;


}

