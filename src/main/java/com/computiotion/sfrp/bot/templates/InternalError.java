package com.computiotion.sfrp.bot.templates;

import com.computiotion.sfrp.bot.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

public class InternalError implements EmbedTemplate {
    private String errorId = null;
    private Boolean slash = null;

    public InternalError(String errorId, boolean slash) {
        this.errorId = errorId;
        this.slash = slash;
    }

    public InternalError(boolean slash) {
        this.slash = slash;
    }

    public InternalError(String errorId) {
        this.errorId = errorId;
    }

    public InternalError() {}

    public @NotNull EmbedBuilder makeEmbed() {
        StringBuilder builder = new StringBuilder("```");
        boolean includeDetails = errorId != null || slash != null;

        if (errorId != null) builder.append("\nERROR_ID=").append(errorId);
        if (slash != null) builder.append("\nCMD_TYPE=").append(slash ? "SLASH" : "MESSAGE");


        builder.append("\n```");
        if (includeDetails) {
            return new EmbedBuilder()
                    .setTitle("An Error Occurred")
                    .setDescription("Unfortunately, an internal error occurred. This issue has been logged and will be fixed as quickly as possible.\n" + builder)
                    .setColor(Colors.Red.getColor());
        }
        return new EmbedBuilder()
                .setTitle("An Error Occurred")
                .setDescription("Unfortunately, an internal error occurred. This issue has been logged and will be fixed as quickly as possible.")
                .setColor(Colors.Red.getColor());
    }
}
