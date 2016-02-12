package com.github.soramusoka.twitter_likes_to_telegram_bot;

public class AppConfig {
    public String appName;
    public String twitterAccessToken;
    public String twitterAccessSecret;
    public String twitterConsumerKey;
    public String twitterConsumerSecret;
    public String telegramToken;
    public String telegramChat;
    public int requestCounterMax;
    public String[] users;

    public AppConfig() {
        this.appName = "Default app";
        this.requestCounterMax = 3;
        this.users = new String[0];
    }

    public String toString() {
        String result = "appName: " + this.appName +
                ", twitterAccessToken: " + this.twitterAccessToken +
                ", twitterAccessSecret: " + this.twitterAccessSecret +
                ", twitterConsumerKey: " + this.twitterConsumerKey +
                ", twitterConsumerSecret: " + this.twitterConsumerSecret +
                ", telegramChat: " + this.telegramChat +
                ", telegramToken: " + this.telegramToken +
                ", requestCounterMax: " + this.requestCounterMax;

        result += ", users: ";
        for (String user : this.users) result += " @" + user;
        return result;
    }
}
