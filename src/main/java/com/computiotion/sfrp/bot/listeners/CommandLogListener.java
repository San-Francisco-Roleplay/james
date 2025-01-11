package com.computiotion.sfrp.bot.listeners;

import com.computiotion.sfrp.bot.ConfigManager;
import com.computiotion.sfrp.bot.config.Config;
import com.computiotion.sfrp.bot.config.ConfigReader;
import com.computiotion.sfrp.bot.config.erlc.CommandLogType;
import com.computiotion.sfrp.bot.config.erlc.ERLCConfig;
import com.computiotion.sfrp.bot.erlc.CommandLog;
import com.computiotion.sfrp.bot.erlc.CommandLogUtils;
import com.computiotion.sfrp.bot.erm.BloxlinkUtils;
import com.computiotion.sfrp.bot.erm.ERMUtils;
import com.computiotion.sfrp.bot.erm.Shift;
import com.computiotion.sfrp.bot.templates.CommandFormat;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class CommandLogListener extends ListenerAdapter {
    private static final HashMap<CommandLogType, TextChannel> channels = new HashMap<>();
    private static final Log logger = LogFactory.getLog(CommandLogListener.class);

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Config config = null;
        try {
            config = ConfigReader.fromApplicationDefaults();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }

        ERLCConfig erlc = config.getErlc();
        String id = erlc.getLogSenderId();

        if (!event.getAuthor().getId().equals(id)) return;

        Message message = event.getMessage();
        List<MessageEmbed> embeds = message.getEmbeds();

        MessageEmbed embed = embeds.getFirst();
        if (embed == null) return;

        CommandLog log;
        try {
            log = CommandLogUtils.parseEmbed(embed);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String command = log.command();
        String userId = log.userId();

        if (Objects.equals(userId, "0")) return;

        logger.trace("command: " + command);
        String base = command.split(" ")[0].trim();
        logger.trace("base: " + base);

        Set<CommandLogType> types = new HashSet<>();
        if (Objects.equals(base, "mod") || Objects.equals(base, "admin") || Objects.equals(base, "unmod") || Objects.equals(base, "unadmin")) {
            logger.trace("Adding power cmd");
            types.add(CommandLogType.Power);
        }
        JDA jda = event.getJDA();

        String discordId = BloxlinkUtils.fromRoblox(ConfigManager.getErmGuild(), log.userId());
        logger.trace("Discord ID: " + discordId);
        if (discordId == null) return;

        boolean exempt = false;
        Guild guild = jda.getGuildById(ConfigManager.getErmGuild());
        if (guild == null) {
            logger.fatal("ERM Guild is null when resolved.");
            return;
        }

        logger.trace("Guild: " + guild.getName() + " Discord ID: " + discordId);
        List<Member> members = guild.getMembers();
        logger.trace(members.size() + " Members Found");

        Member member = members.stream().filter(user -> user.getId().equals(discordId)).findFirst().orElse(null);
        if (member == null) {
            logger.debug("Member is null.");
            return;
        }

        exempt = member.getRoles().stream().anyMatch(role -> erlc.getOffDutyExempt().contains(role.getId()));

        boolean offDutyPing = config.getErlc().getAlwaysAllowedCommands().contains(base) || exempt;

        List<@NotNull Shift> shifts = ERMUtils.getActiveShifts();
        List<String> userIds = shifts.stream().map(Shift::getUserId).toList();
        userIds.forEach(logger::trace);

        if (!userIds.contains(discordId)) {
            types.add(CommandLogType.OffDuty);
        }

        HashMap<CommandLogType, TextChannel> channels = new HashMap<>();
        for (CommandLogType type : types) {
            TextChannel channel = CommandLogListener.channels.get(type);
            if (channel == null) {
                channel = jda.getTextChannelById(erlc.getLogs().get(type));
                CommandLogListener.channels.put(type, channel);
            }
            if (channel == null)
                throw new NullPointerException("The " + type.getReadable() + " channel is null or is not a text channel.");

            channels.put(type, channel);
        }

        if (discordId == null) return;

        for (Map.Entry<CommandLogType, TextChannel> entry : channels.entrySet()) {
            CommandLogType type = entry.getKey();
            TextChannel channel = entry.getValue();

            MessageEmbed sendEmbed = new CommandFormat(discordId, log, type).makeEmbed().build();

            if (type == CommandLogType.OffDuty && offDutyPing) {
                channel.sendMessageEmbeds(sendEmbed)
                        .queue();
                continue;
            }

            channel.sendMessage("<@" + discordId + ">")
                    .setEmbeds(sendEmbed)
                    .queue();
        }
    }
}
