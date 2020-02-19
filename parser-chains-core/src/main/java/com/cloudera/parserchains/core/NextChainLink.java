package com.cloudera.parserchains.core;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link ChainLink} that links directly to the next link in a chain.
 */
public class NextChainLink implements ChainLink {
    private Parser parser;
    private Optional<ChainLink> next;

    public NextChainLink(Parser parser) {
        this.parser = Objects.requireNonNull(parser);
        this.next = Optional.empty();
    }

    public Parser getParser() {
        return parser;
    }

    /**
     * Get the next link in the chain.
     * @param message The message that is being parsed.
     * @return The next link in the chain or Optional.empty if there is no next link.
     */
    @Override
    public Optional<ChainLink> getNext(Message message) {
        return next;
    }

    public NextChainLink setNext(ChainLink next) {
        this.next = Optional.of(next);
        return this;
    }
}
