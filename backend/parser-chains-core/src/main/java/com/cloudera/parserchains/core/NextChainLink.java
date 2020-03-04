package com.cloudera.parserchains.core;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link ChainLink} that links directly to the next link in a chain.
 */
public class NextChainLink implements ChainLink {
    private Parser parser;
    private Optional<ChainLink> next;
    private LinkName linkName;

    /**
     * @param parser The parser at this link in the chain.
     * @param linkName The name of this link in the chain.
     */
    public NextChainLink(Parser parser, LinkName linkName) {
        this.parser = Objects.requireNonNull(parser, "A valid parser is required.");
        this.next = Optional.empty();
        this.linkName = Objects.requireNonNull(linkName, "A link name is required.");
    }

    public Parser getParser() {
        return parser;
    }

    @Override
    public LinkName getLinkName() {
        return linkName;
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
