package io.atesfactory.evrl;

import io.atesfactory.evrl.configuration.EvrlSpringContext;
import io.atesfactory.evrl.loader.Loader;
import io.atesfactory.evrl.loader.LoaderContext;
import io.atesfactory.evrl.loader.LoaderRegistry;
import io.atesfactory.evrl.resourcefactory.ResourceFactory;
import io.atesfactory.evrl.resourcefactory.ResourceFactoryContext;
import io.atesfactory.evrl.resourcefactory.ResourceFactoryRegistry;
import io.atesfactory.evrl.transformer.Transformer;
import io.atesfactory.evrl.transformer.TransformerConfig;
import io.atesfactory.evrl.transformer.TransformerContext;
import io.atesfactory.evrl.transformer.TransformerRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import static io.atesfactory.evrl.FileUtil.convertInputStreamtoString;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ExtensionTests {

    @Autowired
    private ResourceLoader resourceLoader;

    @AfterEach
    void reset() {
        TransformerRegistry.reset();
        LoaderRegistry.reset();
        ResourceFactoryRegistry.reset();
    }

    @Test
    void shouldRegisterUseAndDeRegisterCustomTransformer() throws Exception {
        int transformerCounts = TransformerRegistry.getAll().size();

        TransformerRegistry.register("reverse", java.util.Collections.singletonList(new ExampleTransformer()));

        Assertions.assertEquals(transformerCounts + 1, TransformerRegistry.getAll().size());

        String input = "Hello";
        String expectedOutput = "olleH";

        Resource resource = resourceLoader.getResource("evrl://reverse|text/content:" + input);
        String text = convertInputStreamtoString(resource.getInputStream());

        Assertions.assertEquals(expectedOutput, text);

        TransformerRegistry.reset();

        Assertions.assertEquals(transformerCounts, TransformerRegistry.getAll().size());
    }

    @Test
    void shouldRegisterUseAndDeRegisterCustomLoader() throws Exception {
        int loadersCount = LoaderRegistry.getAll().size();

        LoaderRegistry.register("custom", new ExampleLoader());

        Assertions.assertEquals(loadersCount + 1, LoaderRegistry.getAll().size());

        String input = "myText";
        Resource resource = resourceLoader.getResource("evrl://text/custom:" + input);
        String text = convertInputStreamtoString(resource.getInputStream());

        Assertions.assertEquals(input + "-loaded", text);

        LoaderRegistry.reset();

        Assertions.assertEquals(loadersCount, LoaderRegistry.getAll().size());
    }

    @Test
    void shouldRegisterUseAndDeRegisterCustomResourceFactory() throws Exception {
        int resourceFactoryCount = ResourceFactoryRegistry.getAll().size();

        ResourceFactoryRegistry.register(String.class, new ExampleResourceFactory());

        Assertions.assertEquals(resourceFactoryCount + 1, ResourceFactoryRegistry.getAll().size());

        String input = "myText";
        Resource resource = resourceLoader.getResource("evrl:///content:" + input);
        String text = convertInputStreamtoString(resource.getInputStream());

        Assertions.assertEquals(new StringBuilder(input).reverse().toString(), text);

        ResourceFactoryRegistry.reset();

        Assertions.assertEquals(resourceFactoryCount, ResourceFactoryRegistry.getAll().size());
    }
}

class ExampleTransformer implements Transformer<String, String> {
    @Override
    public String transform(TransformerContext transformerContext, TransformerConfig transformerConfig, Serializable i) {
        EvrlSpringContext evrlSpringContext = transformerContext.getEvrlSpringContext();
        assertNotNull(evrlSpringContext);
        return new StringBuilder((String) i).reverse().toString();
    }

    @Override public Class<String> getInType() {
        return String.class;
    }

    @Override
    public Class<String> getOutType() {
        return String.class;
    }
}

class ExampleLoader implements Loader<String> {
    @Override
    public String load(LoaderContext loaderContext, String loadable) {
        assertNotNull(loaderContext.getEvrlSpringContext());
        return loadable + "-loaded";
    }
}

class ExampleResourceFactory implements ResourceFactory {
    @Override
    public Resource create(ResourceFactoryContext resourceFactoryContext, Object content) {
        assertNotNull(resourceFactoryContext.getEvrlSpringContext());
        return new InputStreamResource(new ByteArrayInputStream(new StringBuilder((String) content).reverse().toString().getBytes()));
    }
}
