package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.config.ConfigDescriptor;
import com.cloudera.parserchains.core.config.ConfigName;
import com.cloudera.parserchains.core.config.ConfigValues;
import com.cloudera.parserchains.core.Message;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.cloudera.parserchains.parsers.AlwaysFailParser.Configurer.errorMessageConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlwaysFailParserTest {

    @Test
    void alwaysFail() {
        final String expectedMessage = "Unexpected value found.";
        Message input = Message.builder().build();
        Message output = new AlwaysFailParser()
                .withError(expectedMessage)
                .parse(input);

        assertTrue(output.getError().isPresent(), 
            "Expected the parser to always fail.");
        assertEquals(IllegalStateException.class, output.getError().get().getClass(), 
            "Expected the parser to capture an exception.");
        assertEquals(expectedMessage, output.getError().get().getMessage(),
            "Expected the parser to report the error message.");


    }

    @Test
    void failWithDefaultMessage() {
        AlwaysFailParser parser = new AlwaysFailParser();        
        Message input = Message.builder().build();
        Message output = new AlwaysFailParser()
                .parse(input);

        final String expectedMessage = parser.getError().getMessage();
        assertEquals(expectedMessage, output.getError().get().getMessage(),
                "Expected the parser to report the default error message.");
        assertTrue(output.getError().isPresent(),
                "Expected the parser to always fail.");
        assertEquals(IllegalStateException.class, output.getError().get().getClass(),
                "Expected the parser to capture an exception.");
    }

    @Test
    void configure() {
        String expected = "a custom error message";
        ConfigValues errorMsg = ConfigValues.builder()
                .withValue(expected)
                .build();
        AlwaysFailParser parser = new AlwaysFailParser();
        parser.configure(errorMessageConfig.getName(), errorMsg);
        assertEquals(expected, parser.getError().getMessage());
    }

    @Test
    void unexpectedConfig() {
        assertThrows(IllegalArgumentException.class, () ->
                new AlwaysFailParser().configure(
                        ConfigName.of("Unexpected configuration"),
                        ConfigValues.builder().build()
                ));
    }

    @Test
    void validConfigurations() {
        List<ConfigDescriptor> configs = new AlwaysFailParser().validConfigurations();
        assertEquals(1, configs.size());
        assertEquals(errorMessageConfig, configs.get(0));
    }
}
