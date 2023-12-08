package com.universal.core.library.random;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StringType {
    @JsonProperty("AN")
    ALPHANUMERIC("AN", "Alpha numberic"),
    @JsonProperty("A")
    ALPHABETIC("A", "Alphabetic"),
    @JsonProperty("N")
    NUMERIC("N", "Numeric")
    ;


    private final String type;
    private final String desc;

    StringType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static StringType getTypeByType(String type) {
        for (StringType ref : StringType.values()) {
            if (ref.type.equals(type)) {
                return ref;
            }
        }
        return null;
    }

    public String getType() {
        return this.type;
    }

    public String getDescription() {
        return this.desc;
    }
}
