package com.cloudera.parserchains.core;

import java.util.Optional;

/**
 * One link in a parser chain.
 */
public interface ChainLink {

    /**
     * @return The {@link Parser} that is executed at this step in the parser chain.
     */
    Parser getParser();

    /**
     * Returns the next {@link ChainLink} in the parser chain.
     * @param message The message that is being parsed.
     * @return The next {@link ChainLink}.
     */
    Optional<ChainLink> getNext(Message message);

    /**
     * Returns the name assigned to this link in the chain.
     * @return The name assigned to this chain link.
     */
    LinkName getLinkName();
}
