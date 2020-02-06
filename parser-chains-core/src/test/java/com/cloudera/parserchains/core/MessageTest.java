package com.cloudera.parserchains.core;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MessageTest {

    @Test
    void addField() {
        Message message = Message.builder()
                .addField(FieldName.of("field1"), FieldValue.of("value1"))
                .addField(FieldName.of("field2"), FieldValue.of("value2"))
                .addField(FieldName.of("field3"), FieldValue.of("value3"))
                .build();

        // validate presence
        assertEquals(FieldValue.of("value1"), message.getField(FieldName.of("field1")).get());
        assertEquals(FieldValue.of("value2"), message.getField(FieldName.of("field2")).get());
        assertEquals(FieldValue.of("value3"), message.getField(FieldName.of("field3")).get());

        // validate absence
        assertFalse(message.getField(FieldName.of("field4")).isPresent());
        assertFalse(message.getField(FieldName.of("field5")).isPresent());
        assertFalse(message.getField(FieldName.of("field6")).isPresent());

        // no errors
        assertFalse(message.getError().isPresent());
    }

    @Test
    void removeField() {
        Message original = Message.builder()
                .addField(FieldName.of("field1"), FieldValue.of("value1"))
                .addField(FieldName.of("field2"), FieldValue.of("value2"))
                .addField(FieldName.of("field3"), FieldValue.of("value3"))
                .build();

        Message copy = Message.builder()
                .withFields(original)
                .removeField(FieldName.of("field1"))
                .build();

        // validate presence
        assertEquals(FieldValue.of("value2"), original.getField(FieldName.of("field2")).get());
        assertEquals(FieldValue.of("value3"), original.getField(FieldName.of("field3")).get());

        // validate absence
        assertFalse(copy.getField(FieldName.of("field1")).isPresent());

        // no errors
        assertFalse(copy.getError().isPresent());
    }

    @Test
    void removeFields() {
        Message original = Message.builder()
                .addField(FieldName.of("field1"), FieldValue.of("value1"))
                .addField(FieldName.of("field2"), FieldValue.of("value2"))
                .addField(FieldName.of("field3"), FieldValue.of("value3"))
                .build();

        Message copy = Message.builder()
                .withFields(original)
                .removeFields(Arrays.asList(FieldName.of("field1"), FieldName.of("field2"), FieldName.of("field3")))
                .build();

        // validate absence
        assertFalse(copy.getField(FieldName.of("field1")).isPresent());
        assertFalse(copy.getField(FieldName.of("field2")).isPresent());
        assertFalse(copy.getField(FieldName.of("field3")).isPresent());

        // no errors
        assertFalse(copy.getError().isPresent());
    }

    @Test
    void renameFields() {
        Message original = Message.builder()
                .addField(FieldName.of("original1"), FieldValue.of("value1"))
                .addField(FieldName.of("original2"), FieldValue.of("value2"))
                .addField(FieldName.of("original3"), FieldValue.of("value3"))
                .build();

        // rename 'original1' to 'new1'
        Message actual = Message.builder()
                .withFields(original)
                .renameField(FieldName.of("original1"), FieldName.of("new1"))
                .renameField(FieldName.of("original2"), FieldName.of("new2"))
                .build();

        // validate
        Message expected = Message.builder()
                .addField(FieldName.of("new1"), FieldValue.of("value1"))
                .addField(FieldName.of("new2"), FieldValue.of("value2"))
                .addField(FieldName.of("original3"), FieldValue.of("value3"))
                .build();
        assertEquals(expected, actual);
    }

    @Test
    void renameMissingField() {
        Message original = Message.builder()
                .addField(FieldName.of("field1"), FieldValue.of("value1"))
                .build();

        // rename 'missing1' which does not exist
        Message actual = Message.builder()
                .withFields(original)
                .renameField(FieldName.of("missing1"), FieldName.of("new1"))
                .build();

        // validate
        Message expected = Message.builder()
                .addField(FieldName.of("field1"), FieldValue.of("value1"))
                .build();
        assertEquals(expected, actual);
    }

    @Test
    void withFields() {
        Message original = Message.builder()
                .addField(FieldName.of("field1"), FieldValue.of("value1"))
                .addField(FieldName.of("field2"), FieldValue.of("value2"))
                .addField(FieldName.of("field3"), FieldValue.of("value3"))
                .build();

        Message copy = Message.builder()
                .withFields(original)
                .build();

        // validate presence
        assertEquals(FieldValue.of("value1"), copy.getField(FieldName.of("field1")).get());
        assertEquals(FieldValue.of("value2"), copy.getField(FieldName.of("field2")).get());
        assertEquals(FieldValue.of("value3"), copy.getField(FieldName.of("field3")).get());

        // validate absence
        assertFalse(copy.getField(FieldName.of("field4")).isPresent());
        assertFalse(copy.getField(FieldName.of("field5")).isPresent());
        assertFalse(copy.getField(FieldName.of("field6")).isPresent());

        // no errors
        assertFalse(copy.getError().isPresent());
    }

    @Test
    void withErrorMessage() {
        final String errorMessage = "this is an error";
        Message original = Message.builder()
                .withError(errorMessage)
                .build();
        assertEquals(errorMessage, original.getError().get().getMessage());
    }

    @Test
    void withError() {
        final Exception exception = new IllegalStateException("this is an error");
        Message original = Message.builder()
                .withError(exception)
                .build();
        assertEquals(exception, original.getError().get());
    }
}
