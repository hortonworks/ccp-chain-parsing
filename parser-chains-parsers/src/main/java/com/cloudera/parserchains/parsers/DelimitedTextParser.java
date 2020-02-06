package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.ConfigName;
import com.cloudera.parserchains.core.ConfigValue;
import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.FieldValue;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.MessageParser;
import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.Regex;

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
@MessageParser(name="Delimited Text", description="Parses delimited text like CSV or TSV.")
public class DelimitedTextParser implements Parser {
    static final String CONFIG_KEY_LABEL = "label";
    static final String CONFIG_KEY_INDEX = "index";

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

    public static final ConfigName inputConfig = ConfigName.of("input", true);
    public static final ConfigName outputConfig = ConfigName.of("output", true);
    public static final ConfigName delimiterConfig = ConfigName.of("delimiter", false);
    public static final ConfigName trimConfig = ConfigName.of("trim", false);

    private FieldName inputField;
    private Regex delimiter;
    private List<OutputField> outputFields;
    private boolean trimWhitespace;

    public DelimitedTextParser() {
        outputFields = new ArrayList<>();
        delimiter = Regex.of(",");
        trimWhitespace = true;
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
    public List<ConfigName> validConfigurations() {
        return Arrays.asList(inputConfig, outputConfig, delimiterConfig, trimConfig);
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
    public void configure(ConfigName configName, List<ConfigValue> configValues) {
        if(inputConfig.equals(configName)) {
            configureInput(configValues);

        } else if(outputConfig.equals(configName)) {
            configureOutput(configValues);

        } else if(delimiterConfig.equals(configName)) {
            configureDelimiter(configValues);

        } else if(trimConfig.equals(configName)) {
            configureTrim(configValues);

        } else {
            throw new IllegalArgumentException(String.format("Unexpected configuration; name=%s", configName));
        }
    }

    private void configureTrim(List<ConfigValue> configValues) {
        // set the whitespace trim
        ParserUtils.requireN(trimConfig, configValues, 1);
        ConfigValue value1 = configValues.get(0);
        trimWhitespace(Boolean.valueOf(value1.getValue()));
    }

    private void configureDelimiter(List<ConfigValue> configValues) {
        // set the delimiter
        ParserUtils.requireN(delimiterConfig, configValues, 1);
        ConfigValue value1 = configValues.get(0);
        withDelimiter(Regex.of(value1.getValue()));
    }

    private void configureInput(List<ConfigValue> configValues) {
        ParserUtils.requireN(inputConfig, configValues, 1);
        ConfigValue value1 = configValues.get(0);
        withInputField(FieldName.of(value1.getValue()));
    }

    private void configureOutput(List<ConfigValue> configValues) {
        ParserUtils.requireN(outputConfig, configValues, 2);

        int index = -1;
        FieldName outputField = null;
        for(ConfigValue value: configValues) {
            if(CONFIG_KEY_LABEL.equals(value.getKey())) {
                outputField = FieldName.of(value.getValue());
            } else if(CONFIG_KEY_INDEX.equals(value.getKey())) {
                index = Integer.parseInt(value.getValue());
            } else {
                throw new IllegalArgumentException(String.format("Unexpected config key: %s", value.getKey()));
            }
        }

        if(outputField != null && index != -1) {
            withOutputField(outputField, index);
        } else {
            throw new IllegalArgumentException(String.format("For '%s' expected a value for both 'label' and 'index'",
                    outputConfig.getName()));
        }
    }
}
