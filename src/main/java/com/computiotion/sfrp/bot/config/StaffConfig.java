package com.computiotion.sfrp.bot.config;

import com.computiotion.sfrp.bot.commands.PermissionLevel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.computiotion.sfrp.bot.BotApplication.getJda;

public class StaffConfig {
    private final String infractionChannel;
    private final String promotionsChannel;
    private final HashMap<StaffPermission, String> roles;

    public StaffConfig(String infractionChannel, String promotionsChannel, HashMap<StaffPermission, String> roles) {
        this.infractionChannel = infractionChannel;
        this.promotionsChannel = promotionsChannel;
        this.roles = roles;
    }

    public String getInfractionChannel() {
        return infractionChannel;
    }

    public String getPromotionsChannel() {
        return promotionsChannel;
    }

    public TextChannel resolveInfractionChannel() {
        return getJda().getTextChannelById(infractionChannel);
    }

    public TextChannel resolvePromotionsChannel() {
        return getJda().getTextChannelById(promotionsChannel);
    }

    @UnmodifiableView
    public Map<StaffPermission, String> getRoles() {
        return Collections.unmodifiableMap(roles);
    }
}
