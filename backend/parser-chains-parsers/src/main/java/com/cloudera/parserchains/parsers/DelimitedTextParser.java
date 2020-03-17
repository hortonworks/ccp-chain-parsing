package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.Constants;
import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.FieldValue;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.Regex;
import com.cloudera.parserchains.core.catalog.MessageParser;
import com.cloudera.parserchains.core.model.config.ConfigDescriptor;
import com.cloudera.parserchains.core.model.config.ConfigKey;
import com.cloudera.parserchains.core.model.config.ConfigName;
import com.cloudera.parserchains.core.model.config.ConfigValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
            this.fieldName = Objects.requireNonNull(fieldName, "An output field name is required.");
            this.index = index;
        }
    }

    private FieldName inputField;
    private Regex delimiter;
    private List<OutputField> outputFields;
    private boolean trimWhitespace;
    private Configurer configurer;

    public DelimitedTextParser() {
        inputField = Constants.DEFAULT_INPUT_FIELD;
        outputFields = new ArrayList<>();
        delimiter = Regex.of(",");
        trimWhitespace = true;
        configurer = new Configurer(this);
    }

    /**
     * @param inputField The name of the field containing the text to parse.
     */
    public DelimitedTextParser withInputField(FieldName inputField) {
        this.inputField = Objects.requireNonNull(inputField, "An input field name is required.");
        return this;
    }

    public FieldName getInputField() {
        return inputField;
    }

    /**
     * @param delimiter A character or regular expression defining the delimiter used to split the text.
     */
    public DelimitedTextParser withDelimiter(Regex delimiter) {
        this.delimiter = Objects.requireNonNull(delimiter, "A valid delimited is required.");
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

    public List<OutputField> getOutputFields() {
        return Collections.unmodifiableList(outputFields);
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
        Message.Builder output = Message.builder().withFields(input);
        if(inputField == null) {
            output.withError("Input Field has not been defined.");

        } else if(!input.getField(inputField).isPresent()) {
            output.withError(format("Message missing expected input field '%s'", inputField.toString()));

        } else {
            input.getField(inputField).ifPresent(val -> doParse(val.toString(), output));
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
    public List<ConfigDescriptor> validConfigurations() {
        return configurer.validConfigurations();
    }

    @Override
    public void configure(ConfigName name, Map<ConfigKey, ConfigValue> values) {
        configurer.configure(name, values);
    }

    /**
     * Handles configuration for the {@link DelimitedTextParser}.
     */
    static class Configurer {
        // input field
        static final ConfigKey inputFieldKey = ConfigKey.builder()
                .key("inputField")
                .label("Input Field")
                .description("The name of the input field to parse.")
                .build();
        static final ConfigDescriptor inputFieldConfig = ConfigDescriptor
                .builder()
                .acceptsValue(inputFieldKey)
                .isRequired(true)
                .build();

        // output field
        static final ConfigKey outputFieldName = ConfigKey.builder()
                .key("fieldName")
                .label("Field Name")
                .description("The name of the output field.")
                .build();
        static final ConfigKey outputFieldIndex = ConfigKey.builder()
                .key("fieldIndex")
                .label("Column Index")
                .description("The index (0-based) of the column containing the data.")
                .build();
        static final ConfigDescriptor outputFieldConfig = ConfigDescriptor
                .builder()
                .name("outputField")
                .description("Output Field")
                .isRequired(true)
                .isCumulative(true)
                .acceptsValue(outputFieldIndex)
                .acceptsValue(outputFieldName)
                .build();

        // delimiter
        static final ConfigKey delimiterKey = ConfigKey.builder()
                .key("delimiter")
                .label("Delimiter")
                .description("A regex delimiter used to split the text. Defaults to comma.")
                .build();
        static final ConfigDescriptor delimiterConfig = ConfigDescriptor
                .builder()
                .acceptsValue(delimiterKey)
                .isRequired(false)
                .build();

        // trim
        static final ConfigKey trimKey = ConfigKey.builder()
                .key("trim")
                .label("Trim Whitespace")
                .description("Trim whitespace from each value. Defaults to true.")
                .build();
        static final ConfigDescriptor trimConfig = ConfigDescriptor
                .builder()
                .acceptsValue(trimKey)
                .isRequired(false)
                .build();

        private DelimitedTextParser parser;

        Configurer(DelimitedTextParser parser) {
            this.parser = parser;
        }

        List<ConfigDescriptor> validConfigurations() {
            return Arrays.asList(inputFieldConfig, outputFieldConfig, delimiterConfig, trimConfig);
        }

        void configure(ConfigName name, Map<ConfigKey, ConfigValue> values) {
            if(inputFieldConfig.getName().equals(name)) {
                configureInput(values);
            } else if(outputFieldConfig.getName().equals(name)) {
                configureOutput(values);
            } else if(delimiterConfig.getName().equals(name)) {
                configureDelimiter(values);
            } else if(trimConfig.getName().equals(name)) {
                configureTrim(values);
            } else {
                throw new IllegalArgumentException(String.format("Unexpected configuration; name=%s", name.get()));
            }
        }

        private void configureTrim(Map<ConfigKey, ConfigValue> values) {
            Optional.ofNullable(values.get(trimKey)).ifPresent(value -> {
                boolean trim = Boolean.valueOf(value.get());
                parser.trimWhitespace(trim);
            });
        }

        private void configureDelimiter(Map<ConfigKey, ConfigValue> values) {
            Optional.ofNullable(values.get(delimiterKey)).ifPresent(value -> {
                Regex delimiter = Regex.of(value.get());
                parser.withDelimiter(delimiter);
            });
        }

        private void configureInput(Map<ConfigKey, ConfigValue> values) {
            Optional.ofNullable(values.get(inputFieldKey)).ifPresent(value -> {
                FieldName inputField = FieldName.of(value.get());
                parser.withInputField(inputField);
            });
        }

        private void configureOutput(Map<ConfigKey, ConfigValue> values) {
            FieldName outputField = Optional.ofNullable(values.get(outputFieldName))
                    .map(value -> FieldName.of(value.get()))
                    .orElseThrow(() -> missingConfig(outputFieldName));
            Integer columnIndex = Optional.ofNullable(values.get(outputFieldIndex))
                    .map(value -> Integer.parseInt(value.get()))
                    .orElseThrow(() -> missingConfig(outputFieldIndex));
            parser.withOutputField(outputField, columnIndex);
        }

        private IllegalArgumentException missingConfig(ConfigKey missing) {
            String error = String.format("No value defined for %s - %s",
                    outputFieldConfig.getDescription().get(), missing.getLabel());
            return new IllegalArgumentException(error);
        }
    }
}
