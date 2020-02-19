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

        assertEquals(csvToParse, output.getField(FieldName.of("input")).get(),
            "Expected the 'input' field to remain in the output message.");
        assertEquals(FieldValue.of("value1"), output.getField(FieldName.of("first")).get(),
            "Expected the 'first' field to have been added to the message.");
        assertEquals(FieldValue.of("value2"), output.getField(FieldName.of("second")).get(),
            "Expected the 'second' field to have been added to the message.");
        assertEquals(FieldValue.of("value3"), output.getField(FieldName.of("third")).get(),
            "Expected the 'third' field to have been added to the message.");
        assertFalse(output.getError().isPresent(),
            "Expected no parsing errors.");
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

        assertEquals(tsvToParse, output.getField(FieldName.of("input")).get(),
            "Expected the 'input' field to remain in the output message.");
        assertEquals(FieldValue.of("value1"), output.getField(FieldName.of("first")).get(),
            "Expected the 'first' field to have been added to the message.");
        assertEquals(FieldValue.of("value2"), output.getField(FieldName.of("second")).get(),
            "Expected the 'second' field to have been added to the message.");
        assertEquals(FieldValue.of("value3"), output.getField(FieldName.of("third")).get(),
            "Expected the 'third' field to have been added to the message.");
        assertFalse(output.getError().isPresent(),
            "Expected no parsing errors.");
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

        assertTrue(output.getError().isPresent(),
            "Expected a parsing error because there is no 'input' field to parse.");
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

        assertEquals(csvToParse, output.getField(FieldName.of("input")).get(),
            "Expected the 'input' field to remain in the output message.");
        assertTrue(output.getError().isPresent(),
            "Expected a parsing error because column '10' does not exist in the data.");
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

        assertEquals(csvToParse, output.getField(FieldName.of("input")).get(),
            "Expected the 'input' field to remain in the output message.");
        assertTrue(output.getError().isPresent(),
            "Expected a parsing error because the input field is empty.");
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

        assertEquals(csvToParse, output.getField(FieldName.of("input")).get(),
            "Expected the 'input' field to remain in the output message.");
        assertEquals(FieldValue.of(" value1"), output.getField(FieldName.of("first")).get(),
            "Expected the 'first' field to have been added to the message.");
        assertEquals(FieldValue.of(" value2"), output.getField(FieldName.of("second")).get(),
            "Expected the 'second' field to have been added to the message.");
        assertEquals(FieldValue.of(" value3"), output.getField(FieldName.of("third")).get(),
            "Expected the 'third' field to have been added to the message.");
        assertFalse(output.getError().isPresent(),
            "Expected no parsing errors.");
    }

    @Test
    void inputFieldNotDefined() {
        Message input = Message.builder().build();
        assertThrows(IllegalStateException.class, () -> new DelimitedTextParser().parse(input),
            "Expected parsing error because no input field has been defined.");
    }

    @Test
    void outputFieldNotDefined() {
        Message input = Message.builder()
                .build();
        Message output = new DelimitedTextParser()
                .withInputField(FieldName.of("input"))
                .withDelimiter(Regex.of(","))
                .parse(input);
        assertTrue(output.getError().isPresent(),
            "Expected parsing error because no output field(s) have been defined");
    }
}

