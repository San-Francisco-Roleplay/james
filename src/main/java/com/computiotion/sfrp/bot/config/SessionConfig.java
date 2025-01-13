package com.computiotion.sfrp.bot.config;

public class SessionConfig {
    private final String channel;
    private final String ping;
    private final String code;
    private final String serverName;


    public SessionConfig(String channel, String ping, String code, String serverName) {
        this.channel = channel;
        this.ping = ping;
        this.code = code;
        this.serverName = serverName;
    }

    public String getChannel() {
        return channel;
    }

    public String getRolePing() {
        return ping;
    }

    public String getCode() {
        return code;
    }

    public String getServerName() {
        return serverName;
    }
}
