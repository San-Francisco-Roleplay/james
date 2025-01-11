package com.computiotion.sfrp.bot;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

/**
 * @see <a href="https://gist.github.com/LeviSnoot/d9147767abeef2f770e9ddcd91eb85aa">Github Gist</a>
 */
public enum DiscordTimeUtils {
    Default(""),
    ShortTime(":t"),
    LongTime(":T"),
    ShortDate(":d"),
    LongData(":D"),
    ShortDateTime(":f"),
    LongDateTime(":F"),
    Relative(":R");

    private final String append;

    DiscordTimeUtils(String append) {
        this.append = append;
    }

    /**
     * @param time The epoch time in seconds.
     * @return The Discord-formatted string.
     */
    @Contract(pure = true)
    public @NotNull String formatTime(long time) {
        return String.format("<t:%s%s>", time, append);
    }

    public @NotNull String formatTime(@NotNull Instant time) {
        return formatTime(time.getEpochSecond());
    }
}
