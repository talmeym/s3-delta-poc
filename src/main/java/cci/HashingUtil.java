package cci;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static cci.Constants.MD5_SUFFIX;

public class HashingUtil {

    public static String getHashFileName(String fileName) {
        return fileName + MD5_SUFFIX;
    }

    public static byte[] md5Hash(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(bytes);
        return DatatypeConverter.printHexBinary(md.digest()).toUpperCase().getBytes();
    }
}
