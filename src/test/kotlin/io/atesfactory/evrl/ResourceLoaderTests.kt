package io.atesfactory.evrl

import java.io.FileNotFoundException
import java.io.Serializable
import java.time.LocalDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ResourceLoader

@SpringBootTest
class ResourceLoaderTests {

    @Autowired
    lateinit var resourceLoader: ResourceLoader

    @MockBean
    lateinit var exampleProperties: ExampleProperties

    @BeforeEach
    fun reset() {
        TransformerRegistry.reset()
        LoaderRegistry.reset()
    }

    @Test
    fun `should load env input stream`() {
        val content = "HELLO"

        val resource = resourceLoader.getResource("evrl://text/content:$content")
        val text = resource.inputStream.bufferedReader().use { it.readText() }

        assertEquals(content, text)
    }

    @Test
    fun `should load env into file`() {
        val content = "HELLO"
        val filePath = "./tmp-file"

        val resource = resourceLoader.getResource("evrl://text|file:$filePath/content:$content")
        val file = resource.file
        try {
            val text = String(file.readBytes())

            assertEquals(content, text)
        } finally {
            file.delete()
        }
    }

    @Test
    fun `should load base64 encoded value into decoded input stream`() {
        val content = "SEVMTE8="
        val decodedValue = "HELLO"

        val resource = resourceLoader.getResource("evrl://base64/content:$content")
        val text = resource.inputStream.bufferedReader().use { it.readText() }

        assertEquals(decodedValue, text)
    }

    @Test
    fun `should load base64 encoded value into file with decoded content`() {
        val content = "SEVMTE8="
        val decodedValue = "HELLO"
        val filePath = "./test/tmp-file-base64"

        val resource = resourceLoader.getResource("evrl://base64|file:$filePath/content:$content")
        val file = resource.file
        try {
            assertTrue(file.absolutePath.contains(filePath))

            val text = String(file.readBytes())

            assertEquals(decodedValue, text)
        } finally {
            file.delete()
        }
    }

    @Test
    fun `should fail to load resource due to typo in protocol`() {
        val content = "HELLO"
        val location = "evrl://text/content:$content"
        assertThrows<FileNotFoundException> {
            val resource = resourceLoader.getResource("U$location")
            resource.inputStream.readAllBytes()
        }
        val resource = resourceLoader.getResource(location)
        val text = resource.inputStream.bufferedReader().use { it.readText() }

        assertEquals(content, text)

    }

    @Test
    fun `should load property at runtime`() {
        val content = "io.atesfactory.evrl.example5"
        val value = "HELLO"

        val resource = resourceLoader.getResource("evrl://text/prop:$content")
        val text = resource.inputStream.bufferedReader().use { it.readText() }

        assertEquals(value, text)
    }

    @Test
    fun `should load property at runtime with env prefix`() {
        val content = "io.atesfactory.evrl.example5"
        val value = "HELLO"

        val resource = resourceLoader.getResource("evrl://text/env:$content")
        val text = resource.inputStream.bufferedReader().use { it.readText() }

        assertEquals(value, text)
    }

    @Test
    fun `should throw exception on incompatible destination type`() {
        val content = "io.atesfactory.evrl.example5"

        TransformerRegistry.register("not-compatible-destination-type", listOf(object : Transformer<String, LocalDateTime> {
            override fun transform(transformerContext: TransformerContext, transformerConfig: TransformerConfig, i: Serializable): LocalDateTime {
                return LocalDateTime.now()
            }

            override fun getInType(): Class<String> {
                return String::class.java
            }

            override fun getOutType(): Class<LocalDateTime> {
                return LocalDateTime::class.java
            }
        }))

         assertThrows<ResourceFactoryException> {  resourceLoader.getResource("evrl://not-compatible-destination-type/env:$content") }
    }
}
