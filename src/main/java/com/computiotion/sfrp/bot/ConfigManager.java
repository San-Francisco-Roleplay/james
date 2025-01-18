package com.computiotion.sfrp.bot;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebSecurity
public class ConfigManager implements WebMvcConfigurer {
    public static final String REDIS_PREFIX_PERM_COMPONENT = "component:";
    public static final String REDIS_PREFIX_INFRACTION_HISTORY = "staff:";
    public static final String REDIS_PREFIX_INFRACTION_COLLECTION = "infract:coll:";
    public static final String REDIS_PREFIX_INFRACTION_QUEUE = "infract:pending:";

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // Allow all origins
        configuration.setAllowedMethods(List.of("*")); // Allow all methods (GET, POST, PUT, DELETE, etc.)
        configuration.setAllowedHeaders(List.of("*"));
//        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults()).authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("file:images/");
    }

    /**
     * Retrieves the Discord bot token from {@code .env}.
     *
     * @return The token, as defined in the .env file.
     * @throws NullPointerException If the token {@code DISCORD_TOKEN} is not defined in the env file.
     */
    @NotNull
    public static String getERLCApiKey() {
        String value = System.getenv("ERLC_API_KEY");
        if (value == null) throw new NullPointerException("No ERLC_API_KEY was found in the .env file.");
        return value;
    }

    /**
     * Retrieves the Discord bot token from {@code .env}.
     *
     * @return The token, as defined in the .env file.
     * @throws NullPointerException If the token {@code DISCORD_TOKEN} is not defined in the env file.
     */
    @NotNull
    public static String getBotToken() {
        String value = System.getenv("DISCORD_TOKEN");
        if (value == null) throw new NullPointerException("No DISCORD_TOKEN was found in the .env file.");
        return value;
    }

    /**
     * Retrieves the setup password from {@code .env}.
     *
     * @return The password, as defined in the .env file.
     * @throws NullPointerException If the token {@code SETUP_PASSWORD} is not defined in the env file.
     */
    @NotNull
    public static String getSetupPassword() {
        String value = System.getenv("SETUP_PASSWORD");
        if (value == null) throw new NullPointerException("No SETUP_PASSWORD was found in the .env file.");
        return value;
    }

    /**
     * Retrieves the Redis URL from {@code .env}.
     *
     * @return The URL, as defined in the .env file.
     * @throws NullPointerException If the token {@code REDIS_URL} is not defined in the env file.
     */
    @NotNull
    public static String getRedisUrl() {
        String value = System.getenv("REDIS_URL");
        if (value == null) throw new NullPointerException("No REDIS_URL was found in the .env file.");
        return value;
    }

    /**
     * Retrieves the Redis password from {@code .env}.
     *
     * @return The password, as defined in the .env file.
     * @throws NullPointerException If the token {@code REDIS_PASSWORD} is not defined in the env file.
     */
    @NotNull
    public static String getRedisPassword() {
        String value = System.getenv("REDIS_PASSWORD");
        if (value == null) throw new NullPointerException("No REDIS_PASSWORD was found in the .env file.");
        return value;
    }

    /**
     * Retrieves the Redis port from {@code .env}.
     *
     * @return The port, as defined in the .env file.
     * @throws NullPointerException If the token {@code REDIS_PORT} is not defined in the env file.
     */
    public static int getRedisPort() {
        String value = System.getenv("REDIS_PORT");
        if (value == null) throw new NullPointerException("No REDIS_PORT was found in the .env file.");
        return Integer.parseInt(value);
    }

    /**
     * Retrieves the Bloxlink API key from {@code .env}.
     *
     * @return The key, as defined in the .env file.
     * @throws NullPointerException If the token {@code BLOXLINK_API_KEY} is not defined in the env file.
     */
    public static @NotNull String getBloxlinkApiKey() {
        String value = System.getenv("BLOXLINK_API_KEY");
        if (value == null) throw new NullPointerException("No BLOXLINK_API_KEY was found in the .env file.");
        return value;
    }

    /**
     * Retrieves the ERM API key from {@code .env}.
     *
     * @return The key, as defined in the .env file.
     * @throws NullPointerException If the token {@code ERM_API_KEY} is not defined in the env file.
     */
    public static @NotNull String getErmAPIKey() {
        String value = System.getenv("ERM_API_KEY");
        if (value == null) throw new NullPointerException("No ERM_API_KEY was found in the .env file.");
        return value;
    }

    /**
     * Retrieves the ERM Guild ID from {@code .env}.
     *
     * @return The ID, as defined in the .env file.
     * @throws NullPointerException If the token {@code ERM_GUILD_ID} is not defined in the env file.
     */
    public static @NotNull String getErmGuild() {
        String value = System.getenv("ERM_GUILD_ID");
        if (value == null) throw new NullPointerException("No ERM_GUILD_ID was found in the .env file.");
        return value;
    }
}
