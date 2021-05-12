package cci;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Constants {
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String MD5_SUFFIX = ".md5";
    public static final String JSON_SUFFIX = ".json";
    public static final String BUCKET_NAME = "test-bucket";
    public static final String PROCESSED_CONTENTS = "PROCESSED";
    public static final String BROADCAST_FILE_PREFIX = "broadcast_";
    public static final String BROADCAST_COMMON_MODEL_FILE = "broadcast_data_in_common_model.json";

    public static final String AWS_REGION = "eu-west-1";
    public static String AWS_ENDPOINT = "http://127.0.0.1.nip.io:55171";
    public static AmazonS3 S3_CLIENT = AmazonS3ClientBuilder.standard().withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(AWS_ENDPOINT, AWS_REGION)).build();;

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final FileOperations FILE_OPS = new FileSystemFO();
    public static final DeltaEngine DELTA_ENGINE = new DeltaEngine();
}
