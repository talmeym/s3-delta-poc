package cci;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static cci.Constants.*;
import static cci.FileUtil.FileStatus.READY;
import static cci.FileUtil.getFileDirectory;

public class ApiCallSimulator {

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Asset[] assets = callBroadcastAPIAndConvertToCommonFormat();

        File outputFile = new File(getFileDirectory(), BROADCAST_FILE_PREFIX + DATE_FORMATTER.format(new Date()) + JSON_SUFFIX);
        OBJECT_MAPPER.writeValue(outputFile, assets);

        if(makeReady(args)) {
            FILE_UTIL.markAsReady(outputFile);
        }
    }

    /**
     * A method pretending to be the function of calling the broadcast API and converting the returned data into common format.
     * It actually it just loads common format data from a file ;-)
     */
    private static Asset[] callBroadcastAPIAndConvertToCommonFormat() throws IOException {
        return OBJECT_MAPPER.reader().readValue(new File(BROADCAST_COMMON_MODEL_FILE), Asset[].class);
    }

    private static boolean makeReady(String[] args) {
        return args!= null && args.length > 0 && args[0] != null && args[0].equals(READY.name());
    }
}
