package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.FieldValue;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.Regex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DelimitedTextParserTest {

    @Test
    void parseCSV() {
        FieldValue csvToParse = FieldValue.of("value1, value2, value3, value4");
        Message input = Message.builder()
                .addField(FieldName.of("input"), csvToParse)
                .build();

        // parse the message
        Message output = new DelimitedTextParser()
                .withDelimiter(Regex.of(","))
                .withInputField(FieldName.of("input"))
                .withOutputField(FieldName.of("first"), 0)
                .withOutputField(FieldName.of("second"), 1)
                .withOutputField(FieldName.of("third"), 2)
                .parse(input);

        // input field should still exist
        assertEquals(csvToParse, output.getField(FieldName.of("input")).get());

        // new output fields
        assertEquals(FieldValue.of("value1"), output.getField(FieldName.of("first")).get());
        assertEquals(FieldValue.of("value2"), output.getField(FieldName.of("second")).get());
        assertEquals(FieldValue.of("value3"), output.getField(FieldName.of("third")).get());

        // no errors
        assertFalse(output.getError().isPresent());
    }

    @Test
    void parseTSV() {
        FieldValue tsvToParse = FieldValue.of("value1\t value2\t value3\t value4");
        Message input = Message.builder()
                .addField(FieldName.of("input"), tsvToParse)
                .build();

        // parse the message
        Message output = new DelimitedTextParser()
                .withDelimiter(Regex.of("\t"))
                .withInputField(FieldName.of("input"))
                .withOutputField(FieldName.of("first"), 0)
                .withOutputField(FieldName.of("second"), 1)
                .withOutputField(FieldName.of("third"), 2)
                .parse(input);

        // input field should still exist
        assertEquals(tsvToParse, output.getField(FieldName.of("input")).get());

        // new output fields
        assertEquals(FieldValue.of("value1"), output.getField(FieldName.of("first")).get());
        assertEquals(FieldValue.of("value2"), output.getField(FieldName.of("second")).get());
        assertEquals(FieldValue.of("value3"), output.getField(FieldName.of("third")).get());

        // no errors
        assertFalse(output.getError().isPresent());
    }

    @Test
    void missingInputField() {
        Message input = Message.builder()
                .build();

        // parse the message
        Message output = new DelimitedTextParser()
                .withDelimiter(Regex.of(","))
                .withInputField(FieldName.of("input"))
                .withOutputField(FieldName.of("first"), 0)
                .parse(input);

        // expect an error
        assertTrue(output.getError().isPresent());
    }

    @Test
    void missingOutputField() {
        FieldValue csvToParse = FieldValue.of("value1, value2, value3, value4");
        Message input = Message.builder()
                .addField(FieldName.of("input"), csvToParse)
                .build();

        // there are only 4 fields in the CSV, but trying to use an index of 10
        int index = 10;
        Message output = new DelimitedTextParser()
                .withDelimiter(Regex.of(","))
                .withInputField(FieldName.of("input"))
                .withOutputField(FieldName.of("first"), index)
                .parse(input);

        // input field should still exist
        assertEquals(csvToParse, output.getField(FieldName.of("input")).get());

        // expect an error
        assertTrue(output.getError().isPresent());
    }

    @Test
    void emptyInputField() {
        // the input field is empty
        FieldValue csvToParse = FieldValue.of("");
        Message input = Message.builder()
                .addField(FieldName.of("input"), csvToParse)
                .build();

        // parse the message
        Message output = new DelimitedTextParser()
                .withDelimiter(Regex.of(","))
                .withInputField(FieldName.of("input"))
                .withOutputField(FieldName.of("first"), 0)
                .withOutputField(FieldName.of("second"), 1)
                .withOutputField(FieldName.of("third"), 2)
                .parse(input);

        // input field should still exist
        assertEquals(csvToParse, output.getField(FieldName.of("input")).get());

        // expect an error
        assertTrue(output.getError().isPresent());
    }

    @Test
    void noWhitespaceTrim() {
        FieldValue csvToParse = FieldValue.of(" value1, value2, value3, value4");
        Message input = Message.builder()
                .addField(FieldName.of("input"), csvToParse)
                .build();

        // parse the message
        Message output = new DelimitedTextParser()
                .trimWhitespace(false)
                .withDelimiter(Regex.of(","))
                .withInputField(FieldName.of("input"))
                .withOutputField(FieldName.of("first"), 0)
                .withOutputField(FieldName.of("second"), 1)
                .withOutputField(FieldName.of("third"), 2)
                .parse(input);

        // input field should still exist
        assertEquals(csvToParse, output.getField(FieldName.of("input")).get());

        // new output fields
        assertEquals(FieldValue.of(" value1"), output.getField(FieldName.of("first")).get());
        assertEquals(FieldValue.of(" value2"), output.getField(FieldName.of("second")).get());
        assertEquals(FieldValue.of(" value3"), output.getField(FieldName.of("third")).get());

        // no errors
        assertFalse(output.getError().isPresent());
    }

    @Test
    void inputFieldNotDefined() {
        Message input = Message.builder().build();
        assertThrows(IllegalStateException.class, () -> new DelimitedTextParser().parse(input));
    }

    @Test
    void outputFieldNotDefined() {
        Message input = Message.builder()
                .build();
        Message output = new DelimitedTextParser()
                .withInputField(FieldName.of("input"))
                .withDelimiter(Regex.of(","))
                .parse(input);

        // expect an error
        assertTrue(output.getError().isPresent());
    }
}

