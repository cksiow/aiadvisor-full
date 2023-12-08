package com.universal.core.library.google.auto.complete;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Prediction {
    private String description;

    @JsonProperty("place_id")
    private String placeId;

    @Builder.Default
    List<String> types = new ArrayList<>();
}
