package com.universal.core.library.google;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universal.core.library.google.auto.complete.GoogleAutoCompleteInfo;
import com.universal.core.library.google.map.MapInfo;
import com.universal.core.library.utils.HttpClientHelper;
import com.universal.core.library.utils.LocationHelper;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class GoogleMapHelper {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${google.search.url:https://www.google.com}")
    String googleSearchUrl;
    @Value("${google.api.url:https://www.googleapis.com}")
    String googleAPIUrl;

    //!1d3000 3000 around 500m, div 6
    private String pbTemplate = "!4m12!1m3!1d3000!2d{lon}!3d{lat}";
    private String pbArgs = "!2m3!1f0!2f0!3f0!3m2!1i1920!2i457!4f13.1!7i" +
            "{recordNumber}" +
            "!8i0!10b1!12m8!1m1!18b1!2m3!5m1!6e2!20e3!10b1!16b1!19m4!2m3!1i360!2i120!4i8!20m57!2m2!1i203!2i100!3m2!2i4!5b1!6m6!1m2" +
            "!1i86!2i86!1m2!1i408!2i240!7m42!1m3!1e1!2b0!3e3!1m3!1e2!2b1!3e2!1m3!1e2!2b0!3e3!1m3!1e8!2b0!3e3!1m3!1e10" +
            "!2b0!3e3!1m3!1e10!2b1!3e2!1m3!1e9!2b1!3e2!1m3!1e10!2b0!3e3!1m3!1e10!2b1!3e2!1m3!1e10!2b0!3e4!2b1!4b1!9b0" +
            "!22m6!1sEMNxY7L3M8Cxz7sP-Z2A6Ag:2306!2s1i:0,t:20588,p:EMNxY7L3M8Cxz7sP-Z2A6Ag:2306!4m1!2i20588!7e81" +
            "!12e3!24m71!1m23!13m9!2b1!3b1!4b1!6i1!8b1!9b1!14b1!20b1!25b1!18m12!3b1!4b1!5b1!6b1!9b1!12b1!13b1!14b1!15b1!17b1!20b1" +
            "!21b1!2b1!5m5!2b1!3b1!5b1!6b1!7b1!10m" +
            "{placeType}!8e3!14m1!3b1!17b1!20m2!1e3!1e6!24b1!25b1!26b1!29b1!30m1!2b1!36b1!39m3!2m2!2i1!3i1" +
            "!43b1!52b1!54m1!1b1!55b1!56m2!1b1!3b1!65m5!3m4!1m3!1m2!1i224!2i298!71b1!72m4!1m2!3b1!5b1!4b1!89b1!26m4!2m3!1i80!2i92!4i8!30m28" +
            "!1m6!1m2!1i0!2i0!2m2!1i458!2i457!1m6!1m2!1i1870!2i0!2m2!1i1920!2i457!1m6!1m2!1i0!2i0!2m2!1i1920!2i20!1m6!1m2!1i0!2i437!2m2!1i1920!2i457" +
            "!31b1!34m18!2b1!3b1!4b1!6b1!8m6!1b1!3b1!4b1!5b1!6b1!7b1!9b1!12b1!14b1!20b1!23b1!25b1!26b1!37m1!1e81!42b1!46m1!1e14!47m0!49m5!3b1!6m1!1b1!7m1!1e3" +
            "!50m26!1m21!2m7!1u3!4sOpen now!5e1!9s0ahUKEwiUj7OY9qz7AhXnUGwGHQwwByoQ_KkBCIQBKAU!10m2!3m1!1e1!2m7!1u2!4sTop rated" +
            "!5e1!9s0ahUKEwiUj7OY9qz7AhXnUGwGHQwwByoQ_KkBCIUBKAY!10m2!2m1!1e1!3m1!1u3!3m1!1u2!4BIAE!2e2!3m2!1b1!3b1!59BQ2dBd0Fn!67m2!7b1!10b1!69i62";

    //below can found jalan kajang impian but no nearby
            /*
            "!2m3!1f0!2f0!3f0!3m2!1i1920!2i342!4f13.1!7i" +
            "{recordNumber}" +
            "!10b1!12m8!1m1!18b1!2m3!5m1!6e2!20e3!10b1!16b1!19m4!2m3!1i360!2i120!4i8!20m57!2m2!1i203!2i100!3m2!2i4!5b1!6m6!1m2
            !1i86!2i86!1m2!1i408!2i240!7m42!1m3!1e1!2b0!3e3!1m3!1e2!2b1!3e2!1m3!1e2!2b0!3e3!1m3!1e8!2b0!3e3!1m3!1e10
            !2b0!3e3!1m3!1e10!2b1!3e2!1m3!1e9!2b1!3e2!1m3!1e10!2b0!3e3!1m3!1e10!2b1!3e2!1m3!1e10!2b0!3e4!2b1!4b1!9b0
            !22m6!1s-Ft0Y-TRNbic4-EP4cG-2A4%3A1520!2s1i%3A3%2Ct%3A11887%2Ce%3A2%2Cp%3A-Ft0Y-TRNbic4-EP4cG-2A4%3A1520!7e81
            !12e3!17s-Ft0Y-TRNbic4-EP4cG-2A4%3A1823!18e15!24m71!1m23!13m9!2b1!3b1!4b1!6i1!8b1!9b1!14b1!20b1!25b1!18m12!3b1!4b1!5b1!6b1!9b1!12b1!13b1!14b1!15b1!17b1!20b1!21b1!2b1!5m5!2b1!3b1!5b1!6b1!7b1!10m1!8e3!14m1!3b1!17b1!20m2!1e3!1e6!24b1!25b1!26b1!29b1!30m1!2b1!36b1!39m3!2m2!2i1!3i1!43b1!52b1!54m1!1b1!55b1!56m2!1b1!3b1!65m5!3m4!1m3!1m2!1i224!2i298!71b1!72m4!1m2!3b1!5b1!4b1!89b1!26m4!2m3!1i80!2i92!4i8!27m5!1s!2s" +
            "{lat}" +
            "%2C+" +
            "{lon}" +
            "!4m2!3d" +
            "{lat}" +
            "!4d" +
            "{lon}" +
            "!30m28!1m6!1m2!1i0!2i0!2m2!1i458!2i342!1m6!1m2!1i1870!2i0!2m2!1i1920!2i342!1m6!1m2!1i0!2i0!2m2!1i1920!2i20!1m6!1m2!1i0!2i322!2m2!1i1920!2i342!34m18!2b1!3b1!4b1!6b1!8m6!1b1!3b1!4b1!5b1!6b1!7b1!9b1!12b1!14b1!20b1!23b1!25b1!26b1!37m1!1e81!42b1!47m0!49m5!3b1!6m1!1b1!7m1!1e3!50m4!2e2!3m2!1b1!3b1!67m2!7b1!10b1!69i627";
        */
    //below can find nearby but cannot find jalan kajang impian
  /*
  "!2m3!1f0!2f0!3f0!3m2!1i1920!2i457!4f13.1!7i" +
            "{recordNumber}" +
            "!8i0!10b1!12m8!1m1!18b1!2m3!5m1!6e2!20e3!10b1!16b1!19m4!2m3!1i360!2i120!4i8!20m57!2m2!1i203!2i100!3m2!2i4!5b1!6m6!1m2!1i86!2i86!1m2!1i408!2i240!7m42!1m3!1e1!2b0!3e3!1m3!1e2!2b1!3e2!1m3!1e2!2b0!3e3!1m3!1e8!2b0!3e3!1m3!1e10!2b0!3e3!1m3!1e10!2b1!3e2!1m3!1e9!2b1!3e2!1m3!1e10!2b0!3e3!1m3!1e10!2b1!3e2!1m3!1e10!2b0!3e4!2b1!4b1!9b0!22m6!1sEMNxY7L3M8Cxz7sP-Z2A6Ag:2306!2s1i:0,t:20588,p:EMNxY7L3M8Cxz7sP-Z2A6Ag:2306!4m1!2i20588!7e81!12e3!24m71!1m23!13m9!2b1!3b1!4b1!6i1!8b1!9b1!14b1!20b1!25b1!18m12!3b1!4b1!5b1!6b1!9b1!12b1!13b1!14b1!15b1!17b1!20b1!21b1!2b1!5m5!2b1!3b1!5b1!6b1!7b1!10m1!8e3!14m1!3b1!17b1!20m2!1e3!1e6!24b1!25b1!26b1!29b1!30m1!2b1!36b1!39m3!2m2!2i1!3i1!43b1!52b1!54m1!1b1!55b1!56m2!1b1!3b1!65m5!3m4!1m3!1m2!1i224!2i298!71b1!72m4!1m2!3b1!5b1!4b1!89b1!26m4!2m3!1i80!2i92!4i8!30m28!1m6!1m2!1i0!2i0!2m2!1i458!2i457!1m6!1m2!1i1870!2i0!2m2!1i1920!2i457!1m6!1m2!1i0!2i0!2m2!1i1920!2i20!1m6!1m2!1i0!2i437!2m2!1i1920!2i457!31b1!34m18!2b1!3b1!4b1!6b1!8m6!1b1!3b1!4b1!5b1!6b1!7b1!9b1!12b1!14b1!20b1!23b1!25b1!26b1!37m1!1e81!42b1!46m1!1e14!47m0!49m5!3b1!6m1!1b1!7m1!1e3!50m26!1m21!2m7!1u3!4sOpen now!5e1!9s0ahUKEwiUj7OY9qz7AhXnUGwGHQwwByoQ_KkBCIQBKAU!10m2!3m1!1e1!2m7!1u2!4sTop rated!5e1!9s0ahUKEwiUj7OY9qz7AhXnUGwGHQwwByoQ_KkBCIUBKAY!10m2!2m1!1e1!3m1!1u3!3m1!1u2!4BIAE!2e2!3m2!1b1!3b1!59BQ2dBd0Fn!67m2!7b1!10b1!69i62";

   */
    public List<MapInfo> getNearBy(String key, Double lon, Double lat, Integer recordNumber, Integer placeType) {
        List<JSONArray> allPlace = new ArrayList<>();
        List<MapInfo> mapInfo = new ArrayList<>();
        try {
            JSONArray allData = new JSONArray();

            var data = getMapJsonData(key, lon, lat, recordNumber, placeType);
            allData.putAll(data);

            if (allData.length() > 0) {
                for (int i = 0; i < allData.length(); i++) {
                    var thisData = ((JSONArray) allData.get(i));
                    if (thisData.length() > 14 && !((JSONArray) allData.get(i)).get(14).toString().equalsIgnoreCase("null")) {
                        allPlace.add(((JSONArray) ((JSONArray) allData.get(i)).get(14)));
                    }

                }
                for (JSONArray place : allPlace) {
                    var latPlace = ((BigDecimal) ((JSONArray) place.get(9)).get(2)).doubleValue();
                    var lonPlace = ((BigDecimal) ((JSONArray) place.get(9)).get(3)).doubleValue();

                    mapInfo.add(MapInfo.builder()
                            .placeName(place.get(11).toString())
                            .placeId(place.get(78).toString())
                            .types(place.get(13).toString().equalsIgnoreCase("null") ? Collections.emptyList() : convertStringList(((JSONArray) place.get(13))))
                            .latitude(latPlace)
                            .longitude(lonPlace)
                            .distance(LocationHelper.distance(lat, lon,
                                    latPlace, lonPlace))
                            .build());
                }
            }
            return mapInfo;
        } catch (Exception e) {
            logger.error("error when get map info: ", e);
            return null;
        }
    }

    private JSONArray getMapJsonData(String key, Double lon, Double lat, Integer recordNumber, Integer placeType) throws UnsupportedEncodingException {
        JSONArray allData;
        String baseUrl = googleSearchUrl + "/search?tbm=map&" +
                "q={q}" +
                "&pb={pb}";
        var url = baseUrl.replace("{q}", URLEncoder.encode(key, StandardCharsets.UTF_8.toString()))
                .replace("{pb}", URLEncoder.encode(
                        pbTemplate.replace("{lon}", lon.toString()).replace("{lat}", lat.toString())
                                + pbArgs.replace("{lon}", lon.toString()).replace("{lat}", lat.toString())
                                .replace("{recordNumber}", recordNumber.toString())
                                //{placetype} 1 = place, 2 = road
                                .replace("{placeType}", placeType.toString())

                        , StandardCharsets.UTF_8.toString())

                );
        logger.info("google map url: {}", url);
        var data = HttpClientHelper.get(url).body();

        var jsonArray = new JSONArray(data.split("\n")[1]);
        allData = ((JSONArray) ((JSONArray) jsonArray.get(0)).get(1));
        return allData;
    }

    @SneakyThrows
    public GoogleAutoCompleteInfo getAutoCompleteInfo(String key, String input,
                                                      Double lat, Double lon
            , Integer radius, ObjectMapper objectMapper) {
        Map<Object, Object> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla");
        var autoCompleteInfo = HttpClientHelper.get(googleAPIUrl + "/maps/api/place/autocomplete/json?" +
                        "input=" + URLEncoder.encode(input, StandardCharsets.UTF_8.toString()) +
                        "&key=" + key +
                        "&strictbounds=true" +
                        "&location=" + lat.toString() +
                        "," + lon.toString() +
                        "&radius=" + radius.toString()
                , headers);
        try {
            return objectMapper.readValue(autoCompleteInfo.body(), GoogleAutoCompleteInfo.class);
        } catch (Exception e) {
            return null;
        }
    }

    private static List<String> convertStringList(JSONArray array) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            result.add(array.get(i).toString());
        }
        return result;
    }
}
