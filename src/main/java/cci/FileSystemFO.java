package cci;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static cci.Constants.*;
import static cci.FileOperations.FileStatus.*;

public class FileSystemFO extends AbstractFO {
    @Override
    public boolean fileExists(String name) {
        return getFile(name).exists();
    }

    @Override
    public byte[] readFile(String fileName) throws Exception {
        return new FileInputStream(getFile(fileName)).readAllBytes();
    }

    @Override
    public void writeFile(String fileName, byte[] bytes) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(getFile(fileName));
        fileOutputStream.write(bytes, 0, bytes.length);
        fileOutputStream.close();
    }

    @Override
    public List<String> listFiles(Predicate<String> filter) {
        String[] files = getDirectory().list();

        if(files == null) {
            throw new IllegalArgumentException(String.format("directory %s not found", BUCKET_NAME));
        }

        return Arrays.stream(files).filter(filter).collect(Collectors.toList());
    }

    private File getFile(String fileName) {
        return new File(getDirectory(), fileName);
    }

    private File getDirectory() {
        File bucketDir = new File(new File("s3"), BUCKET_NAME);

        if(!bucketDir.exists()) {
            boolean made = bucketDir.mkdirs();

            if(!made) {
                throw new IllegalStateException("cannot make directory for test data");
            }
        }

        return bucketDir;
    }

    public static void main(String[] args) throws Exception {
        if(args != null && args.length > 1) {
            File file = new File(args[0]);

            if(file.exists()) {
                FileSystemFO fileSystemFO = new FileSystemFO();
                String action = args[1];

                if(READY.name().equals(action)) {
                    fileSystemFO.markAsReady(file.getName());
                    return;
                }

                if(PROCESSED.name().equals(action)) {
                    fileSystemFO.markAsProcessed(file.getName());
                    return;
                }

                throw new IllegalArgumentException("invalid action: only READY or PROCESSED accepted");
            }

            throw new FileNotFoundException(file.getName());
        }

        throw new IllegalArgumentException("need 2 args: filename and action");
    }
}
