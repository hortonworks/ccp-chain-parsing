package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.config.ConfigName;
import com.cloudera.parserchains.core.config.ConfigValues;
import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.FieldValue;
import com.cloudera.parserchains.core.Message;
import org.junit.jupiter.api.Test;

import static com.cloudera.parserchains.parsers.RenameFieldParser.Configurer.FROM_FIELD;
import static com.cloudera.parserchains.parsers.RenameFieldParser.Configurer.renameFieldConfig;
import static com.cloudera.parserchains.parsers.RenameFieldParser.Configurer.TO_FIELD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RenameFieldParserTest {

    @Test
    void renameField() {
        // rename 'original1' to 'new1'
        Message input = Message.builder()
                .addField(FieldName.of("original1"), FieldValue.of("value1"))
                .addField(FieldName.of("original2"), FieldValue.of("value2"))
                .build();
        Message output = new RenameFieldParser()
                .renameField(FieldName.of("original1"), FieldName.of("new1"))
                .parse(input);

        assertEquals(2, output.getFields().size(), 
            "Expected 2 output fields in the message.");
        assertFalse(output.getField(FieldName.of("original1")).isPresent(), 
            "Expected 'original1' to have been renamed.");
        assertEquals(FieldValue.of("value1"), output.getField(FieldName.of("new1")).get(), 
            "Expected 'original1' to have been renamed to 'new1'.");
        assertEquals(FieldValue.of("value2"), output.getField(FieldName.of("original2")).get(), 
            "Expected 'original2' to remain unchanged.");
    }

    @Test
    void renameFieldDoesNotExist() {
        Message input = Message.builder()
                .addField(FieldName.of("original1"), FieldValue.of("value1"))
                .build();
        Message output = new RenameFieldParser()
                .renameField(FieldName.of("doesNotExist"), FieldName.of("new1"))
                .parse(input);
        assertEquals(input, output,
                "The output fields should be the same as the input. No rename occurred.");
    }

    @Test
    void configure() {
        // rename 'original1' to 'new1'
        ConfigValues fieldToRename = ConfigValues.builder()
                .withValue(FROM_FIELD, "original1")
                .withValue(TO_FIELD, "new1")
                .build();
        RenameFieldParser parser = new RenameFieldParser();
        parser.configure(renameFieldConfig.getName(), fieldToRename);
        assertEquals(FieldName.of("new1"), parser.getFieldsToRename().get(FieldName.of("original1")));
    }

    @Test
    void unexpectedConfig() {
        assertThrows(IllegalArgumentException.class,
                () -> new RenameFieldParser().configure(
                        ConfigName.of("invalid"),
                        ConfigValues.builder().build()
                ));
    }
}
