package com.cloudera.parserchains.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class NextChainLinkTest {
    Message message = Message.builder().build();

    @Test
    void nextLink() {
        NextChainLink next = new NextChainLink(new NoopParser());
        NextChainLink previous = new NextChainLink(new NoopParser())
                .setNext(next);
        assertEquals(next, previous.getNext(message).get());
    }

    @Test
    void nextLinkEmpty() {
        NextChainLink link = new NextChainLink(new NoopParser());
        // the next link is not present until set
        assertFalse(link.getNext(message).isPresent());
    }

    @Test
    void getParser() {
        Parser parser = new NoopParser();
        NextChainLink link = new NextChainLink(parser);
        assertEquals(parser, link.getParser());
    }
}
