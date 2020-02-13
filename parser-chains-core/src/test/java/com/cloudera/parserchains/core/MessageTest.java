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

        assertEquals(3, message.getFields().size(), 
            "Expected 3 fields to have been added to the message.");
        assertEquals(FieldValue.of("value1"), message.getField(FieldName.of("field1")).get(),
            "Expected field1 to have been added to the message.");
        assertEquals(FieldValue.of("value2"), message.getField(FieldName.of("field2")).get(),
            "Expected field2 to have been added to the message.");
        assertEquals(FieldValue.of("value3"), message.getField(FieldName.of("field3")).get(),
            "Expected field3 to have been added to the message.");
        assertFalse(message.getError().isPresent(),
            "Expected no errors to have been attached to the message.");
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
        
        assertFalse(copy.getField(FieldName.of("field1")).isPresent(),
            "Expected field1 to have been removed from the message.");
        assertEquals(FieldValue.of("value2"), copy.getField(FieldName.of("field2")).get(),
            "Expected field2 to have been added to the message.");
        assertEquals(FieldValue.of("value3"), copy.getField(FieldName.of("field3")).get(),
            "Expected field3 to have been added to the message.");
        assertFalse(copy.getError().isPresent(),
            "Expected no errors to have been attached to the message.");
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

        assertFalse(copy.getField(FieldName.of("field1")).isPresent(),
            "Expected field1 to have been removed from the message.");
        assertFalse(copy.getField(FieldName.of("field2")).isPresent(),
            "Expected field2 to have been removed from the message.");
        assertFalse(copy.getField(FieldName.of("field3")).isPresent(),
            "Expected field3 to have been removed from the message.");
        assertFalse(copy.getError().isPresent(),
            "Expected no errors to have been attached to the message.");
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

        assertEquals(3, actual.getFields().size(), 
            "Expected 3 fields in the message.");
        assertEquals(FieldValue.of("value1"), actual.getField(FieldName.of("new1")).get(),
            "Expected original1 to have been renamed to new1.");
        assertEquals(FieldValue.of("value2"), actual.getField(FieldName.of("new2")).get(),
            "Expected original2 to have been renamed to new2.");
        assertEquals(FieldValue.of("value3"), actual.getField(FieldName.of("original3")).get(),
            "Expected original3 to remain unchanged in the message.");
        assertFalse(actual.getError().isPresent(),
            "Expected no errors to have been attached to the message.");
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

        assertEquals(1, actual.getFields().size(), 
            "Expected 1 field in the message.");
        assertEquals(FieldValue.of("value1"), actual.getField(FieldName.of("field1")).get(),
            "Expected field1 to remain unchanged in the message.");
        assertFalse(actual.getError().isPresent(),
            "Expected no errors to have been attached to the message.");        
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

        assertEquals(3, copy.getFields().size(), 
            "Expected 3 fields the copied message.");
        assertEquals(FieldValue.of("value1"), copy.getField(FieldName.of("field1")).get(),
            "Expected field1 to have been added to the message.");
        assertEquals(FieldValue.of("value2"), copy.getField(FieldName.of("field2")).get(),
            "Expected field2 to have been added to the message.");
        assertEquals(FieldValue.of("value3"), copy.getField(FieldName.of("field3")).get(),
            "Expected field3 to have been added to the message.");
        assertFalse(copy.getError().isPresent(),
            "Expected no errors to have been attached to the message.");
    }

    @Test
    void withErrorMessage() {
        final String errorMessage = "this is an error";
        Message original = Message.builder()
                .withError(errorMessage)
                .build();
        assertEquals(errorMessage, original.getError().get().getMessage(), 
            "Expected an error message to have been attached to the message.");
    }

    @Test
    void withError() {
        final Exception exception = new IllegalStateException("this is an error");
        Message original = Message.builder()
                .withError(exception)
                .build();
        assertEquals(exception, original.getError().get(),
            "Expected an exception to have been attached to the message.");
    }
}
