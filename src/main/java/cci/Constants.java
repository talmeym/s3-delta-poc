package cci;

//import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Constants {
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String MD5_SUFFIX = ".md5";
    public static final String JSON_SUFFIX = ".json";
    public static final String LOCATION = "test_data";
    public static final String PROCESSED_CONTENTS = "PROCESSED";
    public static final String BROADCAST_FILE_PREFIX = "broadcast_";
    public static final String BROADCAST_COMMON_MODEL_FILE = "broadcast_data_in_common_model.json";

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final FileOperations FILE_OPS = new FileSystemFO();
    public static final DeltaEngine DELTA_ENGINE = new DeltaEngine();

//    private static AmazonS3Client getS3Client() {
//        return null;
//    }
}
