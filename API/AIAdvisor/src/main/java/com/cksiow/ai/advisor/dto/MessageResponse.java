package com.cksiow.ai.advisor.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Data
@SuperBuilder
@MappedSuperclass
public class MessageResponse {

    String threadId;
    String messageId;
    String runId;
    String replyContext;
    String assistantId;
    String uniqueId;
    String role;
    Integer createTimeStamp;
    Integer credit;
    @Builder.Default
    List<String> predictQuestions = new ArrayList<>();
}
