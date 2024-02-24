package io.atesfactory.evrl;

import io.atesfactory.evrl.configuration.EvrlSpringContext;
import io.atesfactory.evrl.resourcefactory.ResourceFactoryContext;
import io.atesfactory.evrl.resourcefactory.ResourceFactoryRegistry;
import java.io.File;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;

class ResourceFactoryTests {

    private final EvrlSpringContext evrlSpringContext = new EvrlSpringContext(
            Mockito.mock(ApplicationContext.class),
            Mockito.mock(Environment.class)
    );

    @Test
    void shouldCreateInputStreamResourceFromByteArray() {
        byte[] content = "Hello".getBytes();

        Object resource = ResourceFactoryRegistry.get(content.getClass()).create(
                new ResourceFactoryContext(evrlSpringContext), content);

        Assertions.assertEquals(InputStreamResource.class, resource.getClass());
    }

    @Test
    void shouldCreateFileSystemResourceFromFile() {
        File content = new File("myFile");

        Object resource = ResourceFactoryRegistry.get(content.getClass()).create(
                new ResourceFactoryContext(evrlSpringContext), content);

        Assertions.assertEquals(FileSystemResource.class, resource.getClass());
    }
}
