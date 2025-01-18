package com.computiotion.sfrp.bot.infractions;

import com.computiotion.sfrp.bot.BotApplication;
import com.computiotion.sfrp.bot.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.format.datetime.standard.DurationFormatterUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.format.annotation.DurationFormat.Style.SIMPLE;

public class InfractionMessageUtils {
    private static final Log log = LogFactory.getLog(InfractionMessageUtils.class);
    private static JDA jda = null;

    public static EmbedBuilder createQueuedMessage(QueuedInfraction infraction) {
        if (jda == null) jda = BotApplication.getJda();

        List<String> targetIds = infraction.getTargets();
        List<String> issuerIds = infraction.getSignedBy();
        List<String> signedByIds = infraction.getIssuers();
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
                            yield inf.getCount() + " " + type.getDisplay();
                        }
                        case Suspend -> {
                            TimeableInfraction inf = (TimeableInfraction) item;
                            yield DurationFormatterUtils.print(inf.getDuration(), SIMPLE) + " " + type.getDisplay();
                        }
                        case Trial, Blacklist, AdminLeave, Demotion, Termination -> type.getDisplay();
                    };
                }));

        String memberInformation =
                "\n> **Issuer(s):** " + issuers.stream().map(Member::getAsMention).collect(Collectors.joining()) +
                "\n> **Signed By:** " + signedBy.stream().map(Member::getAsMention).collect(Collectors.joining()) +
                "\n> **Target(s):** " + targets.stream().map(Member::getAsMention).collect(Collectors.joining());

        embed.addField("Information", infractionInformation.trim(), false);
        embed.addField("People", memberInformation.trim(), false);

        return embed;
    }
}
