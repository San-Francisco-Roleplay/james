package com.computiotion.sfrp.bot.commands;

import com.computiotion.sfrp.bot.Colors;
import com.computiotion.sfrp.bot.erlc.Rest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@CommandController("admin")
public class Admin extends Command {
    private static Rest erlc = Rest.fromEnv();

    @CommandExecutor(value = "command", level = PermissionLevel.SeniorManagement)
    public void command(@NotNull CommandInteraction interaction, @Text(value = "command", description = "The command to execute.") String command) throws IOException {
        boolean res = erlc.command(command);
        net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction slash = null;
        Message message = null;

        CommandInteractionType type = interaction.getType();

        if (interaction instanceof MessageCommandInteraction msg) {
            message = msg.getMessage();

        }

        if (interaction instanceof SlashCommandInteraction dev) {
            slash = dev.getInteraction();
        }

        MessageEmbed embed = new EmbedBuilder()
                .setColor(Colors.Green.getColor())
                .setDescription("Successfully sent command \n```\n" + command + "\n```")
                .setTitle("Command Sent")
                .build();

        if (!res) {
            embed = new EmbedBuilder()
                    .setColor(Colors.Red.getColor())
                    .setDescription("Failed to send command \n```\n" + command + "\n```")
                    .setTitle("Command Failed to Send.")
                    .build();
        }

        if (type == CommandInteractionType.MESSAGE) {
            assert message != null;
            message.replyEmbeds(embed)
                    .queue();
        } else {
            assert slash != null;
            slash.replyEmbeds(embed)
                    .queue();
        }
    }
}
