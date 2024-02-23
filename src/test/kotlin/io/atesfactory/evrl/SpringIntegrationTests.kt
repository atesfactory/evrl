package io.atesfactory.evrl

import io.atesfactory.evrl.configuration.ProtocolResolver
import io.atesfactory.evrl.configuration.EvrlSpringContext
import io.atesfactory.evrl.loader.EvrlResourceLoader
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.core.io.DefaultResourceLoader

@SpringBootTest
class SpringIntegrationTests {

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Autowired
    lateinit var environment: Environment

    @Test
    fun `should set protocol resolver to default resource loader`() {
        val protocolResolver = ProtocolResolver(
            EvrlSpringContext(
                applicationContext,
                environment
            )
        )

        val resourceLoader = DefaultResourceLoader()
        protocolResolver.setResourceLoader(resourceLoader)

        assertTrue(resourceLoader.protocolResolvers.contains(protocolResolver))
    }

    @Test
    fun `should have delegate class loader`() {
        val evrlSpringContext =
            EvrlSpringContext(applicationContext, environment)
        val delegate = DefaultResourceLoader()
        val resourceLoader =
            EvrlResourceLoader(evrlSpringContext, delegate)

        assertEquals(delegate.classLoader, resourceLoader.classLoader)
    }

}
