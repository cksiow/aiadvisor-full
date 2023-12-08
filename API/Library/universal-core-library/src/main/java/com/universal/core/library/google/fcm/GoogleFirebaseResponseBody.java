package com.universal.core.library.google.fcm;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleFirebaseResponseBody {
    private static final long serialVersionUID = 1L;
    Long multicast_id;
    Boolean success;
    Boolean failure;
    Long canonical_ids;

    @Builder.Default
    List<GoogleFirebaseResponseResult> results = new ArrayList();
}