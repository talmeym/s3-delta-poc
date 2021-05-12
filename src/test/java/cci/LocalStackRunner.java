package cci;

import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.*;

public class LocalStackRunner {
    public static final String AWS_REGION = "eu-west-1";

    private LocalStackContainer localstack;

    @Before
    public void setUp() {
         localstack = new LocalStackContainer().withServices(S3, SQS, SNS).withEnv("DEFAULT_REGION", AWS_REGION);
         localstack.start();
    }

    @Test
    public void doOne() throws InterruptedException {
        while(!localstack.isRunning());

        System.out.println(localstack.getEndpointConfiguration(S3).getServiceEndpoint());

        while(true) {
            Thread.sleep(1000);
        }
    }
}
