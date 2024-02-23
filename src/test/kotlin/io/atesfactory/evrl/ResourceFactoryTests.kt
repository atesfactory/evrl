package io.atesfactory.evrl

import io.atesfactory.evrl.configuration.EvrlSpringContext
import io.atesfactory.evrl.resourcefactory.ResourceFactoryContext
import io.atesfactory.evrl.resourcefactory.ResourceFactoryRegistry
import java.io.File
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.InputStreamResource

class ResourceFactoryTests {

    private var evrlSpringContext = EvrlSpringContext(
        Mockito.mock(ApplicationContext::class.java),
        Mockito.mock(Environment::class.java)
    )

    @Test
    fun `should create InputStreamResource from ByteArray`(){
        val content = "Hello".toByteArray()

        val resource = ResourceFactoryRegistry.get(content::class.java).create(
            ResourceFactoryContext(evrlSpringContext), content)

        assertEquals(InputStreamResource::class.java, resource::class.java)
    }

    @Test
    fun `should create FileSystemResource from File`() {
        val content = File("myFile")

        val resource = ResourceFactoryRegistry.get(content::class.java).create(
            ResourceFactoryContext(evrlSpringContext), content)

        assertEquals(FileSystemResource::class.java, resource::class.java)
    }

}
