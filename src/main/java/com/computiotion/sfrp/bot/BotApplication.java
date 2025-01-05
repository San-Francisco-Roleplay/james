package com.computiotion.sfrp.bot;

import com.computiotion.sfrp.bot.commands.Command;
import com.computiotion.sfrp.bot.listeners.GuildReadyListener;
import com.computiotion.sfrp.bot.listeners.MessageListener;
import com.computiotion.sfrp.bot.listeners.SlashCommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;

@SpringBootApplication
public class BotApplication {

    private static final Log log = LogFactory.getLog(BotApplication.class);

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        SpringApplication.run(BotApplication.class, args);

        ClassTools.getSubclassesOf("com.computiotion.sfrp.bot.commands", Command.class).forEach(command -> {
            Constructor<? extends Command> constructor;
            try {
                constructor = command.getConstructor();
            } catch (NoSuchMethodException e) {
                log.fatal(new NoSuchMethodException("No constructor taking no parameters found for class " + command.getName()));
                return;
            }

            constructor.setAccessible(true);
            Command instance;
            try {
                instance = constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            Command.registerCommand(instance);
        });

        JDA jda = JDABuilder.create(ConfigManager.getBotToken(), EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
                .addEventListeners(new MessageListener(), new SlashCommandListener(), new GuildReadyListener())
                .build();

        String name = jda.getSelfUser().getName();
        log.info("Bot \"" + name + "\" is ready");
    }

}
