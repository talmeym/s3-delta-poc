package cci;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingUtil {
    public static byte[] md5Hash(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(bytes);
        return DatatypeConverter.printHexBinary(md.digest()).toUpperCase().getBytes();
    }
}
