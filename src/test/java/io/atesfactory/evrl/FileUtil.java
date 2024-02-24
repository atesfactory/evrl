package io.atesfactory.evrl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class FileUtil {
    private FileUtil(){

    }
    public static String convertInputStreamtoString(InputStream inputStream) {
        String result;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            result = reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
