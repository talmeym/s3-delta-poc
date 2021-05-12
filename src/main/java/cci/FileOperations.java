package cci;

import java.util.List;
import java.util.function.Predicate;

public interface FileOperations {
    boolean fileExists(String name);
    byte[] readFile(String fileName) throws Exception;
    void writeFile(String fileName, byte[] bytes) throws Exception;
    void deleteFile(String fileName) throws Exception;
    List<String> listFiles(Predicate<String> filter) throws Exception;
    FileSystemFO.FileStatus getFileStatus(String fileName) throws Exception;
    void markAsReady(String fileName) throws Exception;
    void markAsProcessed(String fileName) throws Exception;

    enum FileStatus {
        INCOMPLETE,
        READY,
        INVALID,
        PROCESSED
    }

    static String getProcessedFileName(String fileName) {
        return fileName + Constants.PROCESSED_SUFFIX;
    }
}
