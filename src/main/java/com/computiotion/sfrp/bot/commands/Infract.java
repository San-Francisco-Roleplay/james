package com.computiotion.sfrp.bot.commands;

import com.computiotion.sfrp.bot.Colors;
import com.computiotion.sfrp.bot.ConfigManager;
import com.computiotion.sfrp.bot.Emoji;
import com.computiotion.sfrp.bot.config.Config;
import com.computiotion.sfrp.bot.config.ConfigReader;
import com.computiotion.sfrp.bot.config.StaffConfig;
import com.computiotion.sfrp.bot.config.StaffPermission;
import com.computiotion.sfrp.bot.infractions.*;
import com.computiotion.sfrp.bot.reference.ReferenceData;
import com.computiotion.sfrp.bot.reference.ReferenceDataImpl;
import com.computiotion.sfrp.bot.reference.ReferenceHandler;
import com.computiotion.sfrp.bot.reference.ReferenceManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.InteractionHook;
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
    private final static StaffConfig staff = config.getStaff();

    @CommandExecutor(value = "status", level = PermissionLevel.Staff, description = "Checks your current infractions.")
    public void status(@NotNull CommandInteraction interaction) {

    }

    public Member verifyStaff(@NotNull CommandInteraction interaction, net.dv8tion.jda.api.entities.User user) {
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

            return null;
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

            return null;
        }

        return member;
    }

    @CommandExecutor(value = "history", level = PermissionLevel.HighRank, description = "Checks a staff member's infraction history.")
    public void history(@NotNull CommandInteraction interaction,
                        @User(value = "member", description = "The staff member to check", required = false) net.dv8tion.jda.api.entities.User user) {
        net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction slash = null;
        Message message = null;

        CommandInteractionType type = interaction.getType();

        if (interaction instanceof MessageCommandInteraction msg) {
            message = msg.getMessage();
        }

        if (interaction instanceof SlashCommandInteraction dev) {
            slash = dev.getInteraction();
        }

        Member member = verifyStaff(interaction, user);
        if (member == null) return;

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

    @ReferenceHandler("infract_queue")
    public void infractRef(ReferenceData data, String message) {
        log.trace("Reference triggered: " + message);
    }

    @CommandExecutor(value = "member", level = PermissionLevel.HighRank, description = "Infracts a staff member.")
    public void infract(@NotNull CommandInteraction interaction, @User(value = "member", description = "The staff member to infract.") net.dv8tion.jda.api.entities.User user,
                        @Text(value = "reason", description = "The reason for the infraction.") String reason) {
        net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction slash = null;
        Message message = null;

        CommandInteractionType type = interaction.getType();

        if (interaction instanceof MessageCommandInteraction msg) {
            message = msg.getMessage();
        }

        if (interaction instanceof SlashCommandInteraction dev) {
            slash = dev.getInteraction();
        }

        Member member = verifyStaff(interaction, user);
        if (member == null) return;

        if (interaction.getUser().getId().equals(member.getId())) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Colors.Red.getColor())
                    .setTitle("Invalid Argument")
                    .setDescription("The provided user may not be yourself.")
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

        QueuedInfraction infraction = QueuedInfraction.createInfraction(member.getGuild().getId(), interaction.getUser().getId());
        infraction.addTargets(user.getId());
        infraction.setReason(reason);

        EmbedBuilder queuedMessage = InfractionMessageUtils.createQueuedMessage(infraction);
        if (queuedMessage == null) return;
        Message res;

        if (interaction.getType() == CommandInteractionType.SLASH) {
            assert slash != null;
            InteractionHook hook = slash.replyEmbeds(queuedMessage.build())
                    .complete();
            res = hook.retrieveOriginal().complete();
        } else {
            assert message != null;
            res = message.replyEmbeds(queuedMessage.build())
                    .complete();
        }

        ReferenceManager.registerData(new ReferenceDataImpl("infract_queue", res.getId(), new InfractionReference(infraction.getId())));

        res.addReaction(Emoji.SignOff.toJda())
                .and(res.addReaction(Emoji.PlusOne.toJda()))
                .complete();
    }
}
