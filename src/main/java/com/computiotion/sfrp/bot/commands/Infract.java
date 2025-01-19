package com.computiotion.sfrp.bot.commands;

import com.computiotion.sfrp.bot.Colors;
import com.computiotion.sfrp.bot.ConfigManager;
import com.computiotion.sfrp.bot.config.Config;
import com.computiotion.sfrp.bot.config.ConfigReader;
import com.computiotion.sfrp.bot.config.StaffConfig;
import com.computiotion.sfrp.bot.config.StaffPermission;
import com.computiotion.sfrp.bot.infractions.*;
import com.computiotion.sfrp.bot.reference.*;
import com.computiotion.sfrp.bot.time.TimeParser;
import com.google.common.collect.HashBiMap;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.Integer;
import java.time.Duration;
import java.util.*;

@CommandController(value = "infract", description = "Utilities relating to infractions.")
public class Infract extends Command {
    private final static Config config;
    private final static StaffConfig staff;
    private static final Log log = LogFactory.getLog(Infract.class);

    static {
        try {
            config = ConfigReader.fromApplicationDefaults();
            staff = config.getStaff();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }


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
                    .setDescription("The provided user does not have the <@&" + staff.getRoles().get(StaffPermission.Staff) + "> (staff) role.\n> If you're a server administrator, make sure the `config.Staff.roles.Staff` property is set.")
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
    public void infractRef(ReferenceData data, Message message, Message repliedTo) {
        String content = message.getContentRaw();
        ReferencePayload payload = data.getPayload();
        if (!(payload instanceof InfractionReference ref)) return;

        String queueId = ref.getQueueId();
        QueuedInfraction infraction = QueuedInfraction.getCollection(queueId);
        if (infraction == null) {
            repliedTo.delete().queue();
            return;
        }

        net.dv8tion.jda.api.entities.User author = message.getAuthor();
        if (!author.getId().equals(infraction.getLeader())) {
            author.openPrivateChannel()
                    .flatMap(channel -> channel.sendMessageEmbeds(new EmbedBuilder()
                            .setColor(Colors.Red.getColor())
                            .setTitle("Not Permitted")
                            .setDescription("You are not permitted to preform this action.")
                            .build()))
                    .queue();
            return;
        }


        if (Objects.equals(content, "delete this infraction")) {
            infraction.delete();
            ReferenceManager.removeData(data);

            repliedTo.editMessageEmbeds(new EmbedBuilder()
                            .setColor(Colors.Red.getColor())
                            .setTitle("Infraction Deleted")
                            .setDescription("This infraction was successfully deleted.")
                            .addField("Infraction Details", "> ID: `" + infraction.getId() + "`", false)
                            .build())
                    .setReplace(true)
                    .queue();
            return;
        }

        if (content.startsWith("reason")) {
            log.trace("Setting reason " + content);

            String[] split = content.split(" ", 2);
            if (split.length != 2 || split[1].trim().isEmpty()) {
                EmbedBuilder queuedMessage = InfractionMessageUtils.createQueuedMessage(infraction);
                if (queuedMessage == null) return;

                repliedTo.editMessageEmbeds(queuedMessage.appendDescription("**`CMD:`** A reason must be provided. For more information, review the [documentation](https://docs.sfrp.computiotion.com/ref/ia/infract).")
                        .build()).queue();
                return;
            }

            infraction.setReason(split[1]);
            EmbedBuilder queuedMessage = InfractionMessageUtils.createQueuedMessage(infraction);
            if (queuedMessage == null) return;
            repliedTo.editMessageEmbeds(queuedMessage.build())
                    .queue();
            return;
        }

        if (content.startsWith("add") || content.startsWith("sub")) {
            String[] split = content.split(" ");

            if (split.length == 1) {
                EmbedBuilder queuedMessage = InfractionMessageUtils.createQueuedMessage(infraction);
                if (queuedMessage == null) return;

                repliedTo.editMessageEmbeds(queuedMessage.appendDescription("**`CMD:`** A user must be provided. For more information, review the [documentation](https://docs.sfrp.computiotion.com/ref/ia/infract).")
                        .build()).queue();
                return;
            }

            List<Member> members = new ArrayList<>();

            List<String> args = new ArrayList<>(Arrays.asList(split));
            args.removeFirst();

            for (String arg : args) {
                Member member = message.getGuild().getMembers()
                        .stream().filter(user -> {
                            String mention = user.getAsMention();
                            String id = user.getId();
                            String name = user.getUser().getName();

                            return mention.equalsIgnoreCase(arg) || id.equalsIgnoreCase(arg) || name.equalsIgnoreCase(arg);
                        })
                        .findFirst()
                        .orElse(null);

                EmbedBuilder queuedMessage = InfractionMessageUtils.createQueuedMessage(infraction);
                if (queuedMessage == null) return;

                if (member == null) {
                    repliedTo.editMessageEmbeds(queuedMessage.appendDescription("**`CMD:`** No user was found for argument " + arg)
                                    .build())
                            .queue();
                    return;
                }

                HashBiMap<StaffPermission, String> perms = HashBiMap.create(staff.getRoles());

                List<StaffPermission> userRoles = Objects.requireNonNull(message.getMember()).getRoles()
                        .stream().filter(role -> staff.getRoles().containsValue(role.getId()))
                        .map(role -> perms.inverse().get(role.getId()))
                        .sorted(Comparator.comparingInt(Enum::ordinal))
                        .toList();

                List<StaffPermission> targetRoles = Objects.requireNonNull(member).getRoles()
                        .stream().filter(role -> staff.getRoles().containsValue(role.getId()))
                        .map(role -> perms.inverse().get(role.getId()))
                        .sorted(Comparator.comparingInt(Enum::ordinal))
                        .toList();

                if (!targetRoles.contains(StaffPermission.Staff)) {
                    repliedTo.editMessageEmbeds(queuedMessage.appendDescription("**`CMD:`** User " + member.getAsMention() + " is not staff.")
                                    .build())
                            .queue();
                    return;
                }


                if (targetRoles.getLast().ordinal() >= userRoles.getLast().ordinal()) {
                    repliedTo.editMessageEmbeds(queuedMessage.appendDescription("**`CMD:`** User " + member.getAsMention() + " is a higher rank than you (or the same rank as you).")
                                    .build())
                            .queue();
                    return;
                }

                members.add(member);
            }

            boolean adding = Objects.equals(split[0], "add");
            if (adding) {
                infraction.addTargets(members.stream().map(ISnowflake::getId).toArray(String[]::new));
            } else {
                infraction.removeTargets(members.stream().map(ISnowflake::getId).toArray(String[]::new));
            }

            EmbedBuilder queuedMessage = InfractionMessageUtils.createQueuedMessage(infraction);
            if (queuedMessage == null) return;

            repliedTo.editMessageEmbeds(queuedMessage.build())
                    .queue();

            return;
        }

        if (content.startsWith("+") || content.startsWith("-")) {
            boolean adding = content.startsWith("+");

            String args = content.substring(1);
            String[] split = args.split(" ", 2);

            if (split.length == 0) {
                EmbedBuilder queuedMessage = InfractionMessageUtils.createQueuedMessage(infraction);
                if (queuedMessage == null) return;

                repliedTo.editMessageEmbeds(queuedMessage.appendDescription("**`CMD:`** An infraction type must be provided. For more information, review the [documentation](https://docs.sfrp.computiotion.com/ref/ia/infract).")
                        .build()).queue();
                return;
            }

            InfractionType type = Arrays.stream(InfractionType.values())
                    .filter(item -> Objects.equals(item.getCommand(), split[0]))
                    .findFirst()
                    .orElse(null);

            if (type == null) {
                EmbedBuilder queuedMessage = InfractionMessageUtils.createQueuedMessage(infraction);
                if (queuedMessage == null) return;

                repliedTo.editMessageEmbeds(queuedMessage.appendDescription("**`CMD:`** Invalid type `" + split[0] + "`. For more information, review the [documentation](https://docs.sfrp.computiotion.com/ref/ia/infract).")
                        .build()).queue();
                return;
            }

            Infraction toAdd;

            switch (type) {
                case Warning, Strike -> {
                    int count = 1;
                    if (split.length > 2) {
                        EmbedBuilder queuedMessage = InfractionMessageUtils.createQueuedMessage(infraction);
                        if (queuedMessage == null) return;

                        repliedTo.editMessageEmbeds(queuedMessage.appendDescription("**`CMD:`** Improper usage. For more information, review the [documentation](https://docs.sfrp.computiotion.com/ref/ia/infract).")
                                .build()).queue();
                        return;
                    }

                    if (split.length == 2) {
                        String countStr = split[1];
                        try {
                            count = Integer.parseInt(countStr);
                        } catch (NumberFormatException e) {
                            EmbedBuilder queuedMessage = InfractionMessageUtils.createQueuedMessage(infraction);
                            if (queuedMessage == null) return;

                            repliedTo.editMessageEmbeds(queuedMessage.appendDescription("**`CMD:`** Count must be an integer.")
                                    .build()).queue();
                            return;
                        }

                        if (count < 1 || count > 3) {
                            EmbedBuilder queuedMessage = InfractionMessageUtils.createQueuedMessage(infraction);
                            if (queuedMessage == null) return;

                            repliedTo.editMessageEmbeds(queuedMessage.appendDescription("**`CMD:`** Count must be between 1 and 3.")
                                    .build()).queue();
                            return;
                        }
                    }

                    toAdd = new QuantitativeInfractionImpl(type, count);
                    break;
                }
                case Suspend -> {
                    long duration;

                    if (split.length != 2) {
                        EmbedBuilder queuedMessage = InfractionMessageUtils.createQueuedMessage(infraction);
                        if (queuedMessage == null) return;

                        repliedTo.editMessageEmbeds(queuedMessage.appendDescription("**`CMD:`** Improper usage. For more information, review the [documentation](https://docs.sfrp.computiotion.com/ref/ia/infract).")
                                .build()).queue();
                        return;
                    }

                    try {
                        duration = TimeParser.parseTime(split[1]);
                    } catch (Exception e) {
                        EmbedBuilder queuedMessage = InfractionMessageUtils.createQueuedMessage(infraction);
                        if (queuedMessage == null) return;

                        repliedTo.editMessageEmbeds(queuedMessage.appendDescription("**`CMD:`** Invalid Duration. For more information, review the [documentation](https://docs.sfrp.computiotion.com/ref/ia/infract).")
                                .build()).queue();
                        return;
                    }


                    toAdd = new TimeableInfractionImpl(type, Duration.ofMillis(duration));
                    break;
                }
                default -> {
                    toAdd = new InfractionImpl(type);
                }
            }

            try {
                if (adding) {
                    infraction.addInfraction(toAdd);
                } else {
                    infraction.removeInfraction(toAdd);
                }
            } catch (InfractionEditException e) {
                EmbedBuilder queuedMessage = InfractionMessageUtils.createQueuedMessage(infraction);
                if (queuedMessage == null) return;

                repliedTo.editMessageEmbeds(queuedMessage.appendDescription("**`CMD:`** " + e.getMessage())
                        .build()).queue();
                return;
            }
            EmbedBuilder queuedMessage = InfractionMessageUtils.createQueuedMessage(infraction);
            if (queuedMessage == null) return;

            repliedTo.editMessageEmbeds(queuedMessage.build()).queue();

            return;
        }

        EmbedBuilder queuedMessage = InfractionMessageUtils.createQueuedMessage(infraction);
        if (queuedMessage == null) return;

        repliedTo.editMessageEmbeds(queuedMessage.appendDescription("**`CMD:`** Unknown command `" + content + "` For more information, review the [documentation](https://docs.sfrp.computiotion.com/ref/ia/infract).")
                .build()).queue();
    }

    @CommandExecutor(value = "member", level = PermissionLevel.HighRank, description = "Infracts a staff member.", rate = RateLimitPreset.Session)
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

        HashBiMap<StaffPermission, String> perms = HashBiMap.create(staff.getRoles());

        assert interaction.getGuild() != null;
        List<StaffPermission> userRoles = Objects.requireNonNull(interaction.getGuild().getMember(UserSnowflake.fromId(interaction.getUser().getId()))).getRoles()
                .stream().filter(role -> staff.getRoles().containsValue(role.getId()))
                .map(role -> perms.inverse().get(role.getId()))
                .sorted(Comparator.comparingInt(Enum::ordinal))
                .toList();

        List<StaffPermission> targetRoles = Objects.requireNonNull(member).getRoles()
                .stream().filter(role -> staff.getRoles().containsValue(role.getId()))
                .map(role -> perms.inverse().get(role.getId()))
                .sorted(Comparator.comparingInt(Enum::ordinal))
                .toList();

        if (!targetRoles.contains(StaffPermission.Staff)) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Colors.Red.getColor())
                    .setTitle("User must be Staff")
                    .setDescription("The provided user a staff member.")
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
            return;
        }


        if (targetRoles.getLast().ordinal() >= userRoles.getLast().ordinal()) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Colors.Red.getColor())
                    .setTitle("User may not be Infracted")
                    .setDescription("The provided user is a higher or the same rank as you, and as such may not be infracted by you.")
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
            return;
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
    }
}
