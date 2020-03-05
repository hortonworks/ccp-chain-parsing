package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.catalog.MessageParser;
import com.cloudera.parserchains.core.config.ConfigDescriptor;
import com.cloudera.parserchains.core.config.ConfigKey;
import com.cloudera.parserchains.core.config.ConfigName;
import com.cloudera.parserchains.core.config.ConfigValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A parser that can rename message fields.
 */
@MessageParser(
    name="Rename Field(s)", 
    description="Renames message field(s).")
public class RenameFieldParser implements Parser {
    private Map<FieldName, FieldName> fieldsToRename;
    private Configurer configurer;

    public RenameFieldParser() {
        this.fieldsToRename = new HashMap<>();
        this.configurer = new Configurer(this);
    }

    /**
     * Configure the parser to rename a field.
     * @param from The original field name.
     * @param to The new field name.
     */
    public RenameFieldParser renameField(FieldName from, FieldName to) {
        fieldsToRename.put(from, to);
        return this;
    }

    Map<FieldName, FieldName> getFieldsToRename() {
        return Collections.unmodifiableMap(fieldsToRename);
    }

    @Override
    public Message parse(Message input) {
        Message.Builder output = Message.builder()
                .withFields(input);
        fieldsToRename.forEach((from, to) -> output.renameField(from, to));
        return output.build();
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
     * Handles configuration for the {@link RenameFieldParser}.
     */
    static class Configurer {
        static final ConfigKey fromFieldKey = ConfigKey.builder()
                .key("from")
                .label("Rename From")
                .description("The original name of the field to rename.")
                .build();
        static final ConfigKey toFieldKey = ConfigKey.builder()
                .key("to")
                .label("Rename To")
                .description("The new name of the field.")
                .build();
        static final ConfigDescriptor renameFieldConfig = ConfigDescriptor
                .builder()
                .name("fieldToRename")
                .description("Field to Rename")
                .isRequired(true)
                .acceptsValue(fromFieldKey)
                .acceptsValue(toFieldKey)
                .build();
        private RenameFieldParser parser;

        Configurer(RenameFieldParser parser) {
            this.parser = parser;
        }

        List<ConfigDescriptor> validConfigurations() {
            return Arrays.asList(renameFieldConfig);
        }

        void configure(ConfigName name, Map<ConfigKey, ConfigValue> values) {
            if(renameFieldConfig.getName().equals(name)) {
                configureRenameField(values);
            } else {
                throw new IllegalArgumentException(String.format("Unexpected configuration; name=%s", name));
            }
        }

        private void configureRenameField(Map<ConfigKey, ConfigValue> values) {
            FieldName from = Optional.ofNullable(values.get(fromFieldKey))
                    .map(value -> FieldName.of(value.get()))
                    .orElseThrow(() -> missingConfig(fromFieldKey));
            FieldName to = Optional.ofNullable((values.get(toFieldKey)))
                    .map(value -> FieldName.of(value.get()))
                    .orElseThrow(() -> missingConfig(toFieldKey));
            parser.renameField(from, to);
        }

        private IllegalArgumentException missingConfig(ConfigKey missing) {
            String error = String.format("No value defined for %s.%s", renameFieldConfig.getName(), missing.getKey());
            return new IllegalArgumentException(error);
        }
    }
}
