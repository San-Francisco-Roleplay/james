package com.computiotion.sfrp.bot.commands;

import com.computiotion.sfrp.bot.Colors;
import com.computiotion.sfrp.bot.config.CommandConfig;
import com.computiotion.sfrp.bot.config.Config;
import com.computiotion.sfrp.bot.config.ConfigReader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;

import static com.computiotion.sfrp.bot.config.ConfigReader.CONFIG_FILE_ENV_ENTRY;

@CommandController("dev")
public class Development extends Command {
    private static final Log log = LogFactory.getLog(Development.class);

    public void perm(@NotNull CommandInteraction interaction) {
        net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction slash = null;
        Message message = null;

        CommandInteractionType type = interaction.getType();

        if (interaction instanceof MessageCommandInteraction msg) {
            message = msg.getMessage();
        }

        if (interaction instanceof SlashCommandInteraction dev) {
            slash = dev.getInteraction();
        }

        if (type == CommandInteractionType.MESSAGE) {
            assert message != null;
            message.reply("Command received.")
                    .queue();
        } else {
            assert slash != null;
            slash.reply("Command received.")
                    .setEphemeral(true)
                    .queue();
        }
    }

    @CommandExecutor(value = "perm-enabled", level = PermissionLevel.Enabled)
    public void permEnabled(@NotNull CommandInteraction interaction) {
        perm(interaction);
    }

    @CommandExecutor(value = "perm-staff", level = PermissionLevel.Staff)
    public void permStaff(@NotNull CommandInteraction interaction) {
        perm(interaction);
    }

    @CommandExecutor(value = "perm-ia", level = PermissionLevel.InternalAffairs)
    public void permIA(@NotNull CommandInteraction interaction) {
        perm(interaction);
    }

    @CommandExecutor(value = "perm-dev", level = PermissionLevel.Developer)
    public void permDev(@NotNull CommandInteraction interaction) {
        perm(interaction);
    }

    @CommandExecutor(value = "perm-sm", level = PermissionLevel.SeniorManagement)
    public void permSM(@NotNull CommandInteraction interaction) {
        perm(interaction);
    }

    @CommandExecutor(value = "config", level = PermissionLevel.Developer)
    public void printConfig(@NotNull CommandInteraction interaction, @Boolean(value = "raw", required = false) java.lang.Boolean raw) throws ParserConfigurationException, IOException, SAXException {
        if (raw == null) raw = false;
        Config config = ConfigReader.fromApplicationDefaults();
        net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction slash = null;
        Message message = null;

        CommandInteractionType type = interaction.getType();

        CommandConfig commands = config.getCommands();

        if (interaction instanceof MessageCommandInteraction msg) {
            message = msg.getMessage();
        }

        if (interaction instanceof SlashCommandInteraction dev) {
            slash = dev.getInteraction();
        }

        String desc = new StringBuilder("```\n").append("Prefixes: [").append(commands.getPrefixes().stream()
                .map(prefix -> "\"" + prefix + "\"")
                .collect(Collectors.joining(", "))).append(commands.includesMention() ? (!commands.getPrefixes().isEmpty() ? ", mention" : "mention") : "").append("]\n")
                .append("\n> Check the raw file for more information.\n```")
                .toString();

        if (raw) {
            String fileLoc = System.getenv(CONFIG_FILE_ENV_ENTRY);

            File file = new File(fileLoc);
            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

            desc = "```xml\n" + content + "\n```";
        }

        MessageEmbed res = new EmbedBuilder()
                .setColor(Colors.Gold.getColor())
                .setTitle("Fetched Configuration")
                .setDescription(desc)
                .build();


        if (type == CommandInteractionType.MESSAGE) {
            assert message != null;
            message.replyEmbeds(res)
                    .queue();
        } else {
            assert slash != null;
            slash.replyEmbeds(res)
                    .setEphemeral(true)
                    .queue();
        }
    }

    @CommandExecutor(value = "cmd-required", level = PermissionLevel.Developer)
    public void required(@NotNull CommandInteraction interaction,
                         @Boolean("bool") java.lang.Boolean bool,
                         @Channel("channel") net.dv8tion.jda.api.entities.channel.Channel channel,
                         @Integer("int") java.lang.Integer integer,
                         @User("user") net.dv8tion.jda.api.entities.User user,
                         @Mentionable("mentionable") net.dv8tion.jda.api.entities.IMentionable mentionable,
                         @Number("number") Double number,
                         @Role("role") net.dv8tion.jda.api.entities.Role role,
                         @Text("text") java.lang.String text
    ) {
        net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction slash = null;
        Message message = null;

        CommandInteractionType type = interaction.getType();

        if (interaction instanceof MessageCommandInteraction msg) {
            message = msg.getMessage();
        }

        if (interaction instanceof SlashCommandInteraction dev) {
            slash = dev.getInteraction();
        }

        String desc = "```\nBoolean: " + bool +
                "\nChannel: " + channel.getId() +
                "\nInteger: " + integer +
                "\nUser: " + user.getId() +
                "\nMentionable: " + mentionable.getId() +
                "\nDouble: " + number +
                "\nRole: " + role.getId() +
                "\nText: " + text + "\n```";

        MessageEmbed res = new EmbedBuilder()
                .setColor(Colors.Blue.getColor())
                .setTitle("Result")
                .setDescription(desc)
                .build();


        if (type == CommandInteractionType.MESSAGE) {
            assert message != null;
            message.replyEmbeds(res)
                    .setContent(user.getAsMention())
                    .queue();
        } else {
            assert slash != null;
            slash.replyEmbeds(res)
                    .setContent(user.getAsMention())
                    .queue();
        }
    }

    @CommandExecutor(value = "cmd-optional", level = PermissionLevel.Developer)
    public void optional(@NotNull CommandInteraction interaction,
                         @Boolean(value = "bool", required = false) java.lang.Boolean bool,
                         @Channel(value = "channel", required = false) net.dv8tion.jda.api.entities.channel.Channel channel,
                         @Integer(value = "int", required = false) java.lang.Integer integer,
                         @User(value = "user", required = false) net.dv8tion.jda.api.entities.User user,
                         @Mentionable(value = "mentionable", required = false) net.dv8tion.jda.api.entities.IMentionable mentionable,
                         @Number(value = "number", required = false) Double number,
                         @Role(value = "role", required = false) net.dv8tion.jda.api.entities.Role role,
                         @Text(value = "text", required = false) java.lang.String text
    ) {
        net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction slash = null;
        Message message = null;

        CommandInteractionType type = interaction.getType();

        if (interaction instanceof MessageCommandInteraction msg) {
            message = msg.getMessage();
        }

        if (interaction instanceof SlashCommandInteraction dev) {
            slash = dev.getInteraction();
        }

        String desc = "```\nBoolean: " + bool +
                "\nChannel: " + ((channel == null) ? "null" : channel.getId()) +
                "\nInteger: " + integer +
                "\nMember: " + ((user == null) ? "null" : user.getId()) +
                "\nMentionable: " + ((mentionable == null) ? "null" : mentionable.getId()) +
                "\nDouble: " + number +
                "\nRole: " + ((role == null) ? "null" : role.getId()) +
                "\nText: " + text + "\n```";

        MessageEmbed res = new EmbedBuilder()
                .setColor(Colors.Blue.getColor())
                .setTitle("Result")
                .setDescription(desc)
                .build();


        if (type == CommandInteractionType.MESSAGE) {
            assert message != null;
            message.replyEmbeds(res)
                    .queue();
        } else {
            assert slash != null;
            slash.replyEmbeds(res)
                    .queue();
        }
    }
}
