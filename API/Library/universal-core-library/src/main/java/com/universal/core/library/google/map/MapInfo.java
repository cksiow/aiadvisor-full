package com.universal.core.library.google.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MapInfo {
    private String placeName;
    private String placeId;
    private List<String> types;

    private Double latitude;
    private Double longitude;

    private Double distance;


}
