package com.github.soramusoka.twitter_likes_to_telegram_bot;

public class AppConfig {
    public String appName;
    public String accessToken;
    public String accessSecret;
    public String consumerKey;
    public String consumerSecret;
    public String[] users;
    public String teleToken;
    public String teleChat;
    public Number requestCounterMax;

    public AppConfig() {
        this.appName = "Default app";
        this.requestCounterMax = 3;
        this.users = new String[0];
    }

    public String toString() {
        String result = "appName: " + this.appName +
                ", accessToken: " + this.accessToken +
                ", accessSecret: " + this.accessSecret +
                ", consumerKey: " + this.consumerKey +
                ", consumerSecret: " + this.consumerSecret +
                ", teleChat: " + this.teleChat +
                ", teleToken: " + this.teleToken +
                ", requestCounterMax: " + this.requestCounterMax;

        result += ", users: ";
        for (String user : this.users) result += " @" + user;
        return result;
    }
}
