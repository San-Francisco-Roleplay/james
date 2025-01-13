package com.computiotion.sfrp.bot.commands;

import com.computiotion.sfrp.bot.Colors;
import com.computiotion.sfrp.bot.ConfigManager;
import com.computiotion.sfrp.bot.config.Config;
import com.computiotion.sfrp.bot.config.ConfigReader;
import com.computiotion.sfrp.bot.config.StaffConfig;
import com.computiotion.sfrp.bot.config.StaffPermission;
import com.computiotion.sfrp.bot.infractions.Infraction;
import com.computiotion.sfrp.bot.infractions.InfractionCollection;
import com.computiotion.sfrp.bot.infractions.InfractionHistory;
import com.computiotion.sfrp.bot.infractions.InfractionType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.Role;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@CommandController(value = "infract", description = "Utilities relating to infractions.")
public class Infract extends Command {
    private final static Config config;
    private static final Log log = LogFactory.getLog(Infract.class);

    static {
        try {
            config = ConfigReader.fromApplicationDefaults();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @CommandExecutor(value = "status", level = PermissionLevel.Staff, description = "Checks your current infractions.")
    public void status(@NotNull CommandInteraction interaction) {

    }

    @CommandExecutor(value = "history", level = PermissionLevel.HighRank, description = "Checks a staff member's infraction history.")
    public void history(@NotNull CommandInteraction interaction,
                        @User(value = "member", description = "The staff member to check", required = false) net.dv8tion.jda.api.entities.User user) {
        log.trace("Executing history.");
        StaffConfig staff = config.getStaff();

        net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction slash = null;
        Message message = null;

        CommandInteractionType type = interaction.getType();

        if (interaction instanceof MessageCommandInteraction msg) {
            message = msg.getMessage();
        }

        if (interaction instanceof SlashCommandInteraction dev) {
            slash = dev.getInteraction();
        }

        if (user == null) user = interaction.getUser();

        JDA jda = user.getJDA();

        Guild guild = jda.getGuildById(ConfigManager.getErmGuild());
        assert guild != null;

        Member member = guild.getMemberById(user.getId());
        if (member == null) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Colors.Red.getColor())
                    .setTitle("User must be in Guild")
                    .setDescription("The provided user is not within the server.\n> If you're a server administrator, make sure the ERM guild id corresponds with the guild people will be running the commands in.")
                    .build();

            if (interaction.getType() == CommandInteractionType.SLASH) {
                assert slash != null;
                slash.replyEmbeds(embed)
                        .setEphemeral(true)
                        .complete();
            } else {
                assert message != null;
                message.replyEmbeds(embed)
                        .complete();
            }

            return;
        }

        List<Role> roles = member.getRoles();
        if (!roles.stream().map(ISnowflake::getId).toList()
                .contains(staff.getRoles().get(StaffPermission.Staff))) {

            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Colors.Red.getColor())
                    .setTitle("User must be Staff")
                    .setDescription("The provided user does not have the <@&" + staff.getRoles().get(StaffPermission.Staff) +"> (staff) role.\n> If you're a server administrator, make sure the `config.Staff.roles.Staff` property is set.")
                    .build();

            if (interaction.getType() == CommandInteractionType.SLASH) {
                assert slash != null;
                slash.replyEmbeds(embed)
                        .setEphemeral(true)
                        .complete();
            } else {
                assert message != null;
                message.replyEmbeds(embed)
                        .complete();
            }

            return;
        }

        String memberId = member.getId();
        InfractionHistory history = InfractionHistory.fromUserId(memberId);
        Set<InfractionCollection> infractions = history.getInfractions();

        if (infractions.isEmpty()) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Colors.DarkMode.getColor())
                    .setTitle("Infraction History")
                    .setDescription("The provided user does not have any infraction history.")
                    .build();

            if (interaction.getType() == CommandInteractionType.SLASH) {
                assert slash != null;
                slash.replyEmbeds(embed)
                        .complete();
                return;
            } else {
                assert message != null;
                message.replyEmbeds(embed)
                        .complete();
                return;
            }
        }
    }

    @CommandExecutor(value = "warn", level = PermissionLevel.HighRank, description = "Warns a staff member.")
    public void warn(@NotNull CommandInteraction interaction) {
        InfractionType type = InfractionType.Warning;
    }

    @CommandExecutor(value = "strike", level = PermissionLevel.HighRank, description = "Strikes a staff member.")
    public void strike(@NotNull CommandInteraction interaction) {
        InfractionType type = InfractionType.Strike;

    }

    @CommandExecutor(value = "suspend", level = PermissionLevel.HighRank, description = "Suspends the staff member.")
    public void suspend(@NotNull CommandInteraction interaction) {
        InfractionType type = InfractionType.Suspend;

    }

    @CommandExecutor(value = "trial", level = PermissionLevel.HighRank, description = "Puts the staff member on trial.")
    public void trial(@NotNull CommandInteraction interaction) {
        InfractionType type = InfractionType.Trial;

    }

    @CommandExecutor(value = "leave", level = PermissionLevel.HighRank, description = "Puts the staff member on administrative leave.")
    public void leave(@NotNull CommandInteraction interaction) {
        InfractionType type = InfractionType.AdminLeave;

    }

    @CommandExecutor(value = "demotion", level = PermissionLevel.HighRank, description = "Demotes the staff member.")
    public void demotion(@NotNull CommandInteraction interaction) {
        InfractionType type = InfractionType.Demotion;
    }

    @CommandExecutor(value = "blacklist", level = PermissionLevel.HighRank, description = "Terminates a staff member and blacklists the staff.")
    public void blacklist(@NotNull CommandInteraction interaction) {
        InfractionType type = InfractionType.Blacklist;
    }

    @CommandExecutor(value = "term", level = PermissionLevel.HighRank, description = "Terminates a staff member.")
    public void terminate(@NotNull CommandInteraction interaction) {
        InfractionType type = InfractionType.Termination;
    }
}
