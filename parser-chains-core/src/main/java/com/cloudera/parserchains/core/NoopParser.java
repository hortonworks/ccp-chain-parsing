package com.cloudera.parserchains.core;

import java.util.Collections;
import java.util.List;

/**
 * A Parser that does nothing.
 */
public class NoopParser implements Parser {

    @Override
    public Message parse(Message message) {
        // do nothing
        return message;
    }

    @Override
    public List<FieldName> outputFields() {
        // do nothing
        return Collections.emptyList();
    }

    @Override
    public List<ConfigName> validConfigurations() {
        // do nothing
        return Collections.emptyList();
    }

    @Override
    public void configure(ConfigName configName, List<ConfigValue> configValues) {
        // do nothing
    }
}
