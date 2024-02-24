package io.atesfactory.evrl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static io.atesfactory.evrl.FileUtil.convertInputStreamtoString;

@SpringBootTest
class PropertiesTests {

    @Value("${io.atesfactory.evrl.example1}")
    private Resource resource;

    @Value("${io.atesfactory.evrl.example2}")
    private Resource classPathResource;

    @Autowired
    private ExampleProperties exampleProperties;

    @Value("${io.atesfactory.evrl.example4}")
    private Resource fileLoadedBase64Content;

    @Test
    void shouldLoadTextFromProperties() throws IOException {
        String content = "myText";

        String text = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));

        Assertions.assertEquals(content, text);
    }

    @Test
    void shouldStillBeAbleToLoadOtherResourcesLikeClasspath() throws IOException {
        String fileContent = "Example2Text";

        String text = new BufferedReader(new InputStreamReader(classPathResource.getInputStream(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));

        Assertions.assertEquals(fileContent, text);
    }

    @Test
    void shouldLoadResourcesIntoConfigurationPropertiesInputStreamBased() throws IOException {
        String content = "inputStreamBasedText";

        String text = new BufferedReader(new InputStreamReader(exampleProperties.getInputStreamBased().getInputStream(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));

        Assertions.assertEquals(content, text);
    }

    @Test
    void shouldLoadResourcesIntoConfigurationPropertiesFileBased() throws IOException {
        String fileContent = "fileBasedText";

        Resource file = exampleProperties.getFileBased();
        try {
            String text = convertInputStreamtoString(file.getInputStream());

            Assertions.assertEquals(fileContent, text);
        } finally {
            file.getFile().delete();
        }
    }

    @Test
    void shouldLoadBase64ContentFromFileAndCreateAnInputStream() throws IOException {
        String content = "HELLO";

        String text = new BufferedReader(new InputStreamReader(fileLoadedBase64Content.getInputStream(), StandardCharsets.UTF_8))
                .lines().collect(Collectors.joining("\n"));

        Assertions.assertEquals(content, text);
    }

}
