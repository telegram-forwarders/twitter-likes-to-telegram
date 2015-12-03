package com.github.soramusoka.twitter_likes_to_telegram_bot;

import org.apache.commons.cli.*;

public class ConfigurationManager {
    private CommandLine _cmd;

    public ConfigurationManager(CommandLine cmd) {
        this._cmd = cmd;
    }

    public String getValue(String key) {
        return this.getValue(key, null);
    }

    public String getValue(String key, String defaultValue) {
        return this._cmd.hasOption(key) ? this._cmd.getOptionValue(key) : defaultValue;
    }

    public String[] getValues(String key) {
        return this.getValues(key, null);
    }

    public String[] getValues(String key, String[] defaultValue) {
        return this._cmd.hasOption(key) ? this._cmd.getOptionValues(key) : defaultValue;
    }

    private static void registerOption(Options options, String key) {
        options.addOption(key, key, true, key);
    }

    public static ConfigurationManager loadConfiguration(String[] args) {
        try {
            Options options = new Options();

            registerOption(options, "appName");
            registerOption(options, "accessToken");
            registerOption(options, "accessSecret");
            registerOption(options, "consumerKey");
            registerOption(options, "consumerSecret");
            registerOption(options, "user");
            registerOption(options, "teleToken");
            registerOption(options, "teleChat");

            CommandLineParser optionsParser = new ExtendedGnuParser(true);
            CommandLine cmd = optionsParser.parse(options, args);
            return new ConfigurationManager(cmd);
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }
}