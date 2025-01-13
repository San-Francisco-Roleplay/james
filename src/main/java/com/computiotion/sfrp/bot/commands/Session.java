package com.computiotion.sfrp.bot.commands;

import com.computiotion.sfrp.bot.Colors;
import com.computiotion.sfrp.bot.DiscordTimeUtils;
import com.computiotion.sfrp.bot.Emoji;
import com.computiotion.sfrp.bot.config.Config;
import com.computiotion.sfrp.bot.config.ConfigReader;
import com.computiotion.sfrp.bot.config.SessionConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@CommandController(value = "session", description = "Sends messages related to sessions.")
public class Session extends Command {
    private static final Log log = LogFactory.getLog(Session.class);

    private void purgeChannel(@NotNull TextChannel channel) {
        String id = channel.getJDA().getSelfUser().getId();

        MessageHistory history = channel.getHistory();
        List<Message> messages = history.retrievePast(50).complete();

        messages.stream().filter(message -> message.getAuthor().getId().equals(id))
                .forEach(message -> message.delete().queue());
    }

    @CommandExecutor(value = "poll", level = PermissionLevel.HighRank, description = "Sends out a session poll message.", rate = RateLimitPreset.Session)
    public void poll(@NotNull CommandInteraction interaction) throws ParserConfigurationException, IOException, SAXException {
        Config config = ConfigReader.fromApplicationDefaults();
        SessionConfig sessions = config.getSessions();

        User user = interaction.getUser();

        net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction slash = null;
        Message message = null;


        if (interaction instanceof MessageCommandInteraction msg) {
            message = msg.getMessage();
        }

        if (interaction instanceof SlashCommandInteraction dev) {
            slash = dev.getInteraction();
        }

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Server Start Up Poll")
                .setColor(Colors.Blurple.getColor())
                .setDescription("Our staff team may be hosting an SSU! While there is no set amount of reacts required for a session, this will help our staff estimate how many players will join. If you would like to join the session, react with a checkmark.\n" +
                        "\n> " + Emoji.ContentBullet.e() + "**Host:** " + user.getAsMention())
                .build();

        MessageEmbed image = new EmbedBuilder()
                .setColor(Colors.Blurple.getColor())
                .setImage("https://cdn.computiotion.com/sfrp/banner/Sessions.png")
                .build();

        JDA jda = user.getJDA();
        TextChannel channel = jda.getTextChannelById(sessions.getChannel());

        if (channel == null) {
            log.error("The Sessions channel was resolved to be null: " + sessions.getChannel());
            MessageEmbed error = new EmbedBuilder()
                    .setColor(Colors.Red.getColor())
                    .setTitle("Configuration Error")
                    .setDescription("```\nThe sessions channel was resolved to be null.\n```")
                    .build();

            if (interaction.getType() == CommandInteractionType.SLASH) {
                assert slash != null;
                slash.replyEmbeds(error)
                        .setEphemeral(true)
                        .complete();
            } else {
                assert message != null;
                message.replyEmbeds(error)
                        .complete();
            }
            return;
        }

        purgeChannel(channel);

        Message resMessage = channel.sendMessageEmbeds(image, embed)
                .setContent(Emoji.ContentPing.e() + " > <@&" + sessions.getRolePing() + ">")
                .complete();

        MessageEmbed res = new EmbedBuilder()
                .setTitle("Sent Message.")
                .setColor(Colors.Blurple.getColor())
                .setDescription("[Jump to message](" + resMessage.getJumpUrl() + ")")
                .build();

        resMessage.addReaction(net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode("✅"))
                .complete();

        if (interaction.getType() == CommandInteractionType.SLASH) {
            assert slash != null;
            slash.replyEmbeds(res)
                    .setEphemeral(Objects.equals(slash.getChannelId(), channel.getId()))
                    .complete();
        } else {
            assert message != null;

            if (Objects.equals(message.getChannelId(), channel.getId())) {
                message.delete().complete();
                return;
            }

            message.replyEmbeds(res)
                    .complete();
        }
    }


    @CommandExecutor(value = "ssu", level = PermissionLevel.HighRank, description = "Sends an SSU message.", rate = RateLimitPreset.Session)
    public void ssu(@NotNull CommandInteraction interaction) throws ParserConfigurationException, IOException, SAXException {
        Config config = ConfigReader.fromApplicationDefaults();
        SessionConfig sessions = config.getSessions();

        User user = interaction.getUser();

        net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction slash = null;
        Message message = null;


        if (interaction instanceof MessageCommandInteraction msg) {
            message = msg.getMessage();
        }

        if (interaction instanceof SlashCommandInteraction dev) {
            slash = dev.getInteraction();
        }

        String builder = "Our staff team has decided to host an SSU! Please join for some great roleplays.\n" + "\n> " + Emoji.ContentBullet.e() + " **Server Name:** " + sessions.getServerName() +
                "\n> " + Emoji.ContentBullet.e() + "**Host:** " + user.getAsMention() +
                "\n> " + Emoji.ContentBullet.e() + "**Server Code:** `" + sessions.getCode() + "`" +
                "\n> " + Emoji.ContentBullet.e() + "**Server Launch Time:** " + DiscordTimeUtils.LongDateTime.formatTime(Instant.now()) +
                "\n> " + Emoji.ContentBullet.e() + "**Server Up-time:** " + DiscordTimeUtils.Relative.formatTime(Instant.now());

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Server Start Up!")
                .setColor(Colors.Blurple.getColor())
                .setDescription(builder)
                .build();

        MessageEmbed image = new EmbedBuilder()
                .setColor(Colors.Blurple.getColor())
                .setImage("https://cdn.computiotion.com/sfrp/banner/Sessions.png")
                .build();

        JDA jda = user.getJDA();
        TextChannel channel = jda.getTextChannelById(sessions.getChannel());

        if (channel == null) {
            log.error("The Sessions channel was resolved to be null: " + sessions.getChannel());
            MessageEmbed error = new EmbedBuilder()
                    .setColor(Colors.Red.getColor())
                    .setTitle("Configuration Error")
                    .setDescription("```\nThe sessions channel was resolved to be null.\n```")
                    .build();

            if (interaction.getType() == CommandInteractionType.SLASH) {
                assert slash != null;
                slash.replyEmbeds(error)
                        .setEphemeral(true)
                        .complete();
            } else {
                assert message != null;
                message.replyEmbeds(error)
                        .complete();
            }
            return;
        }

        purgeChannel(channel);

        Message resMessage = channel.sendMessageEmbeds(image, embed)
                .setContent(Emoji.ContentPing.e() + " > <@&" + sessions.getRolePing() + ">")
                .addActionRow(Button.link("https://policeroleplay.community/join/" + sessions.getCode(), "Quick Join"))
                .complete();

        MessageEmbed res = new EmbedBuilder()
                .setTitle("Sent Message.")
                .setColor(Colors.Blurple.getColor())
                .setDescription("[Jump to message](" + resMessage.getJumpUrl() + ")")
                .build();

        if (interaction.getType() == CommandInteractionType.SLASH) {
            assert slash != null;
            slash.replyEmbeds(res)
                    .setEphemeral(Objects.equals(slash.getChannelId(), channel.getId()))
                    .complete();
        } else {
            assert message != null;

            if (Objects.equals(message.getChannelId(), channel.getId())) {
                message.delete().complete();
                return;
            }

            message.replyEmbeds(res)
                    .complete();
        }
    }

    @CommandExecutor(value = "lp", level = PermissionLevel.HighRank, description = "Sends out a \"low on players\" message.", rate = RateLimitPreset.Session)
    public void lowPlayers(CommandInteraction interaction) throws ParserConfigurationException, IOException, SAXException {
        Config config = ConfigReader.fromApplicationDefaults();
        SessionConfig sessions = config.getSessions();

        User user = interaction.getUser();

        net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction slash = null;
        Message message = null;


        if (interaction instanceof MessageCommandInteraction msg) {
            message = msg.getMessage();
        }

        if (interaction instanceof SlashCommandInteraction dev) {
            slash = dev.getInteraction();
        }

        String builder = "The server is currently low on players! Please join the server with the code `" + sessions.getCode() + "`.";

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Session Boost")
                .setColor(Colors.Blurple.getColor())
                .setDescription(builder)
                .build();

        JDA jda = user.getJDA();
        TextChannel channel = jda.getTextChannelById(sessions.getChannel());

        if (channel == null) {
            log.error("The Sessions channel was resolved to be null: " + sessions.getChannel());
            MessageEmbed error = new EmbedBuilder()
                    .setColor(Colors.Red.getColor())
                    .setTitle("Configuration Error")
                    .setDescription("```\nThe sessions channel was resolved to be null.\n```")
                    .build();

            if (interaction.getType() == CommandInteractionType.SLASH) {
                assert slash != null;
                slash.replyEmbeds(error)
                        .setEphemeral(true)
                        .complete();
            } else {
                assert message != null;
                message.replyEmbeds(error)
                        .complete();
            }
            return;
        }

        Message resMessage = channel.sendMessageEmbeds(embed)
                .setContent(Emoji.ContentPing.e() + " > <@&" + sessions.getRolePing() + ">")
                .complete();

        MessageEmbed res = new EmbedBuilder()
                .setTitle("Sent Message.")
                .setColor(Colors.Blurple.getColor())
                .setDescription("[Jump to message](" + resMessage.getJumpUrl() + ")")
                .build();

        if (interaction.getType() == CommandInteractionType.SLASH) {
            assert slash != null;
            slash.replyEmbeds(res)
                    .setEphemeral(Objects.equals(slash.getChannelId(), channel.getId()))
                    .complete();
        } else {
            assert message != null;

            if (Objects.equals(message.getChannelId(), channel.getId())) {
                message.delete().complete();
                return;
            }

            message.replyEmbeds(res)
                    .complete();
        }
    }

    @CommandExecutor(value = "ssd", level = PermissionLevel.HighRank, description = "Sends out a \"no active sessions\" message.", rate = RateLimitPreset.Session)
    public void ssd(CommandInteraction interaction) throws ParserConfigurationException, IOException, SAXException {
        Config config = ConfigReader.fromApplicationDefaults();
        SessionConfig sessions = config.getSessions();

        User user = interaction.getUser();

        net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction slash = null;
        Message message = null;


        if (interaction instanceof MessageCommandInteraction msg) {
            message = msg.getMessage();
        }

        if (interaction instanceof SlashCommandInteraction dev) {
            slash = dev.getInteraction();
        }

        String builder = "Unfortunately, there's no active session. To be notified when there is one, however, go to [support](https://discord.com/channels/1321934421326168114/1324116737259733033) and get the `Session Ping` role, and you'll be pinged when a session goes up.";

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("No Active Session")
                .setColor(Colors.Blurple.getColor())
                .setDescription(builder)
                .build();

        MessageEmbed image = new EmbedBuilder()
                .setColor(Colors.Blurple.getColor())
                .setImage("https://cdn.computiotion.com/sfrp/banner/Sessions.png")
                .build();

        JDA jda = user.getJDA();
        TextChannel channel = jda.getTextChannelById(sessions.getChannel());
        if (channel == null) {
            log.error("The Sessions channel was resolved to be null: " + sessions.getChannel());
            MessageEmbed error = new EmbedBuilder()
                    .setColor(Colors.Red.getColor())
                    .setTitle("Configuration Error")
                    .setDescription("```\nThe sessions channel was resolved to be null.\n```")
                    .build();

            if (interaction.getType() == CommandInteractionType.SLASH) {
                assert slash != null;
                slash.replyEmbeds(error)
                        .setEphemeral(true)
                        .complete();
            } else {
                assert message != null;
                message.replyEmbeds(error)
                        .complete();
            }
            return;
        }

        purgeChannel(channel);

        Message resMessage = channel.sendMessageEmbeds(image, embed)
                .complete();

        MessageEmbed res = new EmbedBuilder()
                .setTitle("Sent Message.")
                .setColor(Colors.Blurple.getColor())
                .setDescription("[Jump to message](" + resMessage.getJumpUrl() + ")")
                .build();

        if (interaction.getType() == CommandInteractionType.SLASH) {
            assert slash != null;
            slash.replyEmbeds(res)
                    .setEphemeral(Objects.equals(slash.getChannelId(), channel.getId()))
                    .complete();
        } else {
            assert message != null;

            if (Objects.equals(message.getChannelId(), channel.getId())) {
                message.delete().complete();
                return;
            }

            message.replyEmbeds(res)
                    .complete();
        }
    }
}
