package twitter.telegram.forwarder;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;

public class IOManager {
    private Logger _logger;
    private String _fileName;
    public Boolean isFirstRun = false;

    public IOManager(Logger logger, String fileName) {
        this._logger = logger;
        this._fileName = fileName;
    }

    public ArrayList<Long> getData() throws Exception {
        if (!this.isExists()) {
            this.createNewFile();
            this.isFirstRun = true;
        }

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

    public Boolean isExists() throws Exception {
        File newFile = new File(this._fileName);
        return newFile.exists();
    }

    public Boolean createNewFile() throws Exception {
        this._logger.debug("Create new file " + this._fileName);
        return new File(this._fileName).createNewFile();
    }
}
