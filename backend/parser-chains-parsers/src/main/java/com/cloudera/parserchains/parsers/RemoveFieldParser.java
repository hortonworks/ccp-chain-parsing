package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.config.ConfigDescriptor;
import com.cloudera.parserchains.core.config.ConfigName;
import com.cloudera.parserchains.core.config.ConfigValues;
import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.catalog.MessageParser;
import com.cloudera.parserchains.core.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A parser which can remove fields from a message.
 */
@MessageParser(
    name="Remove Field(s)",
    description="Removes unwanted message field(s).")
public class RemoveFieldParser implements Parser {
    private List<FieldName> fieldsToRemove;
    private Configurer configurer;

    public RemoveFieldParser() {
        fieldsToRemove = new ArrayList<>();
        this.configurer = new Configurer(this);
    }

    public RemoveFieldParser removeField(FieldName fieldName) {
        fieldsToRemove.add(fieldName);
        return this;
    }

    @Override
    public Message parse(Message message) {
        return Message.builder()
                .withFields(message)
                .removeFields(fieldsToRemove)
                .build();
    }

    @Override
    public List<FieldName> outputFields() {
        return Collections.emptyList();
    }

    @Override
    public List<ConfigDescriptor> validConfigurations() {
        return configurer.validConfigurations();
    }

    @Override
    public void configure(ConfigName name, ConfigValues values) {
        configurer.configure(name, values);
    }

    List<FieldName> getFieldsToRemove() {
        return fieldsToRemove;
    }

    /**
     * Handles configuration for the {@link RemoveFieldParser}.
     */
    static class Configurer {
        static final ConfigDescriptor removeFieldConfig = ConfigDescriptor
                .builder()
                .name("Field to Remove")
                .description("The name of the field to remove.")
                .isRequired(true)
                .build();
        private RemoveFieldParser parser;

        Configurer(RemoveFieldParser parser) {
            this.parser = parser;
        }

        public List<ConfigDescriptor> validConfigurations() {
            return Arrays.asList(removeFieldConfig);
        }

        public void configure(ConfigName name, ConfigValues values) {
            if(removeFieldConfig.getName().equals(name)) {
                values.getValue().ifPresent(value -> {
                    FieldName field = FieldName.of(value.getValue());
                    parser.removeField(field);
                });
            } else {
                throw new IllegalArgumentException(String.format("Unexpected configuration; name=%s", name));
            }
        }
    }
}
