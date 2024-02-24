package io.atesfactory.evrl;

import io.atesfactory.evrl.parser.Parser;
import io.atesfactory.evrl.parser.ParserException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParserTests {

    @Test
    void shouldThrowParserExceptionOnWrongProtocolFormat() {
        String location = "evrl://abcdefg";

        Parser parser = new Parser();
        assertThrows(ParserException.class, () -> parser.parse(location));
    }

}
