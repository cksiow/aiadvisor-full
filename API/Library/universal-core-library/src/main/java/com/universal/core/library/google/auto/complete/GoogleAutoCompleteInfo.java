package com.universal.core.library.google.auto.complete;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleAutoCompleteInfo {
    private String status;
    @Builder.Default
    List<Prediction> predictions = new ArrayList<>();
}
