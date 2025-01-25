package com.computiotion.sfrp.bot;

import com.computiotion.sfrp.bot.adapters.*;
import com.computiotion.sfrp.bot.components.ComponentData;
import com.computiotion.sfrp.bot.infractions.Infraction;
import com.computiotion.sfrp.bot.infractions.InfractionType;
import com.computiotion.sfrp.bot.reference.ReferenceData;
import com.computiotion.sfrp.bot.reference.ReferencePayload;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;

public class Generators {
    private static final Jedis jedis = createJedis();

    private static @NotNull Jedis createJedis() {
        Jedis jedis = new Jedis(ConfigManager.getRedisUrl(), ConfigManager.getRedisPort(), true);
        jedis.auth(ConfigManager.getRedisPassword());

        return jedis;
    }

    public static @NotNull Jedis getJedis() {
        return jedis;
    }

    public static @NotNull Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
//                .registerTypeAdapter(InfractionType.class, new InfractionTypeAdapter())
                .registerTypeAdapter(ComponentData.class, new ComponentDataAdapter())
                .registerTypeAdapter(ReferencePayload.class, new ReferencePayloadAdapter())
                .registerTypeAdapterFactory(new InfractionAdapterFactory())
                .create();
    }

    public static @NotNull S3Client getS3() {
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(ConfigManager.getR2KeyId(), ConfigManager.getR2AccessKey())
                ))
                .region(Region.US_EAST_1) // R2 uses Region US_EAST_1 for compatibility
                .endpointOverride(URI.create(ConfigManager.getR2Endpoint()))
                .serviceConfiguration(S3Configuration.builder()
                        .chunkedEncodingEnabled(false)
                        .checksumValidationEnabled(false) // Disable checksum validation
                        .build())
                .build();
    }
}
