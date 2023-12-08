package com.universal.core.library.utils;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class InstantHelper {

    public static Instant getTimeZoneInstance() {
        return Instant.now().plus(ZonedDateTime.now().getOffset().getTotalSeconds() / 60 / 60, ChronoUnit.HOURS);
    }
}
