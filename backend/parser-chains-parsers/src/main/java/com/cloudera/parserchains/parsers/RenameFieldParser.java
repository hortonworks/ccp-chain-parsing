package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.config.ConfigDescriptor;
import com.cloudera.parserchains.core.config.ConfigName;
import com.cloudera.parserchains.core.config.ConfigKey;
import com.cloudera.parserchains.core.config.ConfigValues;
import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.catalog.MessageParser;
import com.cloudera.parserchains.core.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<FieldName> outputFields() {
        return new ArrayList<>(fieldsToRename.keySet());
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
     * Handles configuration for the {@link RenameFieldParser}.
     */
    static class Configurer {
        static final String FROM_FIELD = "from";
        static final String TO_FIELD = "to";
        static final ConfigDescriptor renameFieldConfig = ConfigDescriptor
                .builder()
                .name("Field to Rename")
                .description("The field(s) to rename.")
                .isRequired(true)
                .requiresValue(FROM_FIELD, "The original name of the field.")
                .requiresValue(TO_FIELD, "The new name of the field.")
                .build();
        private RenameFieldParser parser;

        Configurer(RenameFieldParser parser) {
            this.parser = parser;
        }

        List<ConfigDescriptor> validConfigurations() {
            return Arrays.asList(renameFieldConfig);
        }

        void configure(ConfigName name, ConfigValues values) {
            if(renameFieldConfig.getName().equals(name)) {
                configureRenameField(values);
            } else {
                throw new IllegalArgumentException(String.format("Unexpected configuration; name=%s", name));
            }
        }

        private void configureRenameField(ConfigValues values) {
            FieldName from = values
                    .getValue(ConfigKey.of(FROM_FIELD))
                    .map(value -> FieldName.of(value.getValue()))
                    .orElseThrow(() -> missingConfig(FROM_FIELD));
            FieldName to = values
                    .getValue(ConfigKey.of(TO_FIELD))
                    .map(value -> FieldName.of(value.getValue()))
                    .orElseThrow(() -> missingConfig(TO_FIELD));
            parser.renameField(from, to);
        }

        private IllegalArgumentException missingConfig(String missing) {
            String error = String.format("No value defined for %s.%s", renameFieldConfig.getName(), missing);
            return new IllegalArgumentException(error);
        }
    }
}
