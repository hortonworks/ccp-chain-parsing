package com.cloudera.parserchains.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class NextChainLinkTest {
    static LinkName linkName = LinkName.of("parser22");
    static Message message = Message
            .builder()
            .createdBy(LinkName.of("original"))
            .build();

    @Test
    void nextLink() {
        NextChainLink next = new NextChainLink(new NoopParser(), linkName);
        NextChainLink previous = new NextChainLink(new NoopParser(), linkName)
                .setNext(next);
        assertEquals(next, previous.getNext(message).get());
    }

    @Test
    void nextLinkEmpty() {
        NextChainLink link = new NextChainLink(new NoopParser(), linkName);
        // the next link is not present until set
        assertFalse(link.getNext(message).isPresent());
    }

    @Test
    void getParser() {
        Parser parser = new NoopParser();
        NextChainLink link = new NextChainLink(parser, linkName);
        assertEquals(parser, link.getParser());
    }

    @Test
    void linkName() {
        Parser parser = new NoopParser();
        NextChainLink link = new NextChainLink(parser, linkName);
        assertEquals(linkName, link.getLinkName());
    }
}
