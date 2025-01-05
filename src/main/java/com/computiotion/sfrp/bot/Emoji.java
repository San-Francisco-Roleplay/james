package com.computiotion.sfrp.bot;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum Emoji {
    Success("Success", "1312265798949011456"),
    Error("Error", "1312086150306992188"),
    Right("Right", "1312094399504842843"),
    Left("Left", "1312094398087041075"),
    MiniRight("MiniRight", "1315089516016566292"),
    Loading("Loading", "1324941983663394898", true),
    TimeAccepted("<:TimeAccepted:1315093012128469032>"),
    TimeAwaiting("<:TimeAwaiting:1315093013176778823>"),
    TimeDenied("<:TimeRejected:1315093010383638609>");

    private final String name;
    private final String id;
    private boolean animated = false;

    Emoji(String name, String id, boolean animated) {
        this.name = name;
        this.id = id;
        this.animated = animated;
    }

    Emoji(String name, String id) {
        this.name = name;
        this.id = id;
    }

    Emoji(@NotNull String export) {
        String content = export.substring(2, export.length() - 1); // Remove '<:' and '>'
        String[] split = content.split(":");

        name = split[0];
        id = split[1];
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    @Contract(pure = true)
    public @NotNull String getExport() {
        return String.format("<%s:%s:%s>", animated ? "a" : "", name, id);
    }

    @Contract(pure = true)
    public @NotNull String e() {
        return getExport();
    }

    public boolean isAnimated() {
        return animated;
    }

    public net.dv8tion.jda.api.entities.emoji.Emoji toJda() {
        return net.dv8tion.jda.api.entities.emoji.Emoji.fromFormatted(getExport());
    }
}