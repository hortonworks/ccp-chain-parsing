package com.cloudera.parserchains.core;

import java.util.List;

/**
 * Provides a catalog of all parsers available to the user.
 *
 * <p>A parser should be marked using the {@link MessageParser} annotation
 * so that the parser is discoverable using a {@link ParserCatalog}.
 */
public interface ParserCatalog {

    /**
     * Returns all of the available parsers in the catalog.
     */
    List<ParserInfo> getParsers();
}
