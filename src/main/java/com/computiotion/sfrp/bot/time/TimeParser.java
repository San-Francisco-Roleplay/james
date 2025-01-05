package com.computiotion.sfrp.bot.time;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeParser {
    // Maps valid units to their corresponding millisecond multiplier
    private static final HashMap<String, Long> units = new HashMap<>();
    private static final Logger log = LogManager.getLogger(TimeParser.class);

    static {
        units.put("y", 31536000000L); // year (assuming 365 days)
        units.put("year", 31536000000L);
        units.put("years", 31536000000L);
        units.put("m", 2592000000L); // month (30 days)
        units.put("month", 2592000000L);
        units.put("months", 2592000000L);
        units.put("w", 604800000L); // week
        units.put("week", 604800000L);
        units.put("weeks", 604800000L);
        units.put("d", 86400000L); // day
        units.put("day", 86400000L);
        units.put("days", 86400000L);
        units.put("h", 3600000L); // hour
        units.put("hour", 3600000L);
        units.put("hours", 3600000L);
        units.put("min", 60000L); // minute
        units.put("mins", 60000L); // minute
        units.put("minute", 60000L);
        units.put("minutes", 60000L);
        units.put("s", 1000L); // second
        units.put("second", 1000L);
        units.put("seconds", 1000L);
        units.put("ms", 1L); // second
        units.put("millisecond", 1L);
        units.put("milliseconds", 1L);
    }

    /**
     * Parses a formatted string representing a time duration and converts it to the total number of milliseconds.
     * The string can contain numerical values followed by valid time units (for example, "1 day", "2 hours").
     * Valid time units include variations for years, months, weeks, days, hours, minutes, and seconds.
     * Throws a {@link ParseException} if the string contains invalid or unsupported time units or an improper format.
     *
     * @param input The input string that represents a time duration. Mustn't be null.
     * @return The total time duration in milliseconds as a long value.
     * @throws ParseException If the input format is invalid or contains unknown time units.
     * @throws IllegalArgumentException If the input string is null or improperly formatted.
     */
    public static long parseTime(@NotNull String input) {
        // Normalize input by removing "and", standardizing spaces
        String sanitizedInput = input.toLowerCase().replace("and", "").replaceAll("\\s+", " ").trim();

        // Pattern to match number and unit
        Pattern pattern = Pattern.compile("(\\d+)\\s*(\\w+)");
        Matcher matcher = pattern.matcher(sanitizedInput);

        long totalMilliseconds = 0;

        // Keep track of matched portions to validate remaining input
        StringBuilder matchedContent = new StringBuilder();

        while (matcher.find()) {
            long value;
            try {
                value = Long.parseLong(matcher.group(1));
            } catch (NumberFormatException e) {
                throw new ParseException("Numbers must be between 2^-64 and 2^64, got " + matcher.group(1), matcher.group(1));
            }
            // Number
            String unit = matcher.group(2); // Unit

            if (units.containsKey(unit)) {
                long unitValue = units.get(unit);
                totalMilliseconds += value * unitValue;

                matchedContent.append(matcher.group()).append(" ");
            } else {
                throw new ParseException("Unknown time unit: " + unit, unit);
            }
        }

        // Check if there is any unmatched content
        String unmatchedContent = sanitizedInput.replace(matchedContent.toString().trim(), "").trim();
        if (!unmatchedContent.isEmpty()) {
            throw new ParseException("Invalid input format: " + unmatchedContent, unmatchedContent);
        }

        return totalMilliseconds;
    }
}