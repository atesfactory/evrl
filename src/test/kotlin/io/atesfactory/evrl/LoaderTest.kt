package io.atesfactory.evrl

import io.atesfactory.evrl.loader.LoaderException
import io.atesfactory.evrl.loader.LoaderRegistry
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LoaderTest {

    @Test
    fun `should throw LoaderException`() {
        val loaderName = "unknown"

        assertThrows<LoaderException> { LoaderRegistry.get(loaderName) }
    }

}
