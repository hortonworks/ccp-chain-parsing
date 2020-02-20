package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.FieldValue;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.Regex;
import com.cloudera.parserchains.core.config.ConfigValues;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.cloudera.parserchains.parsers.DelimitedTextParser.Configurer.OUTPUT_FIELD_INDEX;
import static com.cloudera.parserchains.parsers.DelimitedTextParser.Configurer.OUTPUT_FIELD_NAME;
import static com.cloudera.parserchains.parsers.DelimitedTextParser.Configurer.delimiterConfig;
import static com.cloudera.parserchains.parsers.DelimitedTextParser.Configurer.inputFieldConfig;
import static com.cloudera.parserchains.parsers.DelimitedTextParser.Configurer.outputFieldConfig;
import static com.cloudera.parserchains.parsers.DelimitedTextParser.Configurer.trimConfig;
import static com.cloudera.parserchains.parsers.DelimitedTextParser.OutputField;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
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

        // expect an error
        assertTrue(output.getError().isPresent(),
                "Expected parsing error because no output field(s) have been defined");
    }

    @Test
    void configureInputField() {
        ConfigValues inputField = ConfigValues.builder()
                .withValue("original_string")
                .build();
        DelimitedTextParser parser = new DelimitedTextParser();
        parser.configure(inputFieldConfig.getName(), inputField);
        assertEquals(FieldName.of("original_string"), parser.getInputField());
    }

    @Test
    void configureOutputFields() {
        // define the output field values
        ConfigValues outputField1 = ConfigValues.builder()
                .withValue(OUTPUT_FIELD_NAME, "first")
                .withValue(OUTPUT_FIELD_INDEX, "0")
                .build();
        ConfigValues outputField2 = ConfigValues.builder()
                .withValue(OUTPUT_FIELD_NAME, "second")
                .withValue(OUTPUT_FIELD_INDEX, "1")
                .build();
        ConfigValues outputField3 = ConfigValues.builder()
                .withValue(OUTPUT_FIELD_NAME, "third")
                .withValue(OUTPUT_FIELD_INDEX, "2")
                .build();

        DelimitedTextParser parser = new DelimitedTextParser();
        parser.configure(outputFieldConfig.getName(), outputField1);
        parser.configure(outputFieldConfig.getName(), outputField2);
        parser.configure(outputFieldConfig.getName(), outputField3);

        List<OutputField> outputFields = parser.getOutputFields();
        assertEquals(3, outputFields.size());

        // first output field
        assertEquals(FieldName.of("first"), outputFields.get(0).fieldName);
        assertEquals(0, outputFields.get(0).index);

        // second output field
        assertEquals(FieldName.of("second"), outputFields.get(1).fieldName);
        assertEquals(1, outputFields.get(1).index);

        // third output field
        assertEquals(FieldName.of("third"), outputFields.get(2).fieldName);
        assertEquals(2, outputFields.get(2).index);
    }

    @Test
    void configureDelimiter() {
        ConfigValues delimiter = ConfigValues.builder()
                .withValue("|")
                .build();
        DelimitedTextParser parser = new DelimitedTextParser();
        parser.configure(delimiterConfig.getName(), delimiter);
        assertEquals(Regex.of("|"), parser.getDelimiter());
    }

    @Test
    void configureTrim() {
        ConfigValues trim = ConfigValues.builder()
                .withValue("false")
                .build();
        DelimitedTextParser parser = new DelimitedTextParser();
        parser.configure(trimConfig.getName(), trim);
        assertFalse(parser.isTrimWhitespace());
    }

    @Test
    void validConfigurations() {
        DelimitedTextParser parser = new DelimitedTextParser();
        assertThat(parser.validConfigurations(),
                hasItems(inputFieldConfig, outputFieldConfig, delimiterConfig, trimConfig));
    }
}

