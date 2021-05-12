package cci;

import static cci.Constants.FILE_OPS;
import static cci.Constants.PROCESSED_CONTENTS;
import static cci.FileOperations.FileStatus.*;
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

        writeFile(getHashFileName(fileName), PROCESSED_CONTENTS.getBytes());
    }

    @Override
    public final FileStatus getFileStatus(String fileName) throws Exception {
        String hashFileName = getHashFileName(fileName);

        if(FILE_OPS.fileExists(hashFileName)) {
            String contents = new String(FILE_OPS.readFile(hashFileName));

            if(PROCESSED_CONTENTS.equals(contents)) {
                return PROCESSED;
            }

            try {
                if(new String(md5Hash(readFile(fileName))).equals(contents)) {
                    return READY;
                }
            } catch (Exception e) {
                // do below
            }

            return INVALID;
        }

        return INCOMPLETE;
    }
}
