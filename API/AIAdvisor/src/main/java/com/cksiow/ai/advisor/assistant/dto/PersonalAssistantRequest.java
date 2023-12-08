package com.cksiow.ai.advisor.assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;


@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@Data
@SuperBuilder
public class PersonalAssistantRequest {
    String keywords;
    String uniqueId;
}

