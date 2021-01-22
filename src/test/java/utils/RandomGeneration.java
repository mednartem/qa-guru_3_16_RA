package utils;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class RandomGeneration {

    public static String generateString(Integer count) {
        return randomAlphabetic(count);
    }
}
