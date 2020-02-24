package com.cloudera.parserchains.core;

import com.cloudera.parserchains.core.config.ConfigDescriptor;
import com.cloudera.parserchains.core.config.ConfigName;
import com.cloudera.parserchains.core.config.ConfigValues;

import java.util.Collections;
import java.util.List;

/**
 * A Parser that does nothing.
 */
public class NoopParser implements Parser {

    @Override
    public Message parse(Message message) {
        // nothing to do
        return message;
    }

    @Override
    public List<FieldName> outputFields() {
        // nothing to do
        return Collections.emptyList();
    }

    @Override
    public List<ConfigDescriptor> validConfigurations() {
        // nothing to do
        return null;
    }

    @Override
    public void configure(ConfigName name, ConfigValues values) {
        // nothing to do
    }
}
