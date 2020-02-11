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
     */
    List<FieldName> outputFields();
}
