package com.computiotion.sfrp.bot.commands;

import com.computiotion.sfrp.bot.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;

@CommandController("bam")
public class Bam extends Command {
    private static final Log log = LogFactory.getLog(Bam.class);

    @CommandExecutor(level = PermissionLevel.Staff)
    public void ban(@NotNull CommandInteraction interaction, @User("member") net.dv8tion.jda.api.entities.User user, @Text(value = "reason", required = false) String reason) {
        net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction slash = null;
        Message message = null;

        CommandInteractionType type = interaction.getType();

        if (interaction instanceof MessageCommandInteraction msg) {
            message = msg.getMessage();

        }

        if (interaction instanceof SlashCommandInteraction dev) {
            slash = dev.getInteraction();
        }

        MessageEmbed banned = new EmbedBuilder()
                .setColor(Colors.Red.getColor())
                .setDescription("Bammed with the following reason:\n```\n" + (reason == null ? "VDMers are not permitted within SFRP." : reason) + "\n```")
                .setFooter("Verified SFRP bam.")
                .setTitle("Bammed")
                .build();

        if (type == CommandInteractionType.MESSAGE) {
            assert message != null;
            message.replyEmbeds(banned)
                    .setContent(user.getAsMention())
                    .queue();
        } else {
            assert slash != null;
            slash.replyEmbeds(banned)
                    .setContent(user.getAsMention())
                    .queue();
        }
    }
}
