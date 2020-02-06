package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.ConfigName;
import com.cloudera.parserchains.core.ConfigValue;
import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.FieldValue;
import com.cloudera.parserchains.core.Message;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.cloudera.parserchains.parsers.RenameFieldParser.CONFIG_FROM_KEY;
import static com.cloudera.parserchains.parsers.RenameFieldParser.CONFIG_TO_KEY;
import static com.cloudera.parserchains.parsers.RenameFieldParser.renameConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertEquals(input, output);
    }

    @Test
    void configure() {
        // rename 'original1' to 'new1'
        List<ConfigValue> values = Arrays.asList(
                ConfigValue.of(CONFIG_FROM_KEY, "original1"),
                ConfigValue.of(CONFIG_TO_KEY, "new1"));
        RenameFieldParser parser = new RenameFieldParser();
        parser.configure(renameConfig, values);
        assertEquals(FieldName.of("new1"), parser.getFieldsToRename().get(FieldName.of("original1")));
    }

    @Test
    void unexpectedConfig() {
        assertThrows(IllegalArgumentException.class,
                () -> new RenameFieldParser()
                        .configure(ConfigName.of("invalid", false), Collections.emptyList()));
    }
}
