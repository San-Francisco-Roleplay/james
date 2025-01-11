package com.computiotion.sfrp.bot.config;

import com.computiotion.sfrp.bot.BotApplication;
import com.computiotion.sfrp.bot.commands.PermissionLevel;
import com.computiotion.sfrp.bot.config.erlc.CommandLogType;
import com.computiotion.sfrp.bot.config.erlc.ERLCConfig;
import com.computiotion.sfrp.bot.config.erlc.Message;
import com.computiotion.sfrp.bot.config.erlc.MessageType;
import com.computiotion.sfrp.bot.time.TimeParser;
import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.computiotion.sfrp.bot.XMLUtils.*;

public final class ConfigReader {
    public static final String CONFIG_FILE_ENV_ENTRY = "SFRP_CONFIG";
    public static final String CONFIG_JSON_ENV_ENTRY = "SFRP_CONFIG_JSON";
    private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private static final Log log = LogFactory.getLog(ConfigReader.class);
    private static Config appDefaults;

    @Contract(pure = true)
    private static @NotNull PermissionLevelParam fromNode(@NotNull Node node) {
        Element element = asElement(node);
        Set<String> allow = getChildNodesByTagName(element, "allow").stream()
                .map(item -> asText(item).getWholeText())
                .collect(Collectors.toSet());
        Set<String> deny = getChildNodesByTagName(element, "deny").stream()
                .map(item -> asText(item).getWholeText())
                .collect(Collectors.toSet());

        return new PermissionLevelParam(allow, deny);
    }

    public static @NotNull Config fromApplicationDefaults() throws ParserConfigurationException, IOException, SAXException {
        if (appDefaults != null) return appDefaults;

        String fileLoc = System.getenv(CONFIG_FILE_ENV_ENTRY);
        String json = System.getenv(CONFIG_JSON_ENV_ENTRY);
        if (fileLoc == null && json != null) {
            appDefaults = fromStream(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));

            return appDefaults;
        }

        if (fileLoc == null) {
            InputStream stream = BotApplication.class.getResourceAsStream("/config.xml");
            return fromStream(stream);
        }

        File file = new File(fileLoc);
        appDefaults = fromFile(fileLoc, file);

        return appDefaults;
    }

    public static @NotNull Config fromFile(String fileLoc, @NotNull File file) throws ParserConfigurationException, IOException, SAXException {
        boolean exists = file.exists();

        if (!exists)
            throw new RuntimeException(new FileNotFoundException(fileLoc + " (from your " + CONFIG_FILE_ENV_ENTRY + " env entry) was not found."));
        if (!file.canRead())
            throw new RuntimeException(new IllegalAccessError(fileLoc + " (from your " + CONFIG_FILE_ENV_ENTRY + " env entry) cannot be read by the current process."));

        return fromStream(new FileInputStream(file));
    }

    // https://mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
    @Contract("_ -> new")
    public static @NotNull Config fromStream(InputStream stream) throws ParserConfigurationException, IOException, SAXException {
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        // parse XML file
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(stream);

        // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        Element root = doc.getDocumentElement();
        root.normalize();

        Node commands = getChildNodeByTagName(root, "Commands");
        Element commandLevels = asElement(getChildNodeByTagName(commands, "Levels"));

        HashMap<PermissionLevel, Element> levels = new HashMap<>();
        for (PermissionLevel level : PermissionLevel.values()) {
            Element element = asElement(getChildNodeByTagName(commandLevels, level.name()));
            levels.put(level, element);
        }

        HashMap<PermissionLevel, PermissionLevelData> data = new HashMap<>();
        for (Map.Entry<PermissionLevel, Element> entry : levels.entrySet()) {
            PermissionLevel level = entry.getKey();
            Element elem = entry.getValue();

            String baseStr = elem.getAttributeNode("base").getValue();
            if (baseStr.isEmpty()) {
                log.warn("Command level " + level.name() + " does not have a \"base\" attribute, defaulting to \"allow\"");
                baseStr = "allow";
            }

            PermissionLevelDefault base;

            if (baseStr.equalsIgnoreCase("allow")) {
                base = PermissionLevelDefault.ALLOW;
            } else if (baseStr.equalsIgnoreCase("deny")) {
                base = PermissionLevelDefault.DENY;
            } else {
                throw new SchemaViolationError("A \"base\" attribute must be one of: \"allow\", \"deny\"");
            }

            PermissionLevelDataBuilder builder = new PermissionLevelDataBuilder()
                    .setBase(base);

            Node guildNode = getChildNodeByTagNameUnsafe(elem, "guilds");
            Node channelNode = getChildNodeByTagNameUnsafe(elem, "channels");
            Node roleNode = getChildNodeByTagNameUnsafe(elem, "roles");
            Node userNode = getChildNodeByTagNameUnsafe(elem, "users");

            Node silentNode = getChildNodeByTagNameUnsafe(elem, "silent");
            boolean silent = false;

            if (silentNode != null) {
                Text text = asText(silentNode);
                String silentStr = text.getWholeText();

                switch (silentStr) {
                    case "true" -> silent = true;
                    case "false" -> {}
                    case null, default -> {
                        log.warn("Command level " + level.name() + " does not have a \"silent\" attribute, defaulting to \"false\"");
                    }
                }
            }


            builder.setGuilds(guildNode == null ? PermissionLevelParam.fromEmpty() : fromNode(guildNode))
                    .setChannels(channelNode == null ? PermissionLevelParam.fromEmpty() : fromNode(channelNode))
                    .setRoles(roleNode == null ? PermissionLevelParam.fromEmpty() : fromNode(roleNode))
                    .setUser(userNode == null ? PermissionLevelParam.fromEmpty() : fromNode(userNode))
                    .setSilent(silent);

            data.put(level, builder.build());
        }

        Element commandPrefixes = asElement(getChildNodeByTagName(commands, "MessagePrefixes"));
        Set<String> prefixes = getChildNodesByTagName(commandPrefixes, "prefix").stream()
                .map(prefix -> asText(prefix).getWholeText())
                .collect(Collectors.toSet());
        boolean includesMention = getChildNodeByTagNameUnsafe(commandPrefixes, "mention") != null;

        Node erlc = getChildNodeByTagName(root, "ERLC");
        List<Message> messages = new ArrayList<>();

        Node nMessages = getChildNodeByTagName(erlc, "messages");
        Node nInterval = getChildNodeByTagName(nMessages, "interval");

        String intervalText = nInterval.getTextContent();
        long interval = TimeParser.parseTime(intervalText);

        NodeList nodes = nMessages.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node item = nodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) continue;

            String tag = item.getNodeName();
            if (tag.equals("interval")) continue;

            MessageType type = switch (tag) {
                case "h", "hint" -> MessageType.Hint;
                case "m", "message" -> MessageType.Message;
                default -> throw new IllegalStateException("Unexpected value: " + tag);
            };

            String text = item.getTextContent();

            messages.add(new Message(text, type));
        }

        Node nCommandLogs = getChildNodeByTagName(erlc, "commands");
        Node nSender = getChildNodeByTagName(nCommandLogs, "sender");
        Node nChannels = getChildNodeByTagName(nCommandLogs, "channels");
        HashMap<CommandLogType, String> channels = new HashMap<>();

        String sender = nSender.getTextContent();
        for (CommandLogType type : CommandLogType.values()) {
            String tag = type.getTag();
            Node node = getChildNodeByTagNameUnsafe(asElement(nChannels), tag);

            if (node == null) continue;

            String id = node.getTextContent();
            channels.put(type, id);
        }

        Node nAllowed = getChildNodeByTagName(nCommandLogs, "always-allowed-commands");
        Node nOffDuty = getChildNodeByTagName(nCommandLogs, "off-duty-exempt");
        Set<String> alwaysAllowed = new HashSet<>();
        Set<String> offDutyAllowed = new HashSet<>();
        List<Node> nCommands = getChildNodesByTagName(asElement(nAllowed), "command");
        List<Node> nOffDutyRoles = getChildNodesByTagName(asElement(nOffDuty), "role");

        for (Node node : nCommands) {
            String cmd = node.getTextContent();
            if (cmd.startsWith(":")) {
                cmd = cmd.substring(1);
                log.warn("Always-Allowed command " + cmd + " is prefixed with a : in the configuration file. To get rid of this warning, remove the \":\".");
            }

            alwaysAllowed.add(cmd);
        }

        for (Node node : nOffDutyRoles) {
            String role = node.getTextContent();
            offDutyAllowed.add(role);
        }


        stream.close();
        return new Config(new CommandConfig(prefixes, includesMention, data),
                new ERLCConfig(sender, messages, interval, channels, alwaysAllowed, offDutyAllowed));
    }
}
