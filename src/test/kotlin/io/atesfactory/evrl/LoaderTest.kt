package io.atesfactory.evrl

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LoaderTest {

    @Test
    fun `should throw LoaderException`() {
        val loaderName = "unknown"

        assertThrows<LoaderException> { LoaderRegistry.get(loaderName) }
    }

}
