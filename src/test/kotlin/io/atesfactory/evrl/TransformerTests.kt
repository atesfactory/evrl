package io.atesfactory.evrl

import java.io.File
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment

class TransformerTests {

    private var evrlSpringContext = EvrlSpringContext(Mockito.mock(ApplicationContext::class.java), Mockito.mock(Environment::class.java))

    @Test
    fun `should get bytes from string`() {
        val content = "HELLO"
        val transformerName = "text"
        val transformer = TransformerRegistry.get(transformerName, String::class.java)
        val transformerConfig = TransformerConfig(transformerName)
        val transformed = transformer.transform(TransformerContext(evrlSpringContext), transformerConfig, content) as ByteArray

        assertEquals(content, String(transformed, Charsets.UTF_8))
    }

    @Test
    fun `should get file from bytes`() {
        val content = "Example2Text\n"
        val transformerName = "file"
        val transformer = TransformerRegistry.get(transformerName, ByteArray::class.java)
        val transformerConfig = TransformerConfig(transformerName, config = listOf("./test-example.txt"))
        val transformed = transformer.transform(TransformerContext(evrlSpringContext), transformerConfig, content.toByteArray(Charsets.UTF_8)) as File
        try {
            assertEquals(content, String(transformed.readBytes(), Charsets.UTF_8))
        } finally {
            transformed.delete()
        }
    }

    @Test
    fun `should get temp file from bytes`() {
        val content = "Example2Text\n"
        val transformerName = "file"
        val transformer = TransformerRegistry.get(transformerName, ByteArray::class.java)
        val transformerConfig = TransformerConfig(transformerName, config = listOf())
        val transformed = transformer.transform(TransformerContext(evrlSpringContext), transformerConfig, content.toByteArray(Charsets.UTF_8)) as File
        try {
            assertEquals(content, String(transformed.readBytes(), Charsets.UTF_8))
        } finally {
            transformed.delete()
        }
    }

    @Test
    fun `should get file with subdirectories from bytes`() {
        val content = "Example2Text\n"
        val transformerName = "file"
        val transformer = TransformerRegistry.get(transformerName, ByteArray::class.java)
        val transformerConfig = TransformerConfig(transformerName, config = listOf("./test/test-example.txt"))
        val transformed = transformer.transform(TransformerContext(evrlSpringContext), transformerConfig, content.toByteArray(Charsets.UTF_8)) as File
        try {
            assertEquals(content, String(transformed.readBytes(), Charsets.UTF_8))
        } finally {
            transformed.delete()
            val subdirectory = File("./test")
            if(subdirectory.exists()) {
                subdirectory.delete()
            }
        }
    }

    @Test
    fun `should decode base64`() {
        val content = "SEVMTE8="
        val transformerName = "base64"
        val transformer = TransformerRegistry.get(transformerName, String::class.java)
        val transformerConfig = TransformerConfig(transformerName)
        val transformed = transformer.transform(TransformerContext(evrlSpringContext), transformerConfig, content) as ByteArray

        assertEquals("HELLO", String(transformed, Charsets.UTF_8))
    }

    @Test
    fun `should throw TransformerException for unknown transformer`() {
        val transformerName = "unknown"

        assertThrows<TransformerException> { TransformerRegistry.get(transformerName, String::class.java) }
    }

}
