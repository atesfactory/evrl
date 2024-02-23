package io.atesfactory.evrl

import io.atesfactory.evrl.loader.Loader
import io.atesfactory.evrl.loader.LoaderContext
import io.atesfactory.evrl.loader.LoaderRegistry
import io.atesfactory.evrl.resourcefactory.ResourceFactory
import io.atesfactory.evrl.resourcefactory.ResourceFactoryContext
import io.atesfactory.evrl.resourcefactory.ResourceFactoryRegistry
import io.atesfactory.evrl.transformer.Transformer
import io.atesfactory.evrl.transformer.TransformerConfig
import io.atesfactory.evrl.transformer.TransformerContext
import io.atesfactory.evrl.transformer.TransformerRegistry
import java.io.ByteArrayInputStream
import java.io.Serializable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader

@SpringBootTest
class ExtensionTests {

    @Autowired
    lateinit var resourceLoader: ResourceLoader

    @AfterEach
    fun reset() {
        TransformerRegistry.reset()
        LoaderRegistry.reset()
    }

    @Test
    fun `should register, use, and de-register custom Transformer`() {
        val transformerCounts = TransformerRegistry.getAll().size

        TransformerRegistry.register("reverse", listOf(ExampleTransformer()))

        assertEquals(transformerCounts + 1, TransformerRegistry.getAll().size)

        val input = "Hello"
        val expectedOutput = "olleH"

        val resource = resourceLoader.getResource("evrl://reverse|text/content:$input")
        val text = String(resource.inputStream.readAllBytes())

        assertEquals(expectedOutput, text)

        TransformerRegistry.reset()

        assertEquals(transformerCounts, TransformerRegistry.getAll().size)
    }

    @Test
    fun `should register, use, and de-register custom Loader `(){
        val loadersCount = LoaderRegistry.getAll().size

        LoaderRegistry.register("custom", ExampleLoader())

        assertEquals(loadersCount + 1, LoaderRegistry.getAll().size)

        val input = "myText"
        val resource = resourceLoader.getResource("evrl://text/custom:$input")
        val text = String(resource.inputStream.readAllBytes())

        assertEquals("$input-loaded", text)

        LoaderRegistry.reset()

        assertEquals(loadersCount, LoaderRegistry.getAll().size)
    }

    @Test
    fun `should register, use, and de-register custom ResourceFactory`() {
        val resourceFactoryCount = ResourceFactoryRegistry.getAll().size

        ResourceFactoryRegistry.register(String::class.java, ExampleResourceFactory())

        assertEquals(resourceFactoryCount + 1, ResourceFactoryRegistry.getAll().size)

        val input = "myText"
        val resource = resourceLoader.getResource("evrl:///content:$input")
        val text = String(resource.inputStream.readAllBytes())

        assertEquals("${input.reversed()}", text)

        ResourceFactoryRegistry.reset()

        assertEquals(resourceFactoryCount, ResourceFactoryRegistry.getAll().size)
    }
}


class ExampleTransformer: Transformer<String, String> {
    override fun transform(transformerContext: TransformerContext, transformerConfig: TransformerConfig, i: Serializable): String {
        return (i as String).reversed()
    }

    override fun getInType(): Class<String> {
        return String::class.java
    }

    override fun getOutType(): Class<String> {
        return String::class.java
    }
}

class ExampleLoader: Loader<String> {
    override fun load(loaderContext: LoaderContext, loadable: String): String {
        return "$loadable-loaded"
    }
}

class ExampleResourceFactory: ResourceFactory {
    override fun create(resourceFactoryContext: ResourceFactoryContext, content: Any): Resource {
        // Transformation here to show the case
        return InputStreamResource(ByteArrayInputStream((content as String).reversed().toByteArray()))
    }
}
