package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.FieldValue;
import com.cloudera.parserchains.core.Message;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        Message expected = Message.builder()
                .addField(FieldName.of("new1"), FieldValue.of("value1"))
                .addField(FieldName.of("original2"), FieldValue.of("value2"))
                .build();
        assertEquals(expected, output);
    }

    @Test
    void fieldDoesNotExist() {
        // rename a field that does not exist
        Message input = Message.builder()
                .addField(FieldName.of("original1"), FieldValue.of("value1"))
                .build();
        Message output = new RenameFieldParser()
                .renameField(FieldName.of("doesNotExist"), FieldName.of("new1"))
                .parse(input);

        // the output should not have changed from the input
        assertEquals(input, output, "The output fields should be the same as the input");
    }
}
