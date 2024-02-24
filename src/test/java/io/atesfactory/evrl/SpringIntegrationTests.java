package io.atesfactory.evrl;

import io.atesfactory.evrl.configuration.EvrlSpringContext;
import io.atesfactory.evrl.configuration.ProtocolResolver;
import io.atesfactory.evrl.loader.EvrlResourceLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SpringIntegrationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    @Test
    void shouldSetProtocolResolverToDefaultResourceLoader() {
        ProtocolResolver protocolResolver = new ProtocolResolver(
                new EvrlSpringContext(
                        applicationContext,
                        environment
                )
        );

        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        protocolResolver.setResourceLoader(resourceLoader);

        assertTrue(resourceLoader.getProtocolResolvers().contains(protocolResolver));
    }

    @Test
    void shouldHaveDelegateClassLoader() {
        EvrlSpringContext evrlSpringContext =
                new EvrlSpringContext(applicationContext, environment);
        DefaultResourceLoader delegate = new DefaultResourceLoader();
        EvrlResourceLoader resourceLoader =
                new EvrlResourceLoader(evrlSpringContext, delegate);

        assertEquals(delegate.getClassLoader(), resourceLoader.getClassLoader());
    }
}
