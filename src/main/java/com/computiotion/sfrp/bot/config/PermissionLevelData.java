package com.computiotion.sfrp.bot.config;

import com.computiotion.sfrp.bot.commands.PermissionLevel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class PermissionLevelData {
    private @NotNull final PermissionLevelDefault base;

    private final PermissionLevelParam roles;
    private final PermissionLevelParam channels;
    private final PermissionLevelParam user;
    private final PermissionLevelParam guilds;

    private final boolean silent;

    @Contract(pure = true)
    PermissionLevelData(@NotNull PermissionLevelDefault base, PermissionLevelParam roles, PermissionLevelParam channels, PermissionLevelParam user, PermissionLevelParam guilds, Boolean silent) {
        if (roles == null) roles = PermissionLevelParam.fromEmpty();
        if (channels == null) channels = PermissionLevelParam.fromEmpty();
        if (user == null) user = PermissionLevelParam.fromEmpty();
        if (guilds == null) guilds = PermissionLevelParam.fromEmpty();

        this.base = base;

        this.roles = roles;
        this.channels = channels;
        this.user = user;
        this.guilds = guilds;

        if (silent == null) silent = false;
        this.silent = silent;
    }

    public PermissionLevelParam getRoles() {
        return roles;
    }

    public PermissionLevelParam getChannels() {
        return channels;
    }

    public PermissionLevelParam getUser() {
        return user;
    }

    public PermissionLevelParam getGuilds() {
        return guilds;
    }

    public @NotNull PermissionLevelDefault getBase() {
        return base;
    }

    public boolean isSilent() {
        return silent;
    }
}
