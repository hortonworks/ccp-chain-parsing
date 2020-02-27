package com.cloudera.parserchains.queryservice.service;

import com.cloudera.parserchains.queryservice.model.define.ParserChainSchema;
import com.cloudera.parserchains.queryservice.model.exec.ParserResult;

import java.io.IOException;
import java.util.List;

/**
 * A service that executes a parser chain on sample data.
 */
public interface ChainExecutorService {

    /**
     * Executes a parser chain by parsing a message.
     * @param chain The parser chain to execute.
     * @param textToParse The text to parse.
     * @return The result of parsing the text with the parser chain.
     * @throws IOException
     */
    List<ParserResult> execute(ParserChainSchema chain, String textToParse) throws IOException;
}