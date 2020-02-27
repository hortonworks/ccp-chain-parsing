package com.cloudera.parserchains.core.catalog;

import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.config.ConfigDescriptor;
import com.cloudera.parserchains.core.config.ConfigKey;
import com.cloudera.parserchains.core.config.ConfigName;
import com.cloudera.parserchains.core.config.ConfigValue;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnnotationBasedParserInfoBuilderTest {

    /**
     * A parser used for testing that is completely valid.
     */
    @MessageParser(
            name = "Valid Parser",
            description = "This is a valid parser.")
    public class ValidParser implements Parser {
        @Override
        public Message parse(Message message) {
            // do nothing
            return null;
        }

        @Override
        public List<FieldName> outputFields() {
            // do nothing
            return null;
        }

        @Override
        public List<ConfigDescriptor> validConfigurations() {
            // do nothing
            return null;
        }

        @Override
        public void configure(ConfigName name, Map<ConfigKey, ConfigValue> values) {
            // do nothing
        }
    }

    @Test
    void build() {
        Optional<ParserInfo> result = new AnnotationBasedParserInfoBuilder()
                .build(ValidParser.class);
        assertTrue(result.isPresent());
        assertEquals("Valid Parser", result.get().getName());
        assertEquals("This is a valid parser.", result.get().getDescription());
        assertEquals(ValidParser.class, result.get().getParserClass());
    }

    /**
     * A parser used for testing that is missing the required {@link MessageParser} annotation.
     */
    public class MissingAnnotationParser implements Parser {
        @Override
        public Message parse(Message message) {
            // do nothing
            return null;
        }

        @Override
        public List<FieldName> outputFields() {
            // do nothing
            return null;
        }

        @Override
        public List<ConfigDescriptor> validConfigurations() {
            // do nothing
            return null;
        }

        @Override
        public void configure(ConfigName name, Map<ConfigKey, ConfigValue> values) {
            // do nothing
        }
    }

    @Test
    void classIsMissingAnnotation() {
        Optional<ParserInfo> result = new AnnotationBasedParserInfoBuilder()
                .build(MissingAnnotationParser.class);
        assertFalse(result.isPresent());
    }

    @Test
    void classIsNotAParser() {
        Optional<ParserInfo> result = new AnnotationBasedParserInfoBuilder()
                .build(Object.class);
        assertFalse(result.isPresent());
    }
}
