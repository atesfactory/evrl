package io.atesfactory.evrl;

import io.atesfactory.evrl.configuration.EvrlSpringContext;
import io.atesfactory.evrl.transformer.Transformer;
import io.atesfactory.evrl.transformer.TransformerConfig;
import io.atesfactory.evrl.transformer.TransformerContext;
import io.atesfactory.evrl.transformer.TransformerException;
import io.atesfactory.evrl.transformer.TransformerRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransformerTests {

    private final EvrlSpringContext evrlSpringContext = new EvrlSpringContext(
            Mockito.mock(ApplicationContext.class),
            Mockito.mock(Environment.class)
    );

    @Test
    void shouldGetBytesFromString() {
        String content = "HELLO";
        String transformerName = "text";
        Transformer<?, ?> transformer = TransformerRegistry.get(transformerName, String.class);
        TransformerConfig transformerConfig = new TransformerConfig(transformerName);
        byte[] transformed = (byte[]) transformer.transform(
                new TransformerContext(evrlSpringContext),
                transformerConfig, content);

        assertEquals(content, new String(transformed, java.nio.charset.StandardCharsets.UTF_8));
    }

    @Test
    void shouldGetFileFromBytes() throws Exception {
        String content = "Example2Text\n";
        String transformerName = "file";
        Transformer<?, ?> transformer = TransformerRegistry.get(transformerName, byte[].class);
        TransformerConfig transformerConfig = new TransformerConfig(
                transformerName,
                Collections.singletonList("./test-example.txt")
        );
        File transformed = (File) transformer.transform(
                new TransformerContext(evrlSpringContext),
                transformerConfig, content.getBytes(StandardCharsets.UTF_8));
        try {
            assertEquals(content, new String(java.nio.file.Files.readAllBytes(transformed.toPath()), java.nio.charset.StandardCharsets.UTF_8));
        } finally {
            transformed.delete();
        }
    }

    @Test
    void shouldGetTempFileFromBytes() throws Exception{
        String content = "Example2Text\n";
        String transformerName = "file";
        Transformer<?, ?> transformer = TransformerRegistry.get(transformerName, byte[].class);
        TransformerConfig transformerConfig = new TransformerConfig(transformerName, Collections.emptyList());
        File transformed = (File) transformer.transform(
                new TransformerContext(evrlSpringContext),
                transformerConfig, content.getBytes(StandardCharsets.UTF_8));
        try {
            assertEquals(content, new String(java.nio.file.Files.readAllBytes(transformed.toPath()), java.nio.charset.StandardCharsets.UTF_8));
        } finally {
            transformed.delete();
        }
    }

    @Test
    void shouldGetFileWithSubdirectoriesFromBytes() throws Exception{
        String content = "Example2Text\n";
        String transformerName = "file";
        Transformer<?, ?> transformer = TransformerRegistry.get(transformerName, byte[].class);
        TransformerConfig transformerConfig = new TransformerConfig(
                transformerName,
                Collections.singletonList("./test/test-example.txt")
        );
        File transformed = (File) transformer.transform(
                new TransformerContext(evrlSpringContext),
                transformerConfig, content.getBytes(StandardCharsets.UTF_8));
        try {
            assertEquals(content, new String(java.nio.file.Files.readAllBytes(transformed.toPath()), java.nio.charset.StandardCharsets.UTF_8));
        } finally {
            transformed.delete();
            File subdirectory = new File("./test");
            if(subdirectory.exists()) {
                subdirectory.delete();
            }
        }
    }

    @Test
    void shouldDecodeBase64() {
        String content = "SEVMTE8=";
        String transformerName = "base64";
        Transformer<?, ?> transformer = TransformerRegistry.get(transformerName, String.class);
        TransformerConfig transformerConfig = new TransformerConfig(transformerName);
        byte[] transformed = (byte[]) transformer.transform(
                new TransformerContext(evrlSpringContext),
                transformerConfig, content);

        assertEquals("HELLO", new String(transformed, java.nio.charset.StandardCharsets.UTF_8));
    }

    @Test
    void shouldDecodeBase64Url() {
        String content = "SEVMTE8";
        String transformerName = "base64";
        Transformer<?, ?> transformer = TransformerRegistry.get(transformerName, String.class);
        TransformerConfig transformerConfig = new TransformerConfig(transformerName, Collections.singletonList("url"));
        byte[] transformed = (byte[]) transformer.transform(
                new TransformerContext(evrlSpringContext),
                transformerConfig, content);

        assertEquals("HELLO", new String(transformed, java.nio.charset.StandardCharsets.UTF_8));
    }

    @Test
    void shouldThrowTransformerExceptionForUnknownTransformer() {
        String transformerName = "unknown";

        Assertions.assertThrows(TransformerException.class, () -> TransformerRegistry.get(transformerName, String.class));
    }

    @Test
    void shouldThrowTransformerExceptionForInvalidFilePath() {
        String transformerName = "file";
        Transformer<?, ?> transformer = TransformerRegistry.get(transformerName, byte[].class);


        TransformerConfig transformerConfig = new TransformerConfig(transformerName, Collections.singletonList(".//"));
        TransformerContext transformerContext = new TransformerContext(evrlSpringContext);
        byte[] bytes = "".getBytes();

        TransformerException transformerException = Assertions.assertThrows(TransformerException.class, () -> transformer.transform(transformerContext, transformerConfig, bytes));

        Throwable cause = transformerException.getCause();
        assertEquals(NullPointerException.class, cause.getClass());
    }

}
