package com.cloudera.parserchains.core;

import com.cloudera.parserchains.core.catalog.MessageParser;
import com.cloudera.parserchains.core.model.config.ConfigDescriptor;
import com.cloudera.parserchains.core.model.config.ConfigKey;
import com.cloudera.parserchains.core.model.config.ConfigName;
import com.cloudera.parserchains.core.model.config.ConfigValue;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@MessageParser(
        name = "Test Parser",
        description =  "This parser is used for testing only."
)
public class TestParser implements Parser {
    static ConfigKey inputFieldKey = ConfigKey.builder()
            .key("inputField")
            .label("Input Field")
            .description("The name of the input field to parse.")
            .build();
    static ConfigDescriptor inputFieldConfig = ConfigDescriptor
            .builder()
            .name("inputField")
            .description("Input Field")
            .acceptsValue(inputFieldKey)
            .isRequired(false)
            .build();
    private FieldName inputField;

    @Override
    public Message parse(Message message) {
        return message;
    }

    /**
     * @param inputField The name of the field containing the text to parse.
     */
    public TestParser withInputField(FieldName inputField) {
        this.inputField = Objects.requireNonNull(inputField);
        return this;
    }

    public FieldName getInputField() {
        return inputField;
    }

    @Override
    public List<ConfigDescriptor> validConfigurations() {
        return Collections.singletonList(inputFieldConfig);
    }

    @Override
    public void configure(ConfigName name, Map<ConfigKey, ConfigValue> values) {
        if(inputFieldConfig.getName().equals(name)) {
            configureInput(values);
        } else {
            throw new IllegalArgumentException(String.format("Unexpected configuration; name=%s", name));
        }
    }

    private void configureInput(Map<ConfigKey, ConfigValue> values) {
        Optional.ofNullable(values.get(inputFieldKey)).ifPresent(value -> {
            FieldName inputField = FieldName.of(value.get());
            withInputField(inputField);
        });
    }
}
