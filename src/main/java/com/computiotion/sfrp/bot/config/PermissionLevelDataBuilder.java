package com.computiotion.sfrp.bot.config;

import com.computiotion.sfrp.bot.commands.PermissionLevel;

public class PermissionLevelDataBuilder {
    private PermissionLevelDefault base;
    private PermissionLevelParam roles;
    private PermissionLevelParam channels;
    private PermissionLevelParam user;
    private PermissionLevelParam guilds;

    private boolean silent;

    public PermissionLevelDataBuilder setBase(PermissionLevelDefault base) {
        this.base = base;
        return this;
    }

    public PermissionLevelDataBuilder setRoles(PermissionLevelParam roles) {
        this.roles = roles;
        return this;
    }

    public PermissionLevelDataBuilder setChannels(PermissionLevelParam channels) {
        this.channels = channels;
        return this;
    }

    public PermissionLevelDataBuilder setUser(PermissionLevelParam user) {
        this.user = user;
        return this;
    }

    public PermissionLevelDataBuilder setGuilds(PermissionLevelParam guilds) {
        this.guilds = guilds;
        return this;
    }

    public PermissionLevelData createPermissionLevelData() {
        return build();
    }

    public PermissionLevelDataBuilder setSilent(boolean silent) {
        this.silent = silent;
        return this;
    }

    public PermissionLevelData build() {
        return new PermissionLevelData(base, roles, channels, user, guilds, silent);
    }
}