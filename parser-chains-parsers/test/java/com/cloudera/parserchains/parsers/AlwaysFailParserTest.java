package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.ConfigName;
import com.cloudera.parserchains.core.ConfigValue;
import com.cloudera.parserchains.core.Message;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlwaysFailParserTest {

    @Test
    void alwaysFail() {
        Message input = Message.builder().build();
        Message output = new AlwaysFailParser()
                .withError("expect failure")
                .parse(input);

        assertTrue(output.getError().isPresent());
        assertEquals(IllegalStateException.class, output.getError().get().getClass());
        assertEquals("expect failure", output.getError().get().getMessage());
    }

    @Test
    void failWithDefaultMessage() {
        Message input = Message.builder().build();
        Message output = new AlwaysFailParser()
                .parse(input);

        assertTrue(output.getError().isPresent());
        assertEquals(IllegalStateException.class, output.getError().get().getClass());
        assertEquals("Parsing error encountered", output.getError().get().getMessage());
    }

    @Test
    void configure() {
        AlwaysFailParser parser = new AlwaysFailParser();
        parser.configure(AlwaysFailParser.errorConfig, Arrays.asList(ConfigValue.of("the error message")));
        assertEquals("the error message", parser.getError().getMessage());
    }

    @Test
    void unexpectedConfig() {
        assertThrows(IllegalArgumentException.class, () ->
                new AlwaysFailParser()
                        .configure(ConfigName.of("invalid", false), Collections.emptyList()));
    }
}
