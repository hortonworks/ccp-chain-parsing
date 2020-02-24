package com.cloudera.parserchains.core;

import com.cloudera.parserchains.core.config.ConfigDescriptor;
import com.cloudera.parserchains.core.config.ConfigKey;
import com.cloudera.parserchains.core.config.ConfigName;
import com.cloudera.parserchains.core.config.ConfigValue;

import java.util.List;
import java.util.Map;

/**
 * Parses a {@link Message}.
 *
 */
public interface Parser {

    /**
     * Parse a {@link Message}.
     * @param message The message to parse.
     * @return A parsed message.
     */
    Message parse(Message message);

    /**
     * Returns the known output fields added to all parsed messages.  Not all parsers
     * are able to declare their known output fields.
     */
    List<FieldName> outputFields();

    /**
     * Allows a parser author to describe the configuration parameters required by a parser.
     * @return A list of the configuration elements expected by the parser.
     */
    List<ConfigDescriptor> validConfigurations();

    /**
     * Configure a parser.
     * <p>Expect this method to be called for each configuration parameter ({@link ConfigName})
     * accepted by the parser.
     * @param name The name of the configuration.
     * @param values The value(s) of the configuration element.
     */
    void configure(ConfigName name, Map<ConfigKey, ConfigValue> values);
}
