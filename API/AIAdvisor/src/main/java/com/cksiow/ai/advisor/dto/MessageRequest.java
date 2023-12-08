package com.cksiow.ai.advisor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;

@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Data
@SuperBuilder
@MappedSuperclass
public class MessageRequest {

    String threadId;
    String assistantId;
    String messageContext;

    String uniqueId;
}
