package com.computiotion.sfrp.bot.infractions;

import com.computiotion.sfrp.bot.BotApplication;
import com.computiotion.sfrp.bot.Colors;
import com.computiotion.sfrp.bot.time.TimeParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

import static org.springframework.format.annotation.DurationFormat.Style.SIMPLE;

public class InfractionMessageUtils {
    private static final Log log = LogFactory.getLog(InfractionMessageUtils.class);
    private static JDA jda = null;

    public static @Nullable EmbedBuilder createProofMessage(String url, String jumpUrl, QueuedInfraction infraction) {
        EmbedBuilder queuedMessage = createQueuedMessage(infraction);
        if (queuedMessage == null) return null;

        queuedMessage.setTitle("Attach Proof")
                .setColor(Colors.Green.getColor())
                .setDescription("Please [click here](" + url + ") to attach proof. [Jump to Message](" + jumpUrl + ")");

        return queuedMessage;
    }

    public static EmbedBuilder createQueuedMessage(QueuedInfraction infraction) {
        if (jda == null) jda = BotApplication.getJda();

        SortedSet<String> targetIds = infraction.getTargets();
        SortedSet<String> issuerIds = infraction.getSignedBy();
        SortedSet<String> signedByIds = infraction.getIssuers();
        Guild guild = jda.getGuildById(infraction.getGuildId());

        if (guild == null) {
            InfractionMessageUtils.log.fatal(new NullPointerException("An Infraction GuildID may not be null."));
            return null;
        }

        List<Member> issuers = issuerIds.stream().map(guild::getMemberById).toList();
        List<Member> signedBy = signedByIds.stream().map(guild::getMemberById).toList();
        List<Member> targets = targetIds.stream().map(guild::getMemberById).toList();


        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.DarkMode.getColor())
                .setTitle("Infraction Builder");

        String infractionInformation =
                "\n> **ID:** `" + infraction.getId() + "`" +
                "\n> **Reason:** " + (infraction.getReason() == null ? "*No reason was provided*" : infraction.getReason()) +
                "\n> **Punishments:** " + (infraction.getPunishments().isEmpty()
                        ? "*No punishments are present.* [Learn More](https://docs.sfrp.computiotion.com/ref/ia/infract)"
                        : infraction.getPunishments().stream().map(item -> {
                    InfractionType type = item.getType();

                    return switch (type) {
                        case Warning, Strike -> {
                            QuantitativeInfraction inf = (QuantitativeInfraction) item;
                            yield inf.getCount() + " " + type.getDisplay() + ((inf.getCount() != 1) ? "s" : "");
                        }
                        case Suspend -> {
                            TimeableInfraction inf = (TimeableInfraction) item;
                            yield TimeParser.formatTime(inf.getDuration()) + " " + type.getDisplay();
                        }
                        case Trial, Blacklist, AdminLeave, Demotion, Termination -> type.getDisplay();
                    };
                }).collect(Collectors.joining(", "))) +
                "\n> **Proof Status:** " + ((infraction.getProofIds().isEmpty() && infraction.getProofMessage() == null) ? "*No proof has been attached.*" : "Proof has been attached (`proof`).");

        String memberInformation =
                "\n> **Issuer(s):** " + issuers.stream().map(Member::getAsMention).collect(Collectors.joining()) +
                "\n> **Signed By:** " + signedBy.stream().map(Member::getAsMention).collect(Collectors.joining()) +
                "\n> **Target(s):** " + targets.stream().map(Member::getAsMention).collect(Collectors.joining());

        embed.addField("Information", infractionInformation.trim(), false);
        embed.addField("People", memberInformation.trim(), false);

        return embed;
    }
}
