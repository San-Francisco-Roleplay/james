package com.computiotion.sfrp.bot.commands;

import com.computiotion.sfrp.bot.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;

@CommandController(value = "dev", internal = true)
public class Development extends Command {
    private static final Log log = LogFactory.getLog(Development.class);

    @CommandExecutor("cmd-required")
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

    @CommandExecutor("cmd-optional")
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
