package org.example;

import java.time.ZoneId;
import java.util.TimeZone;

public class TimezoneValidator {
    public static String validateTimezone(String timezoneParam) {
        try {
            ZoneId zoneId = ZoneId.of(timezoneParam);
            TimeZone timeZone = TimeZone.getTimeZone(timezoneParam);
            if (!timeZone.getID().equals(timezoneParam)) {
                return null;
            }

            return zoneId.getId();
        } catch (Exception e) {
            return null;
        }
    }
}
