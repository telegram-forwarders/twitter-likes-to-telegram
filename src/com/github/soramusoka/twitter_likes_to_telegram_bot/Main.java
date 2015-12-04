package com.github.soramusoka.twitter_likes_to_telegram_bot;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.util.ArrayList;
import java.util.Properties;

public class Main {

    public static void main(String args[]) throws Exception {

        AppConfig config = loadConfiguration(args);
        if (config == null) {
            throw new Exception("App Configuration error: ConfigurationManager");
        }

        Logger logger = getLogger(config.appName);
        if (logger == null) {
            throw new Exception("Logger configuration error");
        }
        logger.info("Startup configuration: " + config.toString());

        HttpRequest request = new HttpRequest();
        TelegramBot bot = new TelegramBot(config.teleToken, config.teleChat, request, logger);

        Twitter twitter = getTwitterInstance(config.accessToken, config.accessSecret, config.consumerKey, config.consumerSecret);
        logger.debug("Twitter instance successfully created");

        String processedFilePath = "processed.data";
        IOManager ioManager = new IOManager(logger, processedFilePath);

        ArrayList<Long> processedStatuses = ioManager.getData();
        logger.debug("Processed statuses size: " + processedStatuses.size());

        for (String user : config.users) {
            logger.debug("Started processing for user: " + user);

            ResponseList<Status> twits = null;
            Integer requestCounter = 0;

            while (twits == null || requestCounter == config.requestCounterMax) {
                try {
                    requestCounter++;
                    twits = twitter.getFavorites(user);
                } catch (TwitterException e) {
                    if (e.getStatusCode() == 429) {
                        Integer secondsUntilReset = e.getRateLimitStatus().getSecondsUntilReset();
                        logger.info("Rate limit reached. Wait " + secondsUntilReset + " seconds till next try");
                        Thread.sleep(secondsUntilReset * 1000);
                    }
                }
            }

            if (twits != null) {
                for (Status status : twits) {
                    Long id = status.getId();
                    if (!processedStatuses.contains(id)) {
                        String screenName = status.getUser().getScreenName();

                        if (!ioManager.isFirstRun) {
                            String message = "https://twitter.com/" + screenName + "/status/" + id;
                            bot.sendMessage("From user " + user + ": " + message);
                            logger.debug("Status " + id + " processed " + message);
                        }

                        processedStatuses.add(id);
                    }
                }
            } else {
                logger.debug("User " + user + " don't have any favorites");
            }
            logger.info("Processing for user " + user + " finished");
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

    public static AppConfig loadConfiguration(String[] args) {
        try {
            Options options = new Options();

            options.addOption("appName", "appName", false, "Application name");
            options.addOption("accessToken", "accessToken", true, "Twitter access token");
            options.addOption("accessSecret", "accessSecret", true, "Twitter access secret");
            options.addOption("consumerKey", "consumerKey", true, "Twitter consumer key");
            options.addOption("consumerSecret", "consumerSecret", true, "Twitter consumer secret");
            options.addOption("user", "user", true, "Twitter user. Could be use add multiple values");
            options.addOption("teleToken", "teleToken", true, "Telegram access token");
            options.addOption("teleChat", "teleChat", true, "Telegram chat id. Positive number");
            options.addOption("requestCounterMax", "requestCounterMax", true, "Twitter request try counter per user. Positive number");

            CommandLineParser optionsParser = new ExtendedGnuParser(true);
            CommandLine cmd = optionsParser.parse(options, args);

            AppConfig appConfig = new AppConfig();
            appConfig.appName = cmd.hasOption("appName") ? cmd.getOptionValue("appName") : appConfig.appName;
            appConfig.accessSecret = cmd.getOptionValue("accessSecret");
            appConfig.accessToken = cmd.getOptionValue("accessToken");
            appConfig.consumerKey = cmd.getOptionValue("consumerKey");
            appConfig.consumerSecret = cmd.getOptionValue("consumerSecret");
            appConfig.users = cmd.getOptionValues("user");
            appConfig.teleChat = cmd.getOptionValue("teleChat");
            appConfig.teleToken = cmd.getOptionValue("teleToken");

            String requestCounterMax = cmd.hasOption("requestCounterMax") ? cmd.getOptionValue("requestCounterMax") : null;
            if (requestCounterMax != null) {
                appConfig.requestCounterMax = Integer.decode(requestCounterMax);
            }

            return appConfig;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }
}
