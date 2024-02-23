package io.atesfactory.evrl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.Resource

@SpringBootTest
class PropertiesTests {

    @Value("\${io.atesfactory.evrl.example1}")
    lateinit var resource: Resource

    @Value("\${io.atesfactory.evrl.example2}")
    lateinit var classPathResource: Resource

    @Autowired
    lateinit var exampleProperties: ExampleProperties

    @Value("\${io.atesfactory.evrl.example4}")
    lateinit var fileLoadedBase64Content: Resource

    @Test
    fun `should load text from properties`() {
        val content = "myText"

        val text = resource.inputStream.bufferedReader().use { it.readText() }

        assertEquals(content, text)
    }

    @Test
    fun `should still be able to load other resources like classpath`() {
        val fileContent = "Example2Text\n"

        val text = classPathResource.inputStream.bufferedReader().use { it.readText() }

        assertEquals(fileContent, text)
    }

    @Test
    fun `should load resources into configuration properties input stream based`() {
        val content = "inputStreamBasedText"

        val text = exampleProperties.inputStreamBased!!.inputStream.bufferedReader().use { it.readText() }

        assertEquals(content, text)
    }

    @Test
    fun `should load resources into configuration properties file based`() {
        val fileContent = "fileBasedText"

        val file = exampleProperties.fileBased!!.file
        try {
            val text = String(file.readBytes())

            assertEquals(fileContent, text)
        } finally {
            file.delete()
        }
    }

    @Test
    fun `should load base64 content from file and create an input stream`() {
        val content = "HELLO"

        val text = fileLoadedBase64Content.inputStream.bufferedReader().use { it.readText() }

        assertEquals(content, text)
    }

}
