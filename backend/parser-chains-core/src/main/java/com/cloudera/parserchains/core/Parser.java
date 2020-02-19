package com.cloudera.parserchains.core;

import java.util.List;

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
}
