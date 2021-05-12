package cci;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import static cci.Constants.BROADCAST_COMMON_MODEL_FILE;

public class TestDataMaker {

    private static final String DEFAULT_ADDRESS = "12 Griffin Road, Chelsea, TW13AAF";

    private static Asset[] makeAssets(int numberOfAssets) {
        Asset[] assets = new Asset[numberOfAssets];

        for(int i = 0; i < assets.length; i++) {
            assets[i] = new Asset(UUID.randomUUID().toString(), DEFAULT_ADDRESS, true);
        }

        return assets;
    }

    public static void main(String[] args) throws IOException {
        new ObjectMapper().writeValue(new FileOutputStream(BROADCAST_COMMON_MODEL_FILE), makeAssets(Integer.parseInt(args[0])));
    }
}
