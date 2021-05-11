package cci;

import cci.FileUtil.FileStatus;
import cci.events.Event;
import lombok.Data;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cci.Constants.*;
import static cci.FileUtil.getFileDirectory;

public class FileProcessor {
    public static void main(String[] args) {
        String[] jsonFilenames = getFileDirectory().list((file, name) -> name.endsWith(JSON_SUFFIX));

        if(jsonFilenames != null && jsonFilenames.length > 0) {
            Map<Date, String> fileNamesByDate = Arrays.stream(jsonFilenames).collect(Collectors.toMap(FileProcessor::getDateFrom, filename -> filename));
            System.out.println(jsonFilenames.length + " json files found");

            AtomicAssetStore previousFileData = new AtomicAssetStore();

            fileNamesByDate.keySet().stream().sorted().forEach(date -> {
                String fileName = fileNamesByDate.get(date);
                File file = new File(getFileDirectory(), fileName);

                try {
                    FileStatus fileStatus = FILE_UTIL.getFileStatus(file);

                    switch(fileStatus) {
                        case INCOMPLETE:
                            System.out.println("Stopping processing at incomplete file: " + fileName);
                            System.exit(0);
                        case READY:
                            System.out.print("Processing file " + fileName + " .. ");
                            Asset[] fileData = readAssetData(file);
                            List<Event> events = DELTA_ENGINE.determineDelta(fileData, previousFileData.getData());
                            System.out.println(events.size() + " events: " + events);
                            FILE_UTIL.markAsProcessed(file);
                            previousFileData.setData(fileData);
                            break;
                        case PROCESSED:
                            System.out.println("Skipping processed file " + fileName);
                            previousFileData.setData(readAssetData(file));
                            break;
                        case INVALID:
                            System.out.println("Stopping processing at invalid file: " + fileName);
                            System.exit(0);
                    }
                } catch (Exception e) {
                    System.err.println("ERROR processing file: " + e.getMessage());
                    System.exit(1);
                }
            });
        } else {
            System.out.println("No files to process");
        }
    }

    private static Asset[] readAssetData(File file) throws java.io.IOException {
        return OBJECT_MAPPER.reader().readValue(new FileInputStream(file), Asset[].class);
    }

    private static Date getDateFrom(String fileName) {
        try {
            String dateString = fileName.substring(fileName.indexOf('_') + 1, fileName.indexOf('.'));
            return new SimpleDateFormat(Constants.DATE_FORMAT).parse(dateString);
        } catch (ParseException e) {
            throw new IllegalArgumentException("File has invalid filename format: " + fileName);
        }
    }

    @Data
    public static class AtomicAssetStore {
        private Asset[] data;
    }
}
