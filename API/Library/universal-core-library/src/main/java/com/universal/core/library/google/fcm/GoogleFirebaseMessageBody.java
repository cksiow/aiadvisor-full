package com.universal.core.library.google.fcm;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleFirebaseMessageBody {
    private static final long serialVersionUID = 1L;

    private String to;

    @Builder.Default
    private Boolean delay_while_idle = false;

    @Builder.Default
    private String priority = "high";

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private GoogleFirebaseMessageBodyNotification notification = null;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data = null;

}