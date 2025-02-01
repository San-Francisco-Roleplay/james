package com.computiotion.sfrp.bot.infractions;

import com.computiotion.sfrp.bot.*;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;
import software.amazon.awssdk.services.s3.S3Client;

import java.security.InvalidKeyException;
import java.time.Duration;
import java.util.*;

import static com.computiotion.sfrp.bot.ConfigManager.REDIS_PREFIX_INFRACTION_QUEUE;

public class QueuedInfraction implements DatabaseSaveable {
    private static final Snowflake snowflake = new Snowflake();
    private static final Jedis jedis = Generators.getJedis();
    private static final Gson gson = Generators.getGson();
    private final SortedSet<String> targets = new TreeSet<>();
    private final Set<String> proofs = new HashSet<>();
    private String leader;
    private SortedSet<String> issuers = new TreeSet<>();
    private SortedSet<String> signedBy = new TreeSet<>();
    private transient String id;
    private String token;
    @SerializedName("guild_id")
    private String guildId;
    private List<Infraction> infractions;
    @SerializedName("proof_message")
    private String proofMessage;
    @SerializedName("builder_message_id")
    private String builderMessageId;
    private String builderChannelId;
    private String reason;

    private QueuedInfraction(String id) {
        this.id = id;
    }

    public static @NotNull QueuedInfraction createInfraction(@NotNull String builderChannelId, @NotNull String builderId, String guildId, String issuer) {
        return createInfraction(builderChannelId, builderId, guildId, List.of(issuer), List.of());
    }

    public static @NotNull QueuedInfraction createInfraction(String builderChannelId, String builderId, String guildId, String issuer, List<Infraction> infractions) {
        return createInfraction(builderChannelId, builderId, guildId, List.of(issuer), infractions);
    }

    public static @NotNull QueuedInfraction createInfraction(String builderChannelId, String builderId, String guildId, String issuer, Infraction infraction) {
        return createInfraction(builderChannelId, builderId, guildId, List.of(issuer), List.of(infraction));
    }

    public static @NotNull QueuedInfraction createInfraction(String builderChannelId, String builderId, String guildId, List<String> issuer) {
        return createInfraction(builderChannelId, builderId, guildId, issuer, List.of());
    }

    public static @NotNull QueuedInfraction createInfraction(String builderChannelId, String builderId, String guildId, @NotNull List<String> issuer, @NotNull List<Infraction> infractions) {
        QueuedInfraction infraction = new QueuedInfraction(snowflake.nextId());
        infraction.issuers = new TreeSet<>(issuer.stream().filter(Objects::nonNull).toList());
        infraction.signedBy = new TreeSet<>(infraction.issuers);
        infraction.infractions = infractions.stream().filter(Objects::nonNull).toList();
        infraction.guildId = guildId;
        infraction.leader = infraction.issuers.getFirst();
        infraction.builderChannelId = builderChannelId;
        infraction.builderMessageId = builderId;
        try {
            infraction.token = HmacUtils.generateHmac(infraction.id, ConfigManager.getEncryptionToken());
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        infraction.save();

        return infraction;
    }

    public static @Nullable QueuedInfraction getCollection(@NotNull String id) {
        if (id.startsWith(REDIS_PREFIX_INFRACTION_QUEUE)) id = id.substring(REDIS_PREFIX_INFRACTION_QUEUE.length());
        String dataStr = jedis.get(REDIS_PREFIX_INFRACTION_QUEUE + id);
        if (dataStr == null) return null;

        QueuedInfraction infraction = gson.fromJson(dataStr, QueuedInfraction.class);
        infraction.id = id;

        return infraction;
    }

    public String getId() {
        return id;
    }

    public void addTargets(String... targets) {
        this.targets.addAll(Arrays.stream(targets).toList());

        if (targets.length > 0) save();
    }

    public void removeTargets(String... targets) {
        Arrays.stream(targets).toList().forEach(this.targets::remove);

        if (targets.length > 0) save();
    }

    public SortedSet<String> getTargets() {
        return Collections.unmodifiableSortedSet(targets);
    }

    public void addIssuers(String... issuers) {
        this.issuers.addAll(Arrays.stream(issuers).toList());

        if (issuers.length > 0) save();
    }

    public SortedSet<String> getIssuers() {
        return Collections.unmodifiableSortedSet(issuers);
    }

    public void addSignage(String... issuers) {
        this.signedBy.addAll(Arrays.stream(issuers).toList());
        this.issuers.addAll(Arrays.stream(issuers).toList());

        if (issuers.length > 0) save();
    }

    public SortedSet<String> getSignedBy() {
        return Collections.unmodifiableSortedSet(signedBy);
    }

    @Override
    public void save() {
        jedis.set(REDIS_PREFIX_INFRACTION_QUEUE + id, gson.toJson(this), new SetParams()
                .ex(Duration.ofHours(12).toSeconds()));
    }

    public void delete() {
        jedis.del(REDIS_PREFIX_INFRACTION_QUEUE + id);
        S3Client s3 = Generators.getS3();

        proofs.forEach(id -> {
            String key = "IP-" + this.id + "-" + id;
            s3.deleteObject(builder -> builder.bucket(ConfigManager.getR2Bucket()).key(key).build());
        });
    }

    public String getGuildId() {
        return guildId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
        save();
    }

    public List<Infraction> getPunishments() {
        return infractions;
    }

    public void addInfraction(Infraction... punishments) {
        HashMap<InfractionType, Infraction> types = new HashMap<>();

        for (Infraction infraction : infractions) {
            types.put(infraction.getType(), infraction);
        }

        for (Infraction punishment : punishments) {
            InfractionType type = punishment.getType();
            Infraction preexisting = types.get(type);
            if (preexisting == null) {
                infractions.add(punishment);
                continue;
            }

            switch (preexisting.getType()) {
                case Warning, Strike -> {
                    QuantitativeInfraction pre = (QuantitativeInfraction) preexisting;
                    QuantitativeInfraction punish = (QuantitativeInfraction) punishment;

                    int count = pre.getCount();
                    int index = infractions.indexOf(preexisting);

                    if (count + punish.getCount() > 3)
                        throw new InfractionEditException("There may only be a maximum of 3 " + type.getDisplay().toLowerCase() + "s applied.");

                    infractions.set(index, new QuantitativeInfractionImpl(type, count + punish.getCount()));
                }
                case Suspend -> {
                    TimeableInfraction pre = (TimeableInfraction) preexisting;
                    TimeableInfraction punish = (TimeableInfraction) punishment;

                    int index = infractions.indexOf(preexisting);

                    infractions.set(index, new TimeableInfractionImpl(type, pre.getDuration().plus(punish.getDuration())));
                }
                case null, default -> {
                    throw new InfractionEditException("A punishment of this type already exists.");
                }
            }
        }

        if (punishments.length > 0) save();
    }

    public void removeInfraction(Infraction... punishments) {
        HashMap<InfractionType, Infraction> types = new HashMap<>();

        for (Infraction infraction : infractions) {
            types.put(infraction.getType(), infraction);
        }

        for (Infraction punishment : punishments) {
            InfractionType type = punishment.getType();
            Infraction preexisting = types.get(type);
            if (preexisting == null) {
                throw new InfractionEditException("A punishment of this type doesn't exist.");
            }

            switch (preexisting.getType()) {
                case Warning, Strike -> {
                    QuantitativeInfraction pre = (QuantitativeInfraction) preexisting;
                    QuantitativeInfraction punish = (QuantitativeInfraction) punishment;

                    int count = pre.getCount();
                    int index = infractions.indexOf(preexisting);

                    if ((count - punish.getCount()) < 1) {
                        infractions.remove(index);
                        break;
                    }

                    infractions.set(index, new QuantitativeInfractionImpl(type, count - punish.getCount()));
                }
                case Suspend -> {
                    TimeableInfraction pre = (TimeableInfraction) preexisting;
                    TimeableInfraction punish = (TimeableInfraction) punishment;

                    int index = infractions.indexOf(preexisting);
                    Duration duration = pre.getDuration().plus(punish.getDuration());

                    if (duration.toMillisPart() < 1) {
                        infractions.remove(index);
                        break;
                    }

                    infractions.set(index, new TimeableInfractionImpl(type, duration));
                }
                case null, default -> {
                    infractions.remove(preexisting);
                }
            }
        }

        if (punishments.length > 0) save();
    }

    public String getLeader() {
        return leader;
    }

    public String getToken() {
        return token;
    }

    /**
     * @return The original token.
     */
    public String regenToken() {
        String original = token;
        try {
            token = HmacUtils.generateHmac(id, ConfigManager.getEncryptionToken());
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        save();
        return original;
    }


    public Set<String> getProofIds() {
        return Collections.unmodifiableSet(proofs);
    }

    public String getProofMessage() {
        return proofMessage;
    }

    public void setProofMessage(String message) {
        String original = proofMessage;

        proofMessage = message;
        save();
    }

    public void addProof(String... ids) {
        proofs.addAll(Arrays.stream(ids).toList());

        if (ids.length > 0) save();
    }

    public void setProof(String... ids) {
        proofs.clear();
        proofs.addAll(Arrays.stream(ids).toList());

        save();
    }

    public String getBuilderMessageId() {
        return builderMessageId;
    }

    public void setBuilderMessageId(String id) {
        String original = builderMessageId;

        builderMessageId = id;
        if (!original.equals(builderMessageId)) save();
    }

    public String getBuilderChannelId() {
        return builderChannelId;
    }

    public void setBuilderChannelId(String id) {
        String original = builderChannelId;

        builderChannelId = id;
        if (!original.equals(builderChannelId)) save();
    }

    public void setBuilderId(Message message) {
        builderMessageId = message.getId();
        builderChannelId = message.getChannelId();
        save();
    }
}
