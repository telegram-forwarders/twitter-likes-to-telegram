package com.github.soramusoka.twitter_likes_to_telegram_bot;

import org.apache.commons.cli.*;

import java.io.OutputStream;
import java.io.PrintWriter;

public class ConfigManager {

    public static AppConfig loadConfiguration(String[] args) {
        Options options = new Options();

        options.addOption("h", "help", false, "Show usage");
        options.addOption("t", "twitterAccessToken", true, "Twitter access token. Required");
        options.addOption("s", "twitterAccessSecret", true, "Twitter access secret. Required");
        options.addOption("k", "twitterConsumerKey", true, "Twitter consumer key. Required");
        options.addOption("e", "twitterConsumerSecret", true, "Twitter consumer secret. Required");
        options.addOption("o", "telegramToken", true, "Telegram access token. Required");
        options.addOption("a", "telegramChat", true, "Telegram chat id. Positive number. Required");
        options.addOption("u", "user", true, "Twitter user. Could be used to add multiple values. Required");
        options.addOption("r", "requestCounterMax", true, "Twitter request try counter per user. Positive number. Optional");

        try {
            CommandLineParser optionsParser = new ExtendedGnuParser(true);
            CommandLine cmd = optionsParser.parse(options, args);

            AppConfig appConfig = new AppConfig();
            appConfig.twitterAccessSecret = cmd.getOptionValue("twitterAccessSecret");
            appConfig.twitterAccessToken = cmd.getOptionValue("twitterAccessToken");
            appConfig.twitterConsumerKey = cmd.getOptionValue("twitterConsumerKey");
            appConfig.twitterConsumerSecret = cmd.getOptionValue("twitterConsumerSecret");
            appConfig.users = cmd.getOptionValues("user");
            appConfig.telegramChat = cmd.getOptionValue("telegramChat");
            appConfig.telegramToken = cmd.getOptionValue("telegramToken");

            String requestCounterMax = cmd.hasOption("requestCounterMax") ? cmd.getOptionValue("requestCounterMax") : null;
            if (requestCounterMax != null) {
                appConfig.requestCounterMax = Integer.decode(requestCounterMax);
            }
            if (!isApplicable(appConfig)) {
                throw new Exception();
            }
            return appConfig;
        } catch (Exception e) {
            printHelp(options, 80, "Options", "", 3, 5, true, System.out);
            return null;
        }
    }

    private static boolean isApplicable(AppConfig config) {
        return config.twitterAccessSecret != null &&
                config.twitterAccessToken != null &&
                config.twitterConsumerKey != null &&
                config.twitterConsumerSecret != null &&
                config.telegramChat != null &&
                config.telegramToken != null &&
                config.users != null;
    }

    public static void printHelp(final Options options, final int printedRowWidth, final String header, final String footer, final int spacesBeforeOption,
                                 final int spacesBeforeOptionDescription, final boolean displayUsage, final OutputStream out) {
        final String commandLineSyntax = "java rss_to_telegram.jar";
        final PrintWriter writer = new PrintWriter(out);
        final HelpFormatter helpFormatter = new HelpFormatter();

        helpFormatter.printHelp(writer, printedRowWidth, commandLineSyntax, header, options, spacesBeforeOption, spacesBeforeOptionDescription,
                footer, displayUsage);
        writer.flush();
    }
}