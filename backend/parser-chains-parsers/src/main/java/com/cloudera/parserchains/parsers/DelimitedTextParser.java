package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.FieldValue;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.Regex;
import com.cloudera.parserchains.core.catalog.MessageParser;
import com.cloudera.parserchains.core.config.ConfigDescriptor;
import com.cloudera.parserchains.core.config.ConfigKey;
import com.cloudera.parserchains.core.config.ConfigName;
import com.cloudera.parserchains.core.config.ConfigValues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Parses delimited text like CSV.
 */
@MessageParser(
    name="Delimited Text",
    description="Parses delimited text like CSV or TSV.")
public class DelimitedTextParser implements Parser {

    /**
     * Defines an output field that is created by the parser.
     */
    static class OutputField {
        FieldName fieldName;
        int index;

        OutputField(FieldName fieldName, int index) {
            this.fieldName = Objects.requireNonNull(fieldName);
            this.index = index;
        }
    }

    private FieldName inputField;
    private Regex delimiter;
    private List<OutputField> outputFields;
    private boolean trimWhitespace;
    private Configurer configurer;

    public DelimitedTextParser() {
        outputFields = new ArrayList<>();
        delimiter = Regex.of(",");
        trimWhitespace = true;
        configurer = new Configurer(this);
    }

    /**
     * @param inputField The name of the field containing the text to parse.
     */
    public DelimitedTextParser withInputField(FieldName inputField) {
        this.inputField = Objects.requireNonNull(inputField);
        return this;
    }

    public FieldName getInputField() {
        return inputField;
    }

    /**
     * @param delimiter A character or regular expression defining the delimiter used to split the text.
     */
    public DelimitedTextParser withDelimiter(Regex delimiter) {
        this.delimiter = Objects.requireNonNull(delimiter);
        return this;
    }

    public Regex getDelimiter() {
        return delimiter;
    }

    /**
     * @param fieldName The name of a field to create.
     * @param index The 0-based index defining which delimited element is added to the field.
     */
    public DelimitedTextParser withOutputField(FieldName fieldName, int index) {
        outputFields.add(new OutputField(fieldName, index));
        return this;
    }

    List<OutputField> getOutputFields() {
        return outputFields;
    }

    /**
     * @param trimWhitespace True, if whitespace should be trimmed from each value. Otherwise, false.
     */
    public DelimitedTextParser trimWhitespace(boolean trimWhitespace) {
        this.trimWhitespace = trimWhitespace;
        return this;
    }

    public boolean isTrimWhitespace() {
        return trimWhitespace;
    }


    @Override
    public Message parse(Message input) {
        if(inputField == null) {
            throw new IllegalStateException("Input field has not been defined.");
        }
        Message.Builder output = Message.builder().withFields(input);
        Optional<FieldValue> fieldValue = input.getField(inputField);
        if(fieldValue.isPresent()) {
            doParse(fieldValue.get().toString(), output);
        } else {
            output.withError(format("Message does not contain input field '%s'", inputField.toString()));
        }

        return output.build();
    }

    private void doParse(String valueToParse, Message.Builder output) {
        String[] columns = valueToParse.split(delimiter.toString());
        int width = columns.length;
        for(OutputField outputField : outputFields) {
            if(width > outputField.index) {
                // create a new output field
                String column = columns[outputField.index];
                if(trimWhitespace) {
                    column = column.trim();
                }
                output.addField(outputField.fieldName, FieldValue.of(column));

            } else {
                String err = format("Found %d column(s), index %d does not exist.", width, outputField.index);
                output.withError(err);
            }
        }
    }

    @Override
    public List<FieldName> outputFields() {
        return outputFields
                .stream()
                .map(outputField -> outputField.fieldName)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConfigDescriptor> validConfigurations() {
        return configurer.validConfigurations();
    }

    @Override
    public void configure(ConfigName name, ConfigValues values) {
        configurer.configure(name, values);
    }

    /**
     * Handles configuration for the {@link DelimitedTextParser}.
     */
    static class Configurer {
        static final String OUTPUT_FIELD_NAME = "fieldName";
        static final String OUTPUT_FIELD_INDEX = "fieldIndex";
        static final ConfigDescriptor inputFieldConfig = ConfigDescriptor
                .builder()
                .name("inputField")
                .description("The name of the input field to parse.")
                .isRequired(true)
                .build();
        static final ConfigDescriptor outputFieldConfig = ConfigDescriptor
                .builder()
                .name("outputField")
                .description("An output field created by the parser.")
                .isRequired(true)
                .requiresValue(OUTPUT_FIELD_INDEX, "The name of the output field.")
                .requiresValue(OUTPUT_FIELD_NAME, "The column index containing the data for an output field.")
                .build();
        static final ConfigDescriptor delimiterConfig = ConfigDescriptor
                .builder()
                .name("delimiter")
                .description("A regex delimiter used to split the text. Defaults to comma.")
                .isRequired(false)
                .build();
        static final ConfigDescriptor trimConfig = ConfigDescriptor
                .builder()
                .name("trim")
                .description("Trim whitespace from each value. Defaults to true.")
                .isRequired(false)
                .build();
        private DelimitedTextParser parser;

        Configurer(DelimitedTextParser parser) {
            this.parser = parser;
        }

        List<ConfigDescriptor> validConfigurations() {
            return Arrays.asList(inputFieldConfig, outputFieldConfig, delimiterConfig, trimConfig);
        }

        void configure(ConfigName name, ConfigValues values) {
            if(inputFieldConfig.getName().equals(name)) {
                configureInput(values);

            } else if(outputFieldConfig.getName().equals(name)) {
                configureOutput(values);

            } else if(delimiterConfig.getName().equals(name)) {
                configureDelimiter(values);

            } else if(trimConfig.getName().equals(name)) {
                configureTrim(values);

            } else {
                throw new IllegalArgumentException(String.format("Unexpected configuration; name=%s", name));
            }
        }

        private void configureTrim(ConfigValues values) {
            values.getValue().ifPresent(value -> {
                boolean trim = Boolean.valueOf(value.getValue());
                parser.trimWhitespace(trim);
            });
        }

        private void configureDelimiter(ConfigValues values) {
            values.getValue().ifPresent(value -> {
                Regex delimiter = Regex.of(value.getValue());
                parser.withDelimiter(delimiter);
            });
        }

        private void configureInput(ConfigValues values) {
            values.getValue().ifPresent(value -> {
                FieldName inputField = FieldName.of(value.getValue());
                parser.withInputField(inputField);
            });
        }

        private void configureOutput(ConfigValues values) {
            FieldName outputField = values
                    .getValue(ConfigKey.of(OUTPUT_FIELD_NAME))
                    .map(value -> FieldName.of(value.getValue()))
                    .orElseThrow(() -> missingConfig(ConfigKey.of(OUTPUT_FIELD_NAME)));
            Integer columnIndex = values
                    .getValue(ConfigKey.of(OUTPUT_FIELD_INDEX))
                    .map(value -> Integer.parseInt(value.getValue()))
                    .orElseThrow(() -> missingConfig(ConfigKey.of(OUTPUT_FIELD_INDEX)));
            parser.withOutputField(outputField, columnIndex);
        }

        private IllegalArgumentException missingConfig(ConfigKey missing) {
            String error = String.format("No value defined for %s.%s", outputFieldConfig.getName(), missing.getKey());
            return new IllegalArgumentException(error);
        }
    }
}
