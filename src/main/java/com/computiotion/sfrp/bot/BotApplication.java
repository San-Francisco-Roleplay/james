package com.computiotion.sfrp.bot;

import com.computiotion.sfrp.bot.commands.Command;
import com.computiotion.sfrp.bot.config.ConfigReader;
import com.computiotion.sfrp.bot.config.erlc.ERLCConfig;
import com.computiotion.sfrp.bot.erlc.MessagesCron;
import com.computiotion.sfrp.bot.listeners.CommandLogListener;
import com.computiotion.sfrp.bot.listeners.GuildReadyListener;
import com.computiotion.sfrp.bot.listeners.MessageListener;
import com.computiotion.sfrp.bot.listeners.SlashCommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.Timer;

@SpringBootApplication
public class BotApplication {
    private static JDA jda;
    private static final Log log = LogFactory.getLog(BotApplication.class);

    public static void main(String[] args) throws IOException, ClassNotFoundException, ParserConfigurationException, SAXException {
        SpringApplication.run(BotApplication.class, args);
        ConfigManager.initSentry();

        log.debug("Finding command classes.");
        ClassTools.getSubclassesOf("com.computiotion.sfrp.bot", Command.class).forEach(command -> {
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
            log.debug("Registering Command > " + command.getName());
            Command.registerCommand(instance);
        });

        log.debug("Finished searching commands.");

        jda = JDABuilder.create(ConfigManager.getBotToken(), EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS))
                .addEventListeners(new MessageListener(), new SlashCommandListener(), new GuildReadyListener(), new CommandLogListener())
                .build();

        String name = jda.getSelfUser().getName();
        log.info("Bot \"" + name + "\" is ready");

        Timer t = new Timer();
        ERLCConfig erlc = ConfigReader.fromApplicationDefaults().getErlc();
        MessagesCron mCron = new MessagesCron(erlc);
        // This task is scheduled to run every 10 seconds

        t.scheduleAtFixedRate(mCron, 0, erlc.getMessageDelay());

        jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.customStatus("📍 /help"));
    }

    public static JDA getJda() {
        return jda;
    }
}
