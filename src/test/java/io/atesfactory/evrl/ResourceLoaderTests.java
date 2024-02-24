package io.atesfactory.evrl;

import io.atesfactory.evrl.loader.LoaderRegistry;
import io.atesfactory.evrl.resourcefactory.ResourceFactoryException;
import io.atesfactory.evrl.transformer.Transformer;
import io.atesfactory.evrl.transformer.TransformerConfig;
import io.atesfactory.evrl.transformer.TransformerContext;
import io.atesfactory.evrl.transformer.TransformerRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;

import static io.atesfactory.evrl.FileUtil.convertInputStreamtoString;

@SpringBootTest
class ResourceLoaderTests {

    @Autowired
    private ResourceLoader resourceLoader;

    @MockBean
    private ExampleProperties exampleProperties;

    @BeforeEach
    void reset() {
        TransformerRegistry.reset();
        LoaderRegistry.reset();
    }

    @Test
    void shouldLoadEnvInputStream() throws Exception {
        String content = "HELLO";

        Resource resource = resourceLoader.getResource("evrl://text/content:" + content);
        String text = convertInputStreamtoString(resource.getInputStream());

        Assertions.assertEquals(content, text);
    }

    @Test
    void shouldLoadEnvIntoFile() throws Exception {
        String content = "HELLO";
        String filePath = "./tmp-file";

        Resource resource = resourceLoader.getResource("evrl://text|file:" + filePath + "/content:" + content);
        File file = resource.getFile();
        try {
            String text = new String(java.nio.file.Files.readAllBytes(file.toPath()));

            Assertions.assertEquals(content, text);
        } finally {
            file.delete();
        }
    }

    @Test
    void shouldLoadBase64EncodedValueIntoDecodedInputStream() throws Exception {
        String content = "SEVMTE8=";
        String decodedValue = "HELLO";

        Resource resource = resourceLoader.getResource("evrl://base64/content:" + content);
        String text = convertInputStreamtoString(resource.getInputStream());

        Assertions.assertEquals(decodedValue, text);
    }

    @Test
    void shouldLoadBase64EncodedValueIntoFileWithDecodedContent() throws Exception {
        String content = "SEVMTE8=";
        String decodedValue = "HELLO";
        String filePath = "./test/tmp-file-base64";

        Resource resource = resourceLoader.getResource("evrl://base64|file:" + filePath + "/content:" + content);
        File file = resource.getFile();
        try {
            Assertions.assertTrue(file.getAbsolutePath().contains(filePath));

            String text = new String(java.nio.file.Files.readAllBytes(file.toPath()));

            Assertions.assertEquals(decodedValue, text);
        } finally {
            file.delete();
        }
    }

    @Test
    void shouldFailToLoadResourceDueToTypoInProtocol() {
        String content = "HELLO";
        String location = "evrl://text/content:" + content;
        Assertions.assertThrows(FileNotFoundException.class, () -> {
            Resource resource = resourceLoader.getResource("U" + location);
            convertInputStreamtoString(resource.getInputStream());
        });
    }

    @Test
    void shouldLoadPropertyAtRuntime() throws Exception {
        String content = "io.atesfactory.evrl.example5";
        String value = "HELLO";

        Resource resource = resourceLoader.getResource("evrl://text/prop:" + content);
        String text = convertInputStreamtoString(resource.getInputStream());

        Assertions.assertEquals(value, text);
    }

    @Test
    void shouldLoadPropertyAtRuntimeWithEnvPrefix() throws Exception {
        String content = "io.atesfactory.evrl.example5";
        String value = "HELLO";

        Resource resource = resourceLoader.getResource("evrl://text/env:" + content);
        String text = convertInputStreamtoString(resource.getInputStream());

        Assertions.assertEquals(value, text);
    }

    @Test
    void shouldThrowExceptionOnIncompatibleDestinationType() {
        String content = "io.atesfactory.evrl.example5";

        TransformerRegistry.register("not-compatible-destination-type", Collections.singletonList(new Transformer<String, LocalDateTime>() {
            @Override
            public LocalDateTime transform(TransformerContext transformerContext, TransformerConfig transformerConfig, Serializable input) {
                return LocalDateTime.now();
            }

            @Override
            public Class<String> getInType() {
                return String.class;
            }

            @Override
            public Class<LocalDateTime> getOutType() {
                return LocalDateTime.class;
            }
        }));

        Assertions.assertThrows(ResourceFactoryException.class, () -> resourceLoader.getResource("evrl://not-compatible-destination-type/env:" + content));
    }

    
}
