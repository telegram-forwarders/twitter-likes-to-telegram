package twitter.telegram.forwarder;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.util.ArrayList;
import java.util.Properties;

public class Main {

    public static void main(String args[]) throws Exception {
        AppConfig config = ConfigManager.loadConfiguration(args);
        if (config == null) return;

        Logger logger = getLogger();
        if (logger == null) {
            throw new Exception("Logger configuration error");
        }
        logger.info("Startup configuration: " + config.toString());

        HttpRequest request = new HttpRequest();
        TelegramBot bot = new TelegramBot(config.telegramToken, config.telegramChat, request, logger);

        Twitter twitter = getTwitterInstance(config.twitterAccessToken, config.twitterAccessSecret, config.twitterConsumerKey, config.twitterConsumerSecret);
        logger.debug("Twitter instance successfully created");

        String processedFilePath = "processed.data";
        IOManager ioManager = new IOManager(logger, processedFilePath);

        ArrayList<Long> processedStatuses = ioManager.getData();
        logger.debug("Processed statuses size: " + processedStatuses.size());

        for (String user : config.users) {
            logger.debug("Started processing for user: " + user);

            ResponseList<Status> twits = null;
            int requestCounter = 0;

            while (twits == null || requestCounter < config.requestCounterMax) {
                try {
                    requestCounter++;
                    twits = twitter.getFavorites(user);
                } catch (TwitterException e) {
                    if (e.getStatusCode() == 429) {
                        int secondsUntilReset = e.getRateLimitStatus().getSecondsUntilReset();
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

    private static Logger getLogger() throws Exception {
        try {
            String appName = "twitter_telegram_forwarder";
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
