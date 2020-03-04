package com.cloudera.parserchains.queryservice.service;

import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.catalog.MessageParser;
import com.cloudera.parserchains.core.config.ConfigDescriptor;
import com.cloudera.parserchains.core.config.ConfigKey;
import com.cloudera.parserchains.core.config.ConfigName;
import com.cloudera.parserchains.core.config.ConfigValue;

import java.util.List;
import java.util.Map;

/**
 * This parser is used for testing only.  This will throw an unchecked exception, something that
 * parsers should NOT do.
 */
@MessageParser(
        name = "Misbehaving Parser",
        description = "A parser used for testing only.")
public class MisbehavingParser implements Parser {

    @Override
    public Message parse(Message message) {
        throw new RuntimeException("No parser should throw unchecked exceptions, but what if?");
    }

    @Override
    public List<ConfigDescriptor> validConfigurations() {
        // do nothing
        return null;
    }

    @Override
    public void configure(ConfigName name, Map<ConfigKey, ConfigValue> values) {
        // do nothing
    }
}
