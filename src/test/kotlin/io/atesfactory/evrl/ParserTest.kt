package io.atesfactory.evrl

import io.atesfactory.evrl.parser.Parser
import io.atesfactory.evrl.parser.ParserException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ParserTest {

    @Test
    fun `should throw ParserException on wrong protocol format`() {
        val location = "evrl://abcdefg"

        assertThrows<ParserException> { Parser()
            .parse(location) }
    }

}
