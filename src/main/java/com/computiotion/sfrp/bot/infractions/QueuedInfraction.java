package com.computiotion.sfrp.bot.infractions;

import com.computiotion.sfrp.bot.DatabaseSaveable;
import com.computiotion.sfrp.bot.Generators;
import com.computiotion.sfrp.bot.Snowflake;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.time.Duration;
import java.util.*;

import static com.computiotion.sfrp.bot.ConfigManager.REDIS_PREFIX_INFRACTION_QUEUE;

public class QueuedInfraction implements DatabaseSaveable {
    private static final Snowflake snowflake = new Snowflake();
    private static final Jedis jedis = Generators.getJedis();
    private static final Gson gson = Generators.getGson();

    private List<String> issuers = new ArrayList<>();
    private List<String> signedBy = new ArrayList<>();
    private final List<String> targets = new ArrayList<>();
    private transient String id;
    @SerializedName("guild_id")
    private String guildId;
    private List<Infraction> infractions;
    private String reason;

    private QueuedInfraction(String id) {
        this.id = id;
    }

    public static @NotNull QueuedInfraction createInfraction(String guildId, String issuer) {
        return createInfraction(guildId, List.of(issuer), List.of());
    }

    public static @NotNull QueuedInfraction createInfraction(String guildId, String issuer, List<Infraction> infractions) {
        return createInfraction(guildId, List.of(issuer), infractions);
    }

    public static @NotNull QueuedInfraction createInfraction(String guildId, String issuer, Infraction infraction) {
        return createInfraction(guildId, List.of(issuer), List.of(infraction));
    }

    public static @NotNull QueuedInfraction createInfraction(String guildId, List<String> issuer) {
        return createInfraction(guildId, issuer, List.of());
    }

    public static @NotNull QueuedInfraction createInfraction(String guildId, @NotNull List<String> issuer, @NotNull List<Infraction> infractions) {
        QueuedInfraction infraction = new QueuedInfraction(snowflake.nextId());
        infraction.issuers = issuer.stream().filter(Objects::nonNull).toList();
        infraction.signedBy = new ArrayList<>(infraction.issuers);
        infraction.infractions = infractions.stream().filter(Objects::nonNull).toList();
        infraction.guildId = guildId;

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

    public String getId() { return id; }

    public void addTargets(String... targets) {
        this.targets.addAll(Arrays.stream(targets).toList());

        if (targets.length > 0) save();
    }

    public List<String> getTargets() {
        return Collections.unmodifiableList(targets);
    }

    public void addIssuers(String... issuers) {
        this.issuers.addAll(Arrays.stream(issuers).toList());

        if (issuers.length > 0) save();
    }

    public List<String> getIssuers() {
        return Collections.unmodifiableList(issuers);
    }

    public void addSignage(String... issuers) {
        this.signedBy.addAll(Arrays.stream(issuers).toList());
        this.issuers.addAll(Arrays.stream(issuers).toList());

        if (issuers.length > 0) save();
    }

    public List<String> getSignedBy() {
        return Collections.unmodifiableList(signedBy);
    }

    @Override
    public void save() {
        jedis.set(REDIS_PREFIX_INFRACTION_QUEUE + id, gson.toJson(this), new SetParams()
                .ex(Duration.ofHours(12).toSeconds()));
    }

    public void delete() {
        jedis.del(REDIS_PREFIX_INFRACTION_QUEUE + id);
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
}
