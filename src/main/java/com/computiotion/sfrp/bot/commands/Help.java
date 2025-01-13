package com.computiotion.sfrp.bot.commands;

import com.computiotion.sfrp.bot.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

@CommandController(value = "help", description = "Displays a help message about James.")
public class Help extends Command {
    @CommandExecutor()
    public void get(@NotNull CommandInteraction interaction) {
        net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction slash = null;
        Message message = null;


        if (interaction instanceof MessageCommandInteraction msg) {
            message = msg.getMessage();
        }

        if (interaction instanceof SlashCommandInteraction dev) {
            slash = dev.getInteraction();
        }

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("About James")
                .setColor(Colors.DarkMode.getColor())
                .setDescription("James is SFRP's approach to staff moderation and many other utility features.")
                .addField("Bot Information", """
> **Website:** [View Website](https://sfrp.computiotion.com)
> **Support:** [Join Development Server](https://discord.gg/y2K8ZPZJx3)
> **SFRP:** [Join Main Server](https://discord.gg/nfUPYTwyF3)
> **Documentation**: [View Commands](https://docs.sfrp.computiotion.com)""", false)
                .build();

        if (interaction.getType() == CommandInteractionType.SLASH) {
            assert slash != null;
            slash.replyEmbeds(embed)
                    .complete();
        } else {
            assert message != null;
            message.replyEmbeds(embed)
                    .complete();
        }
    }
}
