package cci;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static cci.Constants.*;
import static cci.FileUtil.FileStatus.*;

public class FileUtil {
    public void markAsReady(File file) throws IOException, NoSuchAlgorithmException {
        if(getFileStatus(file) != INCOMPLETE) {
            throw new IllegalStateException("File not INCOMPLETE");
        }

        String md5Hash = md5Hash(file);
        FileOutputStream output = new FileOutputStream(getHashFile(file));
        output.write(md5Hash.getBytes(), 0, md5Hash.length());
        output.close();
    }

    public void markAsProcessed(File file) throws IOException, NoSuchAlgorithmException {
        if(getFileStatus(file) != READY) {
            throw new IllegalStateException("File not READY");
        }

        FileOutputStream output = new FileOutputStream(getHashFile(file));
        output.write(PROCESSED_CONTENTS.getBytes(), 0, PROCESSED_CONTENTS.length());
        output.close();
    }

    public FileStatus getFileStatus(File file) throws NoSuchAlgorithmException, IOException {
        File hashFile = getHashFile(file);

        if(hashFile.exists()) {
            FileInputStream input = new FileInputStream(hashFile);
            String contents = new String(input.readAllBytes());

            if(PROCESSED_CONTENTS.equals(contents)) {
                return PROCESSED;
            }

            try {
                if(md5Hash(file).equals(contents)) {
                    return READY;
                }
            } catch(IOException ioe) {}

            return INVALID;
        }

        return INCOMPLETE;
    }

    private File getHashFile(File file) {
        return new File(file.getParentFile(), file.getName() + MD5_SUFFIX);
    }

    private static String md5Hash(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest).toUpperCase();
    }

    public static File getFileDirectory() {
        File fileDirectory  = new File(FILE_DIR);

        if(!fileDirectory.exists()) {
            if(!fileDirectory.mkdir()) {
                throw new IllegalStateException("Cannot create output directory");
            }
        }

        return fileDirectory;
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        if(args != null && args.length > 1) {
            File file = new File(args[0]);

            if(file.exists()) {
                FileUtil fileUtil = new FileUtil();
                String action = args[1];

                if(READY.name().equals(action)) {
                    fileUtil.markAsReady(file);
                    return;
                }

                if(PROCESSED.name().equals(action)) {
                    fileUtil.markAsProcessed(file);
                    return;
                }

                throw new IllegalArgumentException("Invalid action: only READY or PROCESSED accepted");
            }

            throw new FileNotFoundException(file.getName());
        }

        throw new IllegalArgumentException("Need 2 args: filename and action");
    }

    public enum FileStatus {
        INCOMPLETE,
        READY,
        INVALID,
        PROCESSED
    }
}
