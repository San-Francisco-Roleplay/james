package com.computiotion.sfrp.bot.erlc;

import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandLogUtils {
    private static final Log log = LogFactory.getLog(CommandLogUtils.class);

    @Contract("_ -> new")
    public static @NotNull CommandLog parseEmbed(@NotNull MessageEmbed embed) throws URISyntaxException {
        log.trace("Parsing embed.");

        // Get raw description (contains user and command information)
        String rawDescription = embed.getDescription();
        if (rawDescription == null || rawDescription.isEmpty()) {
            log.warn("Embed description is empty.");
            throw new IllegalArgumentException("Embed description cannot be null or empty.");
        }

        // Get footer text (contains server information)
        MessageEmbed.Footer footer = embed.getFooter();
        String footerText = (footer != null) ? footer.getText() : "";
        if (footerText.isEmpty()) {
            log.warn("Embed footer is empty.");
            throw new IllegalArgumentException("Embed footer cannot be null or empty.");
        }

        // Parse user and command from the description
        String userInfo = "";
        String command = "";
        String[] parts = rawDescription.split("used the command:", 2);
        if (parts.length > 0) {
            userInfo = parts[0].trim(); // Extract user info
        }
        if (parts.length > 1) {
            command = parts[1].trim();
            if (command.startsWith("`") && command.endsWith("`")) {
                command = command.substring(1, command.length() - 1).trim().substring(1); // Remove backticks and colon
            }
        } else {
            log.warn("Failed to parse command from the description.");
            throw new IllegalArgumentException("Command not found in description.");
        }

        log.trace("Parsing user information.");
        String regex = "\\[(.+?)\\]\\((.+?)\\)"; // Matches [display text](URL)
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userInfo);

        String displayText = "";
        String url = "";
        if (matcher.find()) {
            displayText = matcher.group(1); // Capture group 1: Display text
            url = matcher.group(2);        // Capture group 2: URL
        } else {
            log.warn("Failed to parse user information: " + userInfo);
            throw new IllegalArgumentException("User information does not match expected format.");
        }

        log.trace("Display Text: " + displayText + ", URL: " + url);

        String userId = RobloxUserIdParser.getUserIdFromUrl(url);
        if (userId == null) {
            log.warn("Failed to parse user ID from URL: " + url);
            throw new IllegalArgumentException("Invalid Roblox profile URL.");
        }

        // Parse server name from footer
        String serverName = footerText.replace("Private Server: ", "").trim();

        return new CommandLog(userId, command, serverName);
    }
}