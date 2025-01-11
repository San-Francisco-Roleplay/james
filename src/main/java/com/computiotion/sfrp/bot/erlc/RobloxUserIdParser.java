package com.computiotion.sfrp.bot.erlc;

import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RobloxUserIdParser {
    public static @Nullable String getUserIdFromUrl(String url) throws URISyntaxException {
        // Parse the URL to validate it
        URI uri = new URI(url);

        // Regex to capture the user ID in the URL
        String regex = "/users/(\\d+)/profile";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(uri.getPath()); // Use the path part of the URI

        if (matcher.find()) {
            return matcher.group(1); // Group 1 contains the user ID
        }
        return null; // Return null if no match is found
    }
}