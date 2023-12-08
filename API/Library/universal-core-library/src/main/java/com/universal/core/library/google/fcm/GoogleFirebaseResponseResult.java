package com.universal.core.library.google.fcm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleFirebaseResponseResult {
    private static final long serialVersionUID = 1L;

    private String error;

    private String message_id;


}