package com.computiotion.sfrp.bot;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

public enum Error {
    InternalError(500, "An Internal Error Occurred", "This error has been reported and will be fixed as soon as possible."),
    InvalidBody(400, "Invalid Body Schema", "Refer to the documentation for the correct schema."),
    InvalidBodyLink(400, "Invalid Body Schema", "Refer to the documentation for the correct schema; see %s.", 1),
    InvalidBodyDescribe(400, "Invalid Body Schema", "%s", 1),
    InvalidQueryParam(400, "Query Parameter Missing", "Query parameter %s is null."),
    InvalidValue(400, "Invalid Value", "An invalid value was passed to %s."),
    RequestAlreadyPresent(409, "Request Already Present", "An identical request was already present."),
    ResourceNotFound(404, "Resource Not Found", "The requested resource was not found."),
    ResourceNotFoundSpecify(404, "Resource Not Found", "The requested resource (%s) was not found.", 6),
    NoRequest(404, "No Attempt Present", "There is no pre-existing attempt present for %s."),
    MissingAuth(401, "Missing Authentication", "Authentication is required to access this."),
    InvalidAuth(401, "Invalid Authentication", "The authentication provided is invalid and may not access this."),
    NoPerms(403, "Lacking Permissions", "You are lacking permission to access this resource.")
    ;

    private int code = ordinal();
    private final int status;
    private final String title;
    private final String description;

    Error(int status, String title, String description, int code) {
        this.code = code;
        this.status = status;
        this.title = title;
        this.description = description;
    }

    Error(int code, String title, String description) {
        this.status = code;
        this.title = title;
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getJson(String... args) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("timestamp", Instant.now().toString());
        jsonObject.addProperty("code", code);
        jsonObject.addProperty("error", title);
        jsonObject.addProperty("description", String.format(description, (Object[]) args));

        return Generators.getGson().toJson(jsonObject);
    }

    public @NotNull ResponseEntity<?> getResponse(String... args) {
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(getJson(args));
    }

    public int getCode() {
        return code;
    }
}