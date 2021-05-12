package cci;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static cci.Constants.*;
import static cci.FileOperations.FileStatus.READY;

public class ApiCallerWorker {

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);

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

    public static void main(String[] args) throws Exception {
        Asset[] assets = callBroadcastAPIAndConvertToCommonFormat();

        String fileName = BROADCAST_FILE_PREFIX + DATE_FORMATTER.format(new Date()) + JSON_SUFFIX;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OBJECT_MAPPER.writeValue(outputStream, assets);
        FILE_OPS.writeFile(fileName, outputStream.toByteArray());

        if(makeReady(args)) {
            FILE_OPS.markAsReady(fileName);
        }
    }
}
