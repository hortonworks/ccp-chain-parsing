package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.ConfigName;
import com.cloudera.parserchains.core.ConfigValue;
import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.MessageParser;
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
@MessageParser(name="Rename Field(s)", description="Renames a message field.")
public class RenameFieldParser implements Parser {

    // TODO how to communicate the necessary config values to the UI??
    static final String CONFIG_FROM_KEY = "from";
    static final String CONFIG_TO_KEY = "to";
    static ConfigName renameConfig = ConfigName.of("rename", true);
    private Map<FieldName, FieldName> fieldsToRename;

    public RenameFieldParser() {
        this.fieldsToRename = new HashMap<>();
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
    public List<ConfigName> validConfigurations() {
        return Arrays.asList(renameConfig);
    }

    @Override
    public void configure(ConfigName configName, List<ConfigValue> configValues) {
        if(renameConfig.equals(configName)) {
            FieldName from = null;
            FieldName to = null;
            for(ConfigValue value: configValues) {
                if(CONFIG_FROM_KEY.equals(value.getKey())) {
                    from = FieldName.of(value.getValue());
                } else if (CONFIG_TO_KEY.equals(value.getKey())) {
                    to = FieldName.of(value.getValue());
                }
            }
            if(from == null) {
                throw new IllegalArgumentException(String.format("Missing configuration value; key=%s", CONFIG_FROM_KEY));
            } else if(to == null) {
                throw new IllegalArgumentException(String.format("Missing configuration value; key=%s", CONFIG_TO_KEY));
            } else {
                renameField(from, to);
            }

        } else {
            throw new IllegalArgumentException(String.format("Unexpected configuration; name=%s", configName));
        }
    }
}
