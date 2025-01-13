package com.computiotion.sfrp.bot.commands;

import com.computiotion.sfrp.bot.Colors;
import com.computiotion.sfrp.bot.Emoji;
import com.computiotion.sfrp.bot.config.*;
import com.computiotion.sfrp.bot.templates.InternalError;
import com.computiotion.sfrp.bot.templates.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class Command {
    public static final Set<Command> commands = new HashSet<>();
    private static final Set<Class<? extends Annotation>> parameterAnnotations = Set.of(Boolean.class, Channel.class, Integer.class, Mentionable.class, Number.class, Role.class, Text.class, User.class);
    private static final Map<String, java.lang.Boolean> bools = Map.of("true", true, "false", false, "yes", true, "no", false);
    private static final Log log = LogFactory.getLog(Command.class);

    public static void registerCommand(Command command) {
        commands.add(command);
    }

    /**
     * Parses the method parameters and extracts their annotations.
     *
     * @param method the method to parse
     * @return a list of parameter details including name, required flag, and annotation type
     * @throws IllegalArgumentException if no parameter annotation is found for a parameter
     */
    public static @NotNull List<ParsedParameter> parseMethod(@NotNull Method method) {
        log.trace("parseMethod > Parsing method " + method.getName());
        Parameter[] parameters = method.getParameters();
        List<ParsedParameter> params = new ArrayList<>();

        boolean foundOptional = false;
        int i = 0;
        for (Parameter parameter : parameters) {
            if (i == 0) {
                i++;
                continue;
            }
            Class<? extends Annotation> annotation = null;
            log.trace("parseMethod > Parsing parameter " + parameter.getName());

            for (Class<? extends Annotation> paramAnnotation : parameterAnnotations) {
                if (parameter.isAnnotationPresent(paramAnnotation)) {
                    annotation = paramAnnotation;
                    break;
                }
            }

            if (annotation == null) {
                log.trace("parseMethod > No annotation found on " + parameter.getName());
                throw new IllegalArgumentException("No parameter annotation found for parameter " + parameter.getName() + " in " + method.getDeclaringClass().getName() + "#" + method.getName());
            }

            log.trace("parseMethod > Found type: " + annotation.getName());

            Annotation annotationData = parameter.getDeclaredAnnotation(annotation);
            ParameterType type = null;
            boolean required = false;
            String name = null;
            String description = null;

            if (annotationData instanceof Boolean) {
                required = ((Boolean) annotationData).required();
                name = ((Boolean) annotationData).value();
                description = ((Boolean) annotationData).description();
                type = ParameterType.BOOLEAN;

                log.trace("parseMethod > Parameter information > name: " + name + " type: " + type.name() + " required: " + required);

                if (parameter.getType() != java.lang.Boolean.class) {
                    throw new IllegalArgumentException("Parameter " + parameter.getName() + " in " + method.getDeclaringClass().getName() + "#" + method.getName() + " is annotated with @Boolean but is not of type Boolean");
                }
            } else if (annotationData instanceof Channel) {
                required = ((Channel) annotationData).required();
                name = ((Channel) annotationData).value();
                description = ((Channel) annotationData).description();
                type = ParameterType.CHANNEL;

                log.trace("parseMethod > Parameter information > name: " + name + " type: " + type.name() + " required: " + required);

                if (parameter.getType() != net.dv8tion.jda.api.entities.channel.Channel.class) {
                    throw new IllegalArgumentException("Parameter " + parameter.getName() + " in " + method.getDeclaringClass().getName() + "#" + method.getName() + " is annotated with @Channel but is not of type Channel");
                }
            } else if (annotationData instanceof Integer) {
                required = ((Integer) annotationData).required();
                name = ((Integer) annotationData).value();
                description = ((Integer) annotationData).description();
                type = ParameterType.INTEGER;

                log.trace("parseMethod > Parameter information > name: " + name + " type: " + type.name() + " required: " + required);

                if (parameter.getType() != java.lang.Integer.class) {
                    throw new IllegalArgumentException("Parameter " + parameter.getName() + " in " + method.getDeclaringClass().getName() + "#" + method.getName() + " is annotated with @Integer but is not of type Integer");
                }
            } else if (annotationData instanceof Mentionable) {
                required = ((Mentionable) annotationData).required();
                name = ((Mentionable) annotationData).value();
                description = ((Mentionable) annotationData).description();
                type = ParameterType.MENTIONABLE;

                log.trace("parseMethod > Parameter information > name: " + name + " type: " + type.name() + " required: " + required);

                if (parameter.getType() != IMentionable.class) {
                    throw new IllegalArgumentException("Parameter " + parameter.getName() + " in " + method.getDeclaringClass().getName() + "#" + method.getName() + " is annotated with @Mentionable but is not of type IMentionable");
                }
            } else if (annotationData instanceof Number) {
                required = ((Number) annotationData).required();
                name = ((Number) annotationData).value();
                description = ((Number) annotationData).description();
                type = ParameterType.NUMBER;

                log.trace("parseMethod > Parameter information > name: " + name + " type: " + type.name() + " required: " + required);

                if (parameter.getType() != java.lang.Double.class) {
                    log.error("parseMethod > An error occurred.");

                    throw new IllegalArgumentException("Parameter " + parameter.getName() + " in " + method.getDeclaringClass().getName() + "#" + method.getName() + " is annotated with @Number but is not of type Number");
                }
            } else if (annotationData instanceof Role) {
                required = ((Role) annotationData).required();
                name = ((Role) annotationData).value();
                description = ((Role) annotationData).description();
                type = ParameterType.ROLE;

                log.trace("parseMethod > Parameter information > name: " + name + " type: " + type.name() + " required: " + required);

                if (parameter.getType() != net.dv8tion.jda.api.entities.Role.class) {
                    throw new IllegalArgumentException("Parameter " + parameter.getName() + " in " + method.getDeclaringClass().getName() + "#" + method.getName() + " is annotated with @Role but is not of type Role");
                }
            } else if (annotationData instanceof Text) {
                required = ((Text) annotationData).required();
                name = ((Text) annotationData).value();
                description = ((Text) annotationData).description();
                type = ParameterType.TEXT;

                log.trace("parseMethod > Parameter information > name: " + name + " type: " + type.name() + " required: " + required);

                if (parameter.getType() != String.class) {
                    throw new IllegalArgumentException("Parameter " + parameter.getName() + " in " + method.getDeclaringClass().getName() + "#" + method.getName() + " is annotated with @Text but is not of type String");
                }
            } else if (annotationData instanceof User) {
                required = ((User) annotationData).required();
                name = ((User) annotationData).value();
                description = ((User) annotationData).description();
                type = ParameterType.USER;

                log.trace("parseMethod > Parameter information > name: " + name + " type: " + type.name() + " required: " + required);

                if (parameter.getType() != net.dv8tion.jda.api.entities.User.class) {
                    throw new IllegalArgumentException("Parameter " + parameter.getName() + " in " + method.getDeclaringClass().getName() + "#" + method.getName() + " is annotated with @User but is not of type User");
                }
            }

            log.trace("parseMethod > Adding parameter.");
            params.add(new ParsedParameterBuilder()
                    .setName(name)
                    .setDescription(description)
                    .setRequired(required)
                    .setType(type)
                    .build());
            if (!required) {
                foundOptional = true;
            }
            if (foundOptional && required) {
                log.error("parseMethod > Error found in " + method.getDeclaringClass().getName() + "#" + method.getName());
                throw new IllegalStateException("Optional parameters must come after required parameters");
            }
            i++;
        }

        log.trace("parseMethod > Finished parsing method " + method.getName());
        return params;
    }

    public static @NotNull String generateUsage(@NotNull String prefix, @NotNull List<ParsedParameter> params) {
        StringBuilder res = new StringBuilder(prefix.trim());

        for (ParsedParameter param : params) {
            res.append(" ");
            if (param.isRequired()) {
                res.append("<");
            } else {
                res.append("[");
            }

            res.append(param.getName());
            res.append(": ");
            res.append(param.getType().name().toLowerCase());

            if (param.isRequired()) {
                res.append(">");
            } else {
                res.append("]");
            }
        }

        return res.toString();
    }

    public static PermissionLevelDefault canExecute(@NotNull Message message, @NotNull CommandExecutor executor) throws ParserConfigurationException, IOException, SAXException {
        PermissionLevel level = executor.level();
        Config config = ConfigReader.fromApplicationDefaults();

        CommandConfig commands = config.getCommands();
        Map<PermissionLevel, PermissionLevelData> permissions = commands.getPermissions();

        PermissionLevelData levelData = permissions.get(level);
        PermissionLevelDefault base = levelData.getBase();

        PermissionLevelParam guilds = levelData.getGuilds();
        PermissionLevelParam channels = levelData.getChannels();
        PermissionLevelParam roles = levelData.getRoles();
        PermissionLevelParam users = levelData.getUser();

        net.dv8tion.jda.api.entities.User author = message.getAuthor();
        Guild guild = message.getGuild();
        Member member = guild.getMemberById(author.getId());
        List<net.dv8tion.jda.api.entities.Role> userRoles = new ArrayList<>();

        if (member != null) {
            userRoles = member.getRoles();
        }
//        roles.deny().stream().filter(role -> )

        if (base == PermissionLevelDefault.ALLOW) {
            if (guilds.deny().contains(guild.getId())) return PermissionLevelDefault.DENY;
            if (channels.deny().contains(message.getChannelId())) return PermissionLevelDefault.DENY;
            if (userRoles.stream().map(ISnowflake::getId).anyMatch(role -> roles.deny().contains(role)))
                return PermissionLevelDefault.DENY;
            if (users.deny().contains(author.getId())) return PermissionLevelDefault.DENY;
            log.debug("No overriding clauses found for " + base.name() + " – is this an error?");
        } else if (base == PermissionLevelDefault.DENY) {
            if (guilds.allow().contains(guild.getId())) return PermissionLevelDefault.ALLOW;
            if (channels.allow().contains(message.getChannelId())) return PermissionLevelDefault.ALLOW;
            if (userRoles.stream().map(ISnowflake::getId).anyMatch(role -> roles.allow().contains(role)))
                return PermissionLevelDefault.ALLOW;
            if (users.allow().contains(author.getId())) return PermissionLevelDefault.ALLOW;
            log.debug("No overriding clauses found for " + base.name() + " – is this an error?");
        }

        return base;
    }

    public static PermissionLevelDefault canExecute(@NotNull SlashCommandInteractionEvent interaction, @NotNull CommandExecutor executor) throws ParserConfigurationException, IOException, SAXException {
        PermissionLevel level = executor.level();
        Config config = ConfigReader.fromApplicationDefaults();

        CommandConfig commands = config.getCommands();
        Map<PermissionLevel, PermissionLevelData> permissions = commands.getPermissions();

        PermissionLevelData levelData = permissions.get(level);
        PermissionLevelDefault base = levelData.getBase();

        PermissionLevelParam guilds = levelData.getGuilds();
        PermissionLevelParam channels = levelData.getChannels();
        PermissionLevelParam roles = levelData.getRoles();
        PermissionLevelParam users = levelData.getUser();

        net.dv8tion.jda.api.entities.User author = interaction.getUser();
        Guild guild = interaction.getGuild();

        if (guild == null) return PermissionLevelDefault.DENY;

        Member member = guild.getMemberById(author.getId());
        List<net.dv8tion.jda.api.entities.Role> userRoles = new ArrayList<>();

        if (member != null) {
            userRoles = member.getRoles();
        }
//        roles.deny().stream().filter(role -> )

        if (base == PermissionLevelDefault.ALLOW) {
            if (guilds.deny().contains(guild.getId())) return PermissionLevelDefault.DENY;
            if (channels.deny().contains(interaction.getChannelId())) return PermissionLevelDefault.DENY;
            if (userRoles.stream().map(ISnowflake::getId).anyMatch(role -> roles.deny().contains(role)))
                return PermissionLevelDefault.DENY;
            if (users.deny().contains(author.getId())) return PermissionLevelDefault.DENY;
            log.debug("No overriding clauses found for " + base.name() + " – is this an error?");
        } else if (base == PermissionLevelDefault.DENY) {
            if (guilds.allow().contains(guild.getId())) return PermissionLevelDefault.ALLOW;
            if (channels.allow().contains(interaction.getChannelId())) return PermissionLevelDefault.ALLOW;
            if (userRoles.stream().map(ISnowflake::getId).anyMatch(role -> roles.allow().contains(role)))
                return PermissionLevelDefault.ALLOW;
            if (users.allow().contains(author.getId())) return PermissionLevelDefault.ALLOW;
            log.debug("No overriding clauses found for " + base.name() + " – is this an error?");
        }

        return base;
    }

    public static void executeFromMessage(@NotNull Message message, @NotNull String content) throws ParserConfigurationException, IOException, SAXException {
        log.trace("Adding Emoji");
        message.addReaction(Emoji.Loading.toJda()).complete();
        log.trace("Finished adding Emoji");
        String[] split = content.split(" ");
        String commandStr = split[0];

        Command command = null;

        log.trace("Searching for command...");
        for (Command cmd : commands) {
            Class<? extends Command> cls = cmd.getClass();
            CommandController annotation = cls.getAnnotation(CommandController.class);
            if (annotation == null) {
                continue;
            }

            String name = annotation.value();
            if (name.equalsIgnoreCase(commandStr)) {
                command = cmd;
                break;
            }
        }

        log.trace("Finished searching for command class.");
        if (command == null) {
            log.trace("Command is null.");
            message.removeReaction(Emoji.Loading.toJda()).complete();
            message.replyEmbeds(new NoCommand(commandStr).makeEmbed().build()).complete();
            return;
        }
        log.trace("Class is not null: " + command.getClass().getName());

        Class<? extends Command> commandClass = command.getClass();
        Method[] methods = commandClass.getMethods();
        HashMap<String, Method> subToMethod = new HashMap<>();

        CommandController controller = commandClass.getAnnotation(CommandController.class);
        if (controller == null) {
            throw new IllegalArgumentException("Commands must be annotated with CommandController.");
        }
        if (controller.guildOnly() && !message.hasGuild()) {
            message.replyEmbeds(new NoDMs().makeEmbed().build())
                    .queue();
        }

        log.trace("Searching methods: ");
        for (Method method : methods) {
            CommandExecutor annotation = method.getAnnotation(CommandExecutor.class);
            if (annotation == null) {
                continue;
            }

            String name = annotation.value();
            log.trace("Subcommand \"" + name + "\" maps to " + method.getName());
            subToMethod.put(name, method);
        }

        if (subToMethod.containsKey("*")) {
            throw new IllegalArgumentException("Subcommands may not contain an *");
        }

        boolean isMain = subToMethod.containsKey("");
        log.trace("isMain: " + isMain);

        if (split.length == 1 && !isMain) {
            log.trace("Missing subcommand.");
            message.removeReaction(Emoji.Loading.toJda()).complete();


            message.replyEmbeds(new EmbedBuilder()
                    .setTitle("Subcommand Required")
                    .setDescription(String.format("Available sub-commands include: \n```\n%s\n```", subToMethod.entrySet()
                            .stream()
                            .filter(entry -> {
                                try {
                                    return canExecute(message, entry.getValue().getAnnotation(CommandExecutor.class)) == PermissionLevelDefault.ALLOW;
                                } catch (ParserConfigurationException | IOException | SAXException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .map(Map.Entry::getKey)
                            .map(s -> commandStr + " " + s)
                            .reduce((s1, s2) -> s1 + "\n" + s2).orElse("")))
                    .setColor(Colors.Red.getColor())
                    .build()).complete();
            return;
        }

        String subCommand = isMain ? "" : split[1].split(" ", 2)[0];

        Method method = subToMethod.get(subCommand);
        if (method == null) {
            log.trace("No method found for subcommand " + subCommand);
            message.removeReaction(Emoji.Loading.toJda()).complete();
            message.replyEmbeds(new NoSubcommand(commandStr + " " + subCommand).makeEmbed().build()).complete();
            return;
        }

        log.trace("Method found: " + method.getName());
        CommandExecutor executor = method.getAnnotation(CommandExecutor.class);

        if (canExecute(message, executor) == PermissionLevelDefault.DENY) {
            log.debug("Lacking permissions to run command, returning.");
            Config config = ConfigReader.fromApplicationDefaults();

            CommandConfig commands = config.getCommands();
            Map<PermissionLevel, PermissionLevelData> permissions = commands.getPermissions();

            PermissionLevelData levelData = permissions.get(executor.level());
            message.removeReaction(Emoji.Loading.toJda()).complete();

            if (levelData.isSilent()) {
                return;
            }

            message.replyEmbeds(new NoPerms().makeEmbed().build()).complete();
            log.debug("Finished returning.");
            return;
        }

        log.trace("Parsing method for parameters.");
        List<ParsedParameter> params = parseMethod(method);
        log.trace("Finished parsing, filtering to required.");
        List<ParsedParameter> requiredParams = new ArrayList<>();

        for (ParsedParameter param : params) {
            if (param.isRequired()) {
                log.trace("Required parameter found: " + param.getName());
                requiredParams.add(param);
            }
        }
        log.trace("Finished filtering to required.");


        String args = content.substring(commandStr.length() + (isMain ? 0 : subCommand.length() + 1)).trim();
        log.trace("Parsing arguments: " + args);
        List<String> argList = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(args);
        while (m.find()) {
            log.trace("New argument found.");
            argList.add(m.group(1).replace("\"", ""));
        }

        if (argList.size() < requiredParams.size()) {
            log.trace("Fewer provided arguments that required parameters.");
            message.removeReaction(Emoji.Loading.toJda()).complete();
            message.replyEmbeds(new InvalidFormat(generateUsage(commandStr + " " + subCommand, params)).makeEmbed().build()).complete();
            return;
        }

        if (argList.size() > params.size()) {
            message.removeReaction(Emoji.Loading.toJda()).complete();
            message.replyEmbeds(new InvalidFormat(generateUsage(commandStr + " " + subCommand, params)).makeEmbed().build()).complete();
            return;
        }

        List<Object> computed = new ArrayList<>();
        log.trace("Computing arguments.");
        int i = 0;
        for (String arg : argList) {
            log.trace("Argument > " + arg);
            if (arg.isEmpty()) {
                log.trace("Argument is empty, exiting.");
                message.removeReaction(Emoji.Loading.toJda()).complete();
                message.replyEmbeds(new InvalidFormat(generateUsage(commandStr + " " + subCommand, params)).makeEmbed().build()).complete();
                return;
            }

            if (arg.startsWith("\"") && arg.endsWith("\"")) {
                log.trace("Fewer provided arguments that required parameters.");
                arg = arg.substring(1, arg.length() - 1);
            }

            log.trace("Parsing argument: " + arg);

            var param = params.get(i);
            ParameterType type = param.getType();
            log.trace("Mapped to parameter: " + param.getName());
            if (type == ParameterType.BOOLEAN) {
                java.lang.Boolean bool = bools.get(arg.toLowerCase());
                if (bool == null) {
                    log.trace("Value is not valid boolean: " + arg);
                    message.removeReaction(Emoji.Loading.toJda()).complete();
                    message.replyEmbeds(new InvalidFormat(generateUsage(commandStr + " " + subCommand, params)).makeEmbed().build()).complete();
                    return;
                }
                computed.add(bool);
            } else if (type == ParameterType.CHANNEL) {
                String finalArg = arg;
                GuildChannel channel = message.getMentions().getChannels().stream().
                        filter(c -> c.getAsMention().equalsIgnoreCase(finalArg) || c.getId().equalsIgnoreCase(finalArg))
                        .findFirst()
                        .orElse(null);

                if (channel == null) {
                    log.trace("Value is not valid channel: " + arg);
                    message.removeReaction(Emoji.Loading.toJda()).complete();
                    message.replyEmbeds(new InvalidFormat(generateUsage(commandStr + " " + subCommand, params)).makeEmbed().build()).complete();
                    return;
                }

                computed.add(channel);
            } else if (type == ParameterType.INTEGER) {
                try {
                    computed.add(java.lang.Integer.parseInt(arg));
                } catch (NumberFormatException e) {
                    log.trace("Value is not valid integer: " + arg);
                    message.removeReaction(Emoji.Loading.toJda()).complete();
                    message.replyEmbeds(new InvalidFormat(generateUsage(commandStr + " " + subCommand, params)).makeEmbed().build()).complete();
                    return;
                }
            } else if (type == ParameterType.MENTIONABLE) {
                String finalArg = arg;
                IMentionable mentionable = message.getMentions().getMentions(Message.MentionType.USER, Message.MentionType.ROLE).stream()
                        .filter(mention -> mention.getAsMention().equalsIgnoreCase(finalArg) || mention.getId().equalsIgnoreCase(finalArg))
                        .findFirst()
                        .orElse(null);

                if (mentionable == null) {
                    log.trace("Value is not valid role/user: " + arg);
                    message.removeReaction(Emoji.Loading.toJda()).complete();
                    message.replyEmbeds(new InvalidFormat(generateUsage(commandStr + " " + subCommand, params)).makeEmbed().build()).complete();
                    return;
                }

                computed.add(mentionable);
            } else if (type == ParameterType.NUMBER) {
                try {
                    computed.add(Double.parseDouble(arg));
                } catch (NumberFormatException e) {
                    log.trace("Value is not valid double: " + arg);
                    message.removeReaction(Emoji.Loading.toJda()).complete();
                    message.replyEmbeds(new InvalidFormat(generateUsage(commandStr + " " + subCommand, params)).makeEmbed().build()).complete();
                    return;
                }
            } else if (type == ParameterType.ROLE) {
                String finalArg = arg;
                net.dv8tion.jda.api.entities.Role role = message.getMentions().getRoles().stream()
                        .filter(r -> r.getAsMention().equalsIgnoreCase(finalArg) || r.getId().equalsIgnoreCase(finalArg))
                        .findFirst()
                        .orElse(null);

                if (role == null) {
                    log.trace("Value is not valid role: " + arg);
                    message.removeReaction(Emoji.Loading.toJda()).complete();
                    message.replyEmbeds(new InvalidFormat(generateUsage(commandStr + " " + subCommand, params)).makeEmbed().build()).complete();
                    return;
                }

                computed.add(role);
            } else if (type == ParameterType.TEXT) {
                computed.add(arg);
            } else if (type == ParameterType.USER) {
                String finalArg = arg;
                net.dv8tion.jda.api.entities.User user = message.getMentions().getUsers().stream()
                        .filter(mem -> mem.getAsMention().equalsIgnoreCase(finalArg) || mem.getId().equalsIgnoreCase(finalArg))
                        .findFirst()
                        .orElse(null);

                if (user == null) {
                    log.trace("Value is not valid user: " + arg);
                    message.removeReaction(Emoji.Loading.toJda()).complete();
                    message.replyEmbeds(new InvalidFormat(generateUsage(commandStr + " " + subCommand, params)).makeEmbed().build()).complete();
                    return;
                }

                computed.add(user);
            }
            i++;
        }

        if (computed.size() > params.size()) {
            log.trace("More parameters than expected.");
            message.removeReaction(Emoji.Loading.toJda()).complete();
            message.replyEmbeds(new InvalidFormat(generateUsage(commandStr + " " + subCommand, params)).makeEmbed().build()).complete();
            return;
        }

        int passed = computed.size();
        int paramSize = params.size();

        for (int j = 0; j < paramSize - passed; j++) {
            var param = params.get(j + passed);
            if (param.isRequired()) {
                message.removeReaction(Emoji.Loading.toJda()).complete();
                message.replyEmbeds(new InvalidFormat(generateUsage(commandStr + " " + subCommand, params)).makeEmbed().build()).complete();
                return;
            }

            computed.add(null);
        }

        MessageCommandInteraction interaction = new MessageCommandInteraction(
                method,
                message,
                message.getAuthor(),
                message.getGuild(),
                CommandInteractionType.MESSAGE);

        computed.addFirst(interaction);

        log.trace("Computed arguments: " + computed.stream().map(obj -> obj == null ? "null" : obj.getClass().getSimpleName()).collect(Collectors.joining(", ")));
        try {
            log.debug("Invoking method on command " + commandClass.getName() + "#" + method.getName() + " (" + (commandStr + " " + subCommand).trim() + ")");
            method.setAccessible(true); // Ensure the method is accessible

            message.removeReaction(Emoji.Loading.toJda()).complete();
            log.trace("Removed reaction");

            method.invoke(command, computed.toArray());
            log.debug("Method invoked successfully");
        } catch (RuntimeException | IllegalAccessException | InvocationTargetException e) {
            log.error("Error invoking method: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static void executeFromSlash(@NotNull SlashCommandInteractionEvent interaction) throws ParserConfigurationException, IOException, SAXException {
        String[] split = interaction.getFullCommandName().split(" ");
        String commandStr = split[0];

        Command command = null;

        log.trace("Searching for command...");
        for (Command cmd : commands) {
            Class<? extends Command> cls = cmd.getClass();
            CommandController annotation = cls.getAnnotation(CommandController.class);
            if (annotation == null) {
                continue;
            }

            String name = annotation.value();
            if (name.equalsIgnoreCase(commandStr)) {
                command = cmd;
                break;
            }
        }

        log.trace("Finished searching for command class.");
        if (command == null) {
            log.trace("Command is null.");
            interaction.replyEmbeds(new InternalError().makeEmbed().build())
                    .setEphemeral(true)
                    .complete();
            return;
        }
        log.trace("Class is not null: " + command.getClass().getName());

        Class<? extends Command> commandClass = command.getClass();
        Method[] methods = commandClass.getMethods();
        HashMap<String, Method> subToMethod = new HashMap<>();

        log.trace("Searching methods: ");
        for (Method method : methods) {
            CommandExecutor annotation = method.getAnnotation(CommandExecutor.class);
            if (annotation == null) {
                continue;
            }

            String name = annotation.value();
            log.trace("Subcommand \"" + name + "\" maps to " + method.getName());
            subToMethod.put(name, method);
        }

        boolean isMain = subToMethod.containsKey("");
        log.trace("isMain: " + isMain);

        if (split.length == 1 && !isMain) {
            log.trace("Missing subcommand.");
            interaction.replyEmbeds(new EmbedBuilder()
                            .setTitle("Subcommand Required")
                            .setDescription(String.format("Available sub-commands include: \n```\n%s\n```", subToMethod.entrySet()
                                    .stream()
                                    .filter(entry -> {
                                        try {
                                            return canExecute(interaction, entry.getValue().getAnnotation(CommandExecutor.class)) == PermissionLevelDefault.ALLOW;
                                        } catch (ParserConfigurationException | IOException | SAXException e) {
                                            throw new RuntimeException(e);
                                        }
                                    })
                                    .map(Map.Entry::getKey)
                                    .map(s -> commandStr + " " + s)
                                    .reduce((s1, s2) -> s1 + "\n" + s2).orElse("")))
                            .setColor(Colors.Red.getColor())
                            .build())
                    .setEphemeral(true)
                    .complete();
            return;
        }

        String subCommand = isMain ? "" : split[1].split(" ", 2)[0];

        Method method = subToMethod.get(subCommand);
        if (method == null) {
            log.trace("No method found for subcommand " + subCommand);
            interaction.replyEmbeds(new InternalError().makeEmbed().build())
                    .setEphemeral(true)
                    .complete();
            return;
        }
        log.trace("Method found: " + method.getName());

        CommandExecutor executor = method.getAnnotation(CommandExecutor.class);

        if (canExecute(interaction, executor) == PermissionLevelDefault.DENY) {
            log.trace("Lacking permissions, returning.");
            Config config = ConfigReader.fromApplicationDefaults();

            CommandConfig commands = config.getCommands();
            Map<PermissionLevel, PermissionLevelData> permissions = commands.getPermissions();

            PermissionLevelData levelData = permissions.get(executor.level());

            if (levelData.isSilent()) {
                return;
            }

            interaction.replyEmbeds(new NoPerms().makeEmbed().build())
                    .setEphemeral(true)
                    .complete();
            return;
        }

        log.trace("Parsing method for parameters.");
        List<ParsedParameter> params = parseMethod(method);

        List<Object> computed = new ArrayList<>();

        for (var val : params) {
            String name = val.getName();
            ParameterType type = val.getType();

            Object typed = null;
            OptionMapping option = interaction.getOption(name);

            if (option == null) {
                computed.add(null);
                continue;
            }

            switch (type) {
                case ROLE -> typed = option.getAsRole();
                case TEXT -> typed = option.getAsString();
                case USER -> typed = option.getAsUser();
                case NUMBER -> typed = option.getAsDouble();
                case BOOLEAN -> typed = option.getAsBoolean();
                case CHANNEL -> typed = option.getAsChannel();
                case INTEGER -> typed = option.getAsInt();
                case MENTIONABLE -> typed = option.getAsMentionable();
            }

            computed.add(typed);
        }

        int passed = computed.size();
        int paramSize = params.size();

        for (int j = 0; j < paramSize - passed; j++) {
            var param = params.get(j + passed);
            if (param.isRequired()) {
                interaction.replyEmbeds(new InvalidFormat(generateUsage(commandStr + " " + subCommand, params)).makeEmbed().build())
                        .setEphemeral(true)
                        .complete();
                return;
            }

            computed.add(null);
        }

        SlashCommandInteraction slashInteraction = new SlashCommandInteraction(
                method,
                interaction,
                interaction.getUser(),
                interaction.getGuild(),
                CommandInteractionType.SLASH);

        computed.addFirst(slashInteraction);

        try {
            log.debug("Invoking method on command " + commandClass.getName() + "#" + method.getName() + " (" + (commandStr + " " + subCommand).trim() + ")");
            method.setAccessible(true); // Ensure the method is accessible

            log.debug("Types of computed: [" + computed.stream().map(obj -> {
                if (obj == null) return "null";
                return obj.getClass().getName();
            }).collect(Collectors.joining(", ")) + "]");

            method.invoke(command, computed.toArray());
            log.debug("Method invoked successfully");
        } catch (RuntimeException | IllegalAccessException | InvocationTargetException e) {
            log.error("Error invoking method: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static @NotNull List<OptionData> parsedToOptions(@NotNull List<ParsedParameter> parsed) {
        List<OptionData> res = new ArrayList<>();

        for (ParsedParameter param : parsed) {
            ParameterType type = param.getType();
            OptionType jdaType = null;
            switch (type) {
                case ROLE -> jdaType = OptionType.ROLE;
                case TEXT -> jdaType = OptionType.STRING;
                case USER -> jdaType = OptionType.USER;
                case NUMBER -> jdaType = OptionType.NUMBER;
                case BOOLEAN -> jdaType = OptionType.BOOLEAN;
                case CHANNEL -> jdaType = OptionType.CHANNEL;
                case INTEGER -> jdaType = OptionType.INTEGER;
                case MENTIONABLE -> jdaType = OptionType.MENTIONABLE;
            }
            OptionData data = new OptionData(jdaType, param.getName(), param.getDescription());
            data.setRequired(param.isRequired());
            res.add(data);
        }

        return res;
    }

    public static void registerCommands(@NotNull Guild guild) {
        log.debug("Registering commands for guild " + guild.getId());
        CommandData[] cmds = commands.stream().map(command -> {
            Class<? extends Command> commandClass = command.getClass();
            CommandController annotation = commandClass.getAnnotation(CommandController.class);
            if (annotation == null) {
                throw new IllegalArgumentException("Commands must be annotated with CommandController.");
            }

            SlashCommandData data = Commands.slash(annotation.value(), annotation.description());
            data.setGuildOnly(annotation.guildOnly());
            data.setDefaultPermissions(DefaultMemberPermissions.ENABLED);

            Method[] methods = commandClass.getMethods();
            HashMap<String, Method> subToMethod = new HashMap<>();

            log.trace("Searching methods: ");
            for (Method method : methods) {
                CommandExecutor executor = method.getAnnotation(CommandExecutor.class);
                if (executor == null) {
                    continue;
                }

                String name = executor.value();
                log.trace("Subcommand \"" + name + "\" maps to " + method.getName());
                subToMethod.put(name, method);
            }

            boolean isMain = subToMethod.containsKey("");
            log.trace("isMain: " + isMain);

            if (isMain) {
                Method method = subToMethod.get("");
                List<ParsedParameter> parsedParameters = parseMethod(method);
                List<OptionData> options = parsedToOptions(parsedParameters);

                data.addOptions(options.toArray(new OptionData[0]));
                return data;
            }

            for (Map.Entry<String, Method> entry : subToMethod.entrySet()) {
                String subcommand = entry.getKey();
                Method method = entry.getValue();

                CommandExecutor executor = method.getAnnotation(CommandExecutor.class);

                List<ParsedParameter> parsedParameters = parseMethod(method);
                List<OptionData> options = parsedToOptions(parsedParameters);

                data.addSubcommands(new SubcommandData(subcommand, executor.description())
                        .addOptions(options.toArray(new OptionData[0])));
            }

            return data;
        }).toList().toArray(new CommandData[0]);

        guild.updateCommands().addCommands(cmds)
                .queue();
    }

    @Contract(pure = true)
    @UnmodifiableView
    public static @NotNull Set<Command> getCommands() {
        return Collections.unmodifiableSet(commands);
    }

}