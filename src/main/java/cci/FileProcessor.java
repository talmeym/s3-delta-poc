package cci;

import cci.FileOperations.FileStatus;
import cci.events.Event;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cci.Constants.*;

public class FileProcessor {
    public static void main(String[] args) throws Exception {
        List<String> jsonFiles = FILE_OPS.listFiles((name) -> name.endsWith(JSON_SUFFIX));

        if(jsonFiles != null && jsonFiles.size() > 0) {
            Map<Date, String> fileNamesByDate = jsonFiles.stream().collect(Collectors.toMap(FileProcessor::getDateFrom, filename -> filename));
            System.out.println(jsonFiles.size() + " json files found");

            AtomicAssetStore previousFileData = new AtomicAssetStore();

            fileNamesByDate.keySet().stream().sorted().forEach(date -> {
                String fileName = fileNamesByDate.get(date);

                try {
                    FileStatus fileStatus = FILE_OPS.getFileStatus(fileName);

                    switch(fileStatus) {
                        case INCOMPLETE:
                            System.out.println("Stopping processing at incomplete file: " + fileName);
                            System.exit(0);
                        case READY:
                            System.out.print("Processing file " + fileName + " .. ");
                            Asset[] fileData = readAssetData(fileName);
                            List<Event> events = DELTA_ENGINE.determineDelta(fileData, previousFileData.getData());
                            System.out.println(events.size() + " events: " + events);
                            FILE_OPS.markAsProcessed(fileName);
                            previousFileData.setData(fileData);
                            break;
                        case PROCESSED:
                            System.out.println("Skipping processed file " + fileName);
                            previousFileData.setData(readAssetData(fileName));
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

    private static Asset[] readAssetData(String name) throws Exception {
        return OBJECT_MAPPER.reader().readValue(FILE_OPS.readFile(name), Asset[].class);
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
