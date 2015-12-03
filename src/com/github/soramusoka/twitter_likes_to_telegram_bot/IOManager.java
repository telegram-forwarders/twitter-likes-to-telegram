package com.github.soramusoka.twitter_likes_to_telegram_bot;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;

public class IOManager {
    private Logger _logger;
    private String _fileName;

    public IOManager(Logger logger, String fileName) {
        this._logger = logger;
        this._fileName = fileName;
    }

    public ArrayList<Long> getData() {
        ArrayList<Long> result = new ArrayList<>();
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(this._fileName))) {
                String line;
                while ((line = br.readLine()) != null) {
                    try {
                        result.add(Long.decode(line));
                    } catch (Exception e) {
                        this._logger.error("Long decode error: " + line + e.toString());
                    }
                }
            }
        } catch (Exception e) {
            this._logger.error("Read file error: " + e.toString());
        }
        return result;
    }

    public void saveData(ArrayList<Long> statuses) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this._fileName, false));
            for (Long statusId : statuses) {
                writer.write(statusId.toString());
                writer.newLine();
            }
            writer.close();
            this._logger.debug(statuses.size() + " elements was saved");
        } catch (Exception e) {
            this._logger.error("Write file error: " + e.toString());
        }
    }

    public static IOManager createInstance(Logger logger, String fileName) throws Exception {
        if (!isExists(fileName)) {
            logger.debug("Create new file " + fileName);
            createNewFile(fileName);
        }
        return new IOManager(logger, fileName);
    }

    public static Boolean isExists(String name) throws Exception {
        File newFile = new File(name);
        return newFile.exists();
    }

    public static Boolean createNewFile(String name) throws Exception {
        File newFile = new File(name);
        return newFile.createNewFile();
    }
}
