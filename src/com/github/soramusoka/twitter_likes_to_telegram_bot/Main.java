package com.github.soramusoka.twitter_likes_to_telegram_bot;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.util.ArrayList;
import java.util.Properties;

public class Main {

    public static void main(String args[]) throws Exception {

        ConfigurationManager configManager = ConfigurationManager.loadConfiguration(args);
        if (configManager == null) {
            throw new Exception("App Configuration error: ConfigurationManager");
        }

        String APP_NAME = configManager.getValue("appName", "defaultApp");
        String CONSUMER_KEY = configManager.getValue("consumerKey");
        String CONSUMER_SECRET = configManager.getValue("consumerSecret");
        String ACCESS_TOKEN = configManager.getValue("accessToken");
        String ACCESS_SECRET = configManager.getValue("accessSecret");
        String TELEGRAM_TOKEN = configManager.getValue("teleToken");
        String TELEGRAM_CHAT_ID = configManager.getValue("teleChat");
        String[] TWITTER_USERS = configManager.getValues("user");

        if (TELEGRAM_CHAT_ID == null) throw new Exception("App configuration error: TELEGRAM_CHAT_ID is required");
        if (TELEGRAM_TOKEN == null) throw new Exception("App configuration error: TELEGRAM_TOKEN is required");
        if (CONSUMER_KEY == null) throw new Exception("App configuration error: CONSUMER_KEY is required");
        if (CONSUMER_SECRET == null) throw new Exception("App configuration error: CONSUMER_SECRET is required");
        if (ACCESS_TOKEN == null) throw new Exception("App configuration error: ACCESS_TOKEN is required");
        if (ACCESS_SECRET == null) throw new Exception("App configuration error: ACCESS_SECRET is required");
        if (TWITTER_USERS == null || TWITTER_USERS.length == 0)
            throw new Exception("App configuration error: TWITTER_USERNAMES is required");

        Logger logger = getLogger(APP_NAME);
        if (logger == null) {
            throw new Exception("Logger configuration error");
        }

        String users = "";
        for (String aTWITTER_USERNAME : TWITTER_USERS) users += " @" + aTWITTER_USERNAME;

        logger.info("Configuration: " +
                "APP_NAME: " + APP_NAME +
                ", TELEGRAM_TOKEN: " + TELEGRAM_TOKEN +
                ", TELEGRAM_CHAT_ID: " + TELEGRAM_CHAT_ID +
                ", CONSUMER_KEY: " + CONSUMER_KEY +
                ", CONSUMER_SECRET: " + CONSUMER_SECRET +
                ", ACCESS_TOKEN: " + ACCESS_TOKEN +
                ", ACCESS_SECRET: " + ACCESS_SECRET +
                ", TWITTER_USERNAMES: " + users);

        HttpRequest request = new HttpRequest();
        TelegramBot bot = new TelegramBot(TELEGRAM_TOKEN, TELEGRAM_CHAT_ID, request, logger);

        Twitter twitter = getTwitterInstance(ACCESS_TOKEN, ACCESS_SECRET, CONSUMER_KEY, CONSUMER_SECRET);
        logger.debug("Twitter instance successfully created");

        String processedFilePath = "processed.data";
        IOManager ioManager = IOManager.createInstance(logger, processedFilePath);

        ArrayList<Long> processedStatuses = ioManager.getData();
        logger.debug("Processed statuses size: " + processedStatuses.size());

        for (String user : TWITTER_USERS) {
            logger.debug("Started processing for user: " + user);

            ResponseList<Status> twits = twitter.getFavorites(user);
            if (twits != null) {
                for (Status status : twits) {
                    Long id = status.getId();
                    if (!processedStatuses.contains(id)) {
                        String screenName = status.getUser().getScreenName();
                        String message = "https://twitter.com/" + screenName + "/status/" + id;
                        // bot.sendMessage(message);
                        logger.debug("Status " + id + " processed " + message);
                        processedStatuses.add(id);
                    }
                }
            } else {
                logger.debug("User " + user + " don't have any favorites");
            }
        }
        ioManager.saveData(processedStatuses);
    }

    private static Twitter getTwitterInstance(String accessToken, String accessSecret, String consumerKey, String consumerSecret) {
        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        twitter.setOAuthAccessToken(new AccessToken(accessToken, accessSecret));
        return twitter;
    }

    private static Logger getLogger(String appName) throws Exception {
        try {
            Properties log4jProperties = new Properties();
            log4jProperties.setProperty("log4j.logger." + appName, "DEBUG, myConsoleAppender");
            log4jProperties.setProperty("log4j.appender.myConsoleAppender", "org.apache.log4j.ConsoleAppender");
            log4jProperties.setProperty("log4j.appender.myConsoleAppender.layout", "org.apache.log4j.PatternLayout");
            log4jProperties.setProperty("log4j.appender.myConsoleAppender.layout.ConversionPattern", "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
            PropertyConfigurator.configure(log4jProperties);
            return Logger.getLogger(appName);
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return null;
        }
    }
}
