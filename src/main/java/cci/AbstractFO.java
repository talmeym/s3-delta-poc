package cci;

import static cci.Constants.FILE_OPS;
import static cci.FileOperations.FileStatus.*;
import static cci.FileOperations.getProcessedFileName;
import static cci.HashingUtil.getHashFileName;
import static cci.HashingUtil.md5Hash;

public abstract class AbstractFO implements FileOperations {

    @Override
    public final void markAsReady(String fileName) throws Exception {
        if(getFileStatus(fileName) != INCOMPLETE) {
            throw new IllegalStateException("File not INCOMPLETE");
        }

        writeFile(getHashFileName(fileName), md5Hash(readFile(fileName)));
    }

    @Override
    public final void markAsProcessed(String fileName) throws Exception {
        if(getFileStatus(fileName) != READY) {
            throw new IllegalStateException("File not READY");
        }

        String hashFileName = getHashFileName(fileName);
        byte[] md5 = readFile(hashFileName);
        deleteFile(hashFileName);
        writeFile(getProcessedFileName(hashFileName), md5);
    }

    @Override
    public final FileStatus getFileStatus(String fileName) throws Exception {
        String hashFileName = getHashFileName(fileName);
        String processedFileName = getProcessedFileName(getHashFileName(fileName));

        if(fileExists(hashFileName) && fileExists(processedFileName)) {
            throw new IllegalStateException(String.format("hash file and processed file exist for %s", fileName));
        }


        if(FILE_OPS.fileExists(hashFileName) || FILE_OPS.fileExists(processedFileName)) {
            String contents = new String(FILE_OPS.readFile(FILE_OPS.fileExists(hashFileName) ? hashFileName : processedFileName));

            try {
                if(new String(md5Hash(readFile(fileName))).equals(contents)) {
                    return FILE_OPS.fileExists(hashFileName) ? READY : PROCESSED;
                }
            } catch (Exception e) {
                // do below
            }

            return INVALID;
        }

        return INCOMPLETE;
    }
}
