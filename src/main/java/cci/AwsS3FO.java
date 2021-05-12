package cci;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static cci.Constants.BUCKET_NAME;
import static cci.Constants.S3_CLIENT;

public class AwsS3FO extends AbstractFO {
    @Override
    public boolean fileExists(String name) {
        return S3_CLIENT.doesObjectExist(BUCKET_NAME, name);
    }

    @Override
    public byte[] readFile(String fileName) throws IOException {
        S3Object s3Object = S3_CLIENT.getObject(BUCKET_NAME, fileName);
        return readBytes(s3Object.getObjectContent());
    }

    private byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;

        while((len = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
        }

        inputStream.close();
        outputStream.close();

        return outputStream.toByteArray();
    }

    @Override
    public void writeFile(String fileName, byte[] bytes) {
        if(!S3_CLIENT.doesBucketExist(BUCKET_NAME)) {
            S3_CLIENT.createBucket(BUCKET_NAME);
        }

        S3_CLIENT.putObject(BUCKET_NAME, fileName, new String(bytes));
    }

    @Override
    public List<String> listFiles(Predicate<String> filter) {
        List<S3ObjectSummary> objectSummaries = S3_CLIENT.listObjects(BUCKET_NAME).getObjectSummaries();
        return objectSummaries.stream().map(S3ObjectSummary::getKey).filter(filter).collect(Collectors.toList());
    }

}
