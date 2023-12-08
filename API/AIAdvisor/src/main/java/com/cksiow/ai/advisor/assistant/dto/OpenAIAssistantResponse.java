package com.cksiow.ai.advisor.assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Id;


@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Data
@SuperBuilder
@Entity(name = "assistant_openai_response")
public class OpenAIAssistantResponse {
    //this is assistantId!
    @Id
    String id;
    String name;
}

