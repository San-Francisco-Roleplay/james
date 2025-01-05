package com.computiotion.sfrp.bot.routes.api.v1;

import com.computiotion.sfrp.bot.Error;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.computiotion.sfrp.bot.Hooks.useAuth;
import static com.computiotion.sfrp.bot.Hooks.useBody;

@RestController
public class Config {
    @PostMapping(value = "/api/v1/config/prefixes", consumes = "application/json", produces = "application/json")
    public static ResponseEntity<?> addPrefix(@RequestBody String rawBody, @RequestHeader(value = "Authorization", required = false) String token) {
        JsonObject body = useBody(rawBody);
        boolean authed = useAuth(token);

        if (!authed) return Error.NoPerms.getResponse();
        if (body == null) return Error.InvalidBody.getResponse();

        JsonElement prefix = body.get("prefix");
        if (prefix == null || !prefix.isJsonPrimitive() || !prefix.getAsJsonPrimitive().isString()) return Error.InvalidBodyDescribe.getResponse("prefix was either not found or is not a string.");

        var config = com.computiotion.sfrp.bot.models.Config.getInstance();
        config.addPrefix(prefix.getAsString());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/api/v1/config/prefixes", consumes = "application/json", produces = "application/json")
    public static ResponseEntity<?> deletePrefix(@RequestBody String rawBody, @RequestHeader(value = "Authorization", required = false) String token) {
        JsonObject body = useBody(rawBody);
        boolean authed = useAuth(token);

        if (!authed) return Error.NoPerms.getResponse();
        if (body == null) return Error.InvalidBody.getResponse();

        JsonElement prefix = body.get("prefix");
        if (prefix == null || !prefix.isJsonPrimitive() || !prefix.getAsJsonPrimitive().isString()) return Error.InvalidBodyDescribe.getResponse("prefix was either not found or is not a string.");

        var config = com.computiotion.sfrp.bot.models.Config.getInstance();
        config.removePrefix(prefix.getAsString());

        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/api/v1/config/prefixes", consumes = "application/json", produces = "application/json")
    public static ResponseEntity<?> replacePrefixes(@RequestBody String rawBody, @RequestHeader(value = "Authorization", required = false) String token) {
        JsonObject body = useBody(rawBody);
        boolean authed = useAuth(token);

        if (!authed) return Error.NoPerms.getResponse();
        if (body == null) return Error.InvalidBody.getResponse();

        JsonElement prefix = body.get("prefixes");
        if (prefix == null || !prefix.isJsonArray()) return Error.InvalidBodyDescribe.getResponse("prefixes was either not found or is not a string array.");

        var config = com.computiotion.sfrp.bot.models.Config.getInstance();
        JsonArray jsonArray = prefix.getAsJsonArray();
//        jsonArray.asList().stream().map(obj).forEach(config::addPrefix);
//        config.setPrefixes();

        return ResponseEntity.noContent().build();
    }
}
