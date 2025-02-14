package com.cloudera.parserchains.core;

import java.util.List;

/**
 * Parses a {@link Message} using a parser chain.
 */
public interface ChainRunner {

    /**
     * Parses text input using a parser chain.
     * @param toParse The input to parse.
     * @param chain The parser chain that parses each message.
     */
    List<Message> run(String toParse, ChainLink chain);


    /**
     * The original message that is constructed for the parser chain.
     * @param toParse The text to parse.
     */
    Message originalMessage(String toParse);
}
