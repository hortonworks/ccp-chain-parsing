package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.catalog.MessageParser;
import com.cloudera.parserchains.core.model.config.ConfigDescriptor;
import com.cloudera.parserchains.core.model.config.ConfigKey;
import com.cloudera.parserchains.core.model.config.ConfigName;
import com.cloudera.parserchains.core.model.config.ConfigValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public List<ConfigDescriptor> validConfigurations() {
        return configurer.validConfigurations();
    }

    @Override
    public void configure(ConfigName name, Map<ConfigKey, ConfigValue> values) {
        configurer.configure(name, values);
    }

    List<FieldName> getFieldsToRemove() {
        return fieldsToRemove;
    }

    /**
     * Handles configuration for the {@link RemoveFieldParser}.
     */
    static class Configurer {
        static final ConfigKey removeFieldKey = ConfigKey.builder()
                .key("fieldToRemove")
                .label("Field to Remove")
                .description("The name of the field to remove.")
                .build();
        static final ConfigDescriptor removeFieldConfig = ConfigDescriptor
                .builder()
                .name("fieldToRemove")
                .description("Field to Remove")
                .acceptsValue(removeFieldKey)
                .isRequired(true)
                .build();
        private RemoveFieldParser parser;

        Configurer(RemoveFieldParser parser) {
            this.parser = parser;
        }

        public List<ConfigDescriptor> validConfigurations() {
            return Arrays.asList(removeFieldConfig);
        }

        public void configure(ConfigName name, Map<ConfigKey, ConfigValue> values) {
            if(removeFieldConfig.getName().equals(name)) {
                configureRemoveField(values);
            } else {
                throw new IllegalArgumentException(String.format("Unexpected configuration; name=%s", name));
            }
        }

        private void configureRemoveField(Map<ConfigKey, ConfigValue> values) {
            Optional.ofNullable(values.get(removeFieldKey)).ifPresent(value -> {
                FieldName field = FieldName.of(value.get());
                parser.removeField(field);
            });
        }
    }
}
