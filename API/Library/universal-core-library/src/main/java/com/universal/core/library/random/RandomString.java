package com.universal.core.library.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomString {
    public static String generateRandomString(StringType type, int numberOfCharacter) {
        List<Integer> characterList = new ArrayList();
        if (type == StringType.ALPHANUMERIC || type == StringType.ALPHABETIC) {
            for (int i = 65; i <= 90; i++) {
                characterList.add(i);
            }
        }
        if (type == StringType.ALPHANUMERIC || type == StringType.NUMERIC) {
            for (int i = 48; i <= 57; i++) {
                characterList.add(i);
            }
        }
        int leftLimit = 0; // letter 'a'
        int rightLimit = characterList.size() - 1; // letter 'z'
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(numberOfCharacter);
        for (int i = 0; i < numberOfCharacter; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) (int) characterList.get(randomLimitedInt));
        }
        return buffer.toString();

    }
}
