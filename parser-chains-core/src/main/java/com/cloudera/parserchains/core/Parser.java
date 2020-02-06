package com.cloudera.parserchains.core;

import java.util.List;

/**
 * Parses a {@link Message}.
 *
 * <p>Implementers of this interface should also mark their implementation using
 * the {@link MessageParser} annotation so that the parser is discoverable
 * using a {@link ParserCatalog}.
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
     *
     * TODO should this just be removed?  The user will know the output fields when a message is parsed.
     */
    List<FieldName> outputFields();

    /**
     * Returns the configuration elements expected by the parser.
     * @return A list of valid configuration elements.
     */
    List<ConfigName> validConfigurations();

    /**
     * Configure a parser. Expect this method to be called for each {@link ConfigName} value.
     *
     * @param configName The name of the configuration.
     * @param configValues The value(s) of the configuration element.
     */
    void configure(ConfigName configName, List<ConfigValue> configValues);
}
