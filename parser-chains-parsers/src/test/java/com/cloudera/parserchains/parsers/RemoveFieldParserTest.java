package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.FieldValue;
import com.cloudera.parserchains.core.Message;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

        // validate
        assertFalse(output.getField(FieldName.of("field1")).isPresent(), 
            "Expected 'field1' to have been removed.");
        assertFalse(output.getField(FieldName.of("field2")).isPresent(), 
            "Expected 'field2' to have been removed.");
        assertEquals(FieldValue.of("value3"), output.getField(FieldName.of("field3")).get(), 
            "Expected 'field3' to remain.");
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
        assertEquals(FieldValue.of("value1"), output.getField(FieldName.of("field1")).get(),
            "Expected 'field1' to remain.");
        assertEquals(FieldValue.of("value2"), output.getField(FieldName.of("field2")).get(),
            "Expected 'field2' to remain.");
        assertEquals(FieldValue.of("value3"), output.getField(FieldName.of("field3")).get(),
            "Expected 'field3' to remain.");
    }
}
