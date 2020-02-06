package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.ConfigName;
import com.cloudera.parserchains.core.ConfigValue;
import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.FieldValue;
import com.cloudera.parserchains.core.Message;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RemoveFieldParserTest {

    @Test
    void removeField() {
        Message input = Message.builder()
                .addField(FieldName.of("field1"), FieldValue.of("value1"))
                .addField(FieldName.of("field2"), FieldValue.of("value2"))
                .addField(FieldName.of("field3"), FieldValue.of("value3"))
                .build();
        Message output = new RemoveFieldParser()
                .removeField(FieldName.of("field1"))
                .removeField(FieldName.of("field2"))
                .parse(input);

        // ensure the fields were removed
        assertFalse(output.getField(FieldName.of("field1")).isPresent());
        assertFalse(output.getField(FieldName.of("field2")).isPresent());
        assertEquals(FieldValue.of("value3"), output.getField(FieldName.of("field3")).get());
    }

    @Test
    void nothingToRemove() {
        Message input = Message.builder()
                .addField(FieldName.of("field1"), FieldValue.of("value1"))
                .addField(FieldName.of("field2"), FieldValue.of("value2"))
                .addField(FieldName.of("field3"), FieldValue.of("value3"))
                .build();
        Message output = new RemoveFieldParser()
                .parse(input);

        // ensure the fields were removed
        assertEquals(FieldValue.of("value1"), output.getField(FieldName.of("field1")).get());
        assertEquals(FieldValue.of("value2"), output.getField(FieldName.of("field2")).get());
        assertEquals(FieldValue.of("value3"), output.getField(FieldName.of("field3")).get());
    }

    @Test
    void configure() {
        RemoveFieldParser parser = new RemoveFieldParser();
        parser.configure(RemoveFieldParser.removeConfig, Arrays.asList(ConfigValue.of("field1")));
        assertThat(parser.getFieldsToRemove(), hasItems(FieldName.of("field1")));
    }

    @Test
    void unexpectedConfig() {
        assertThrows(IllegalArgumentException.class,
                () -> new RemoveFieldParser().configure(ConfigName.of("invalid", false), Collections.emptyList()));
    }
}
