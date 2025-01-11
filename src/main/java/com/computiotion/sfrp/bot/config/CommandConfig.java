package com.computiotion.sfrp.bot.config;

import com.computiotion.sfrp.bot.commands.PermissionLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class CommandConfig {
    @NotNull
    private final Set<String> prefixes;
    private final boolean mentions;
    @NotNull
    private final Map<PermissionLevel, PermissionLevelData> permissions;

    CommandConfig(@NotNull Set<String> prefixes, boolean mentions, @NotNull Map<PermissionLevel, PermissionLevelData> permissions) {
        this.prefixes = prefixes;
        this.mentions = mentions;
        this.permissions = permissions;
    }

    @UnmodifiableView
    public @NotNull Set<String> getPrefixes() {
        return Collections.unmodifiableSet(prefixes);
    }

    public boolean includesMention() {
        return mentions;
    }

    @UnmodifiableView
    public @NotNull Map<PermissionLevel, PermissionLevelData> getPermissions() {
        return Collections.unmodifiableMap(permissions);
    }
}
