package com.computiotion.sfrp.bot.config;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public record PermissionLevelParam(Set<String> allow, Set<String> deny) {

    @Contract(pure = true)
    public @NotNull @UnmodifiableView Set<String> getAllow() {
        return Collections.unmodifiableSet(allow);
    }

    @Contract(pure = true)
    public @NotNull @UnmodifiableView Set<String> getDeny() {
        return Collections.unmodifiableSet(deny);
    }

    @Contract(" -> new")
    public static @NotNull PermissionLevelParam fromEmpty() {
        return new PermissionLevelParam(new HashSet<>(), new HashSet<>());
    }
}
