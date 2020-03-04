package com.cloudera.parserchains.core;

import com.cloudera.parserchains.core.config.ConfigDescriptor;
import com.cloudera.parserchains.core.config.ConfigKey;
import com.cloudera.parserchains.core.config.ConfigName;
import com.cloudera.parserchains.core.config.ConfigValue;

import java.util.List;
import java.util.Map;

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
    public List<ConfigDescriptor> validConfigurations() {
        // nothing to do
        return null;
    }

    @Override
    public void configure(ConfigName name, Map<ConfigKey, ConfigValue> values) {
        // nothing to do
    }
}
