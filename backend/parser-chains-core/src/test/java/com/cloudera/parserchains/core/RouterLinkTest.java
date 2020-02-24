package com.cloudera.parserchains.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RouterLinkTest {
    Parser parser;
    LinkName linkName;
    
    @BeforeEach
    void setup() {
        parser = Mockito.mock(Parser.class);
        linkName = LinkName.of("parser35");
    }
    
    @Test
    void route() {
        // there is a valid route for this message
        Message message = Message.builder()
                .addField(FieldName.of("tag"), FieldValue.of("match_1"))
                .createdBy(LinkName.of("original"))
                .build();

        ChainLink expectedNext = new NextChainLink(parser, linkName);
        RouterLink routerLink = new RouterLink(linkName)
                .withInputField(FieldName.of("tag"))
                .withRoute(Regex.of("match_[0-9]+"), expectedNext)
                .withRoute(Regex.of("no_match"), new NextChainLink(parser, linkName));

        ChainLink actualNext = routerLink.getNext(message).get();
        assertEquals(expectedNext, actualNext);
    }

    @Test
    void noRoute() {
        // there is no valid route for this message
        Message message = Message.builder()
                .addField(FieldName.of("tag"), FieldValue.of("route9"))
                .createdBy(LinkName.of("original"))
                .build();

        ChainLink expectedNext = new NextChainLink(parser, linkName);
        RouterLink routerLink = new RouterLink(linkName)
                .withInputField(FieldName.of("tag"))
                .withRoute(Regex.of("no_match"), expectedNext);

        assertFalse(routerLink.getNext(message).isPresent());
    }

    @Test
    void defaultRoute() {
        // there is no valid route for this message
        Message message = Message.builder()
                .addField(FieldName.of("tag"), FieldValue.of("route9"))
                .createdBy(LinkName.of("original"))
                .build();

        ChainLink expectedNext = new NextChainLink(parser, linkName);
        RouterLink routerLink = new RouterLink(linkName)
                .withInputField(FieldName.of("tag"))
                .withRoute(Regex.of("no_match"), new NextChainLink(parser, linkName))
                .withDefault(expectedNext);

        ChainLink actualNext = routerLink.getNext(message).get();
        assertEquals(expectedNext, actualNext);
    }

    @Test
    void undefinedRoutingField() {
        Message message = Message.builder()
                .addField(FieldName.of("tag"), FieldValue.of("route9"))
                .createdBy(LinkName.of("original"))
                .build();
        RouterLink routerLink = new RouterLink(linkName)
                .withRoute(Regex.of("match"), new NextChainLink(parser, linkName));

        // the field used to route the message was not defined
        assertThrows(IllegalStateException.class, () -> routerLink.getNext(message));
    }
}
