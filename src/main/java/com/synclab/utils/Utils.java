package com.synclab.utils;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {

    public static String storePath = "src/main/resources/database/embedding.json";

    public static Path toPath(String fileName) {
        URL fileUrl = Utils.class.getClassLoader().getResource(fileName);
        if (fileUrl == null) {
            throw new RuntimeException("File " + fileName + " not found");
        }
        try {
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
