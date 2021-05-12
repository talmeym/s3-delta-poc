package cci;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static cci.Constants.LOCATION;

public class AwsS3FO extends AbstractFO {
    private AmazonS3Client s3Client;

    public AwsS3FO(AmazonS3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public boolean fileExists(String name) {
        return s3Client.doesObjectExist(LOCATION, name);
    }

    @Override
    public byte[] readFile(String fileName) throws IOException {
        S3Object s3Object = s3Client.getObject(LOCATION, fileName);
        InputStream inputStream = s3Object.getObjectContent();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;

        while((len = inputStream.read(buffer)) != 0) {
            outputStream.write(buffer, 0, len);
        }

        inputStream.close();
        outputStream.close();

        return outputStream.toByteArray();
    }

    @Override
    public void writeFile(String fileName, byte[] bytes) {
        if(!s3Client.doesBucketExist(LOCATION)) {
            s3Client.createBucket(LOCATION);
        }

        s3Client.putObject(LOCATION, fileName, new String(bytes));
    }

    @Override
    public List<String> listFiles(Predicate<String> filter) {
        List<S3ObjectSummary> objectSummaries = s3Client.listObjects(LOCATION).getObjectSummaries();
        return objectSummaries.stream().map(S3ObjectSummary::getKey).filter(filter).collect(Collectors.toList());
    }

}
