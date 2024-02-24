package io.atesfactory.evrl;

import io.atesfactory.evrl.configuration.EvrlSpringContext;
import io.atesfactory.evrl.loader.Loader;
import io.atesfactory.evrl.loader.LoaderContext;
import io.atesfactory.evrl.loader.LoaderException;
import io.atesfactory.evrl.loader.LoaderRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.io.FileNotFoundException;
import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class LoaderTests {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    Environment environment;


    @Test
    void shouldThrowLoaderException() {
        String loaderName = "unknown";

        assertThrows(LoaderException.class, () -> LoaderRegistry.get(loaderName));
    }

    @Test
    void shouldThrowLoaderExceptionForUnknownFile() {
        LoaderContext loaderContext = new LoaderContext(new EvrlSpringContext(applicationContext, environment));
        Loader<? extends Serializable> file = LoaderRegistry.get("file");
        LoaderException loaderException = assertThrows(LoaderException.class, () -> file.load(loaderContext, "./does-not-exist"));
        assertEquals(FileNotFoundException.class, loaderException.getCause().getClass());
    }

    @Test
    void shouldThrowLoaderExceptionForMissingProperty() {
        LoaderContext loaderContext = new LoaderContext(new EvrlSpringContext(applicationContext, environment));
        Loader<? extends Serializable> prop = LoaderRegistry.get("prop");
        LoaderException loaderException = assertThrows(LoaderException.class, () -> prop.load(loaderContext, "does-not-exist"));
        assertEquals("Could not find property or env named: does-not-exist", loaderException.getMessage());
    }
}
