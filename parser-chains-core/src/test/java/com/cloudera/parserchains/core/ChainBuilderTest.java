package com.cloudera.parserchains.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChainBuilderTest {
    Message message;
    Parser firstParser;
    Parser secondParser;

    @BeforeEach
    void setup() {
        message = Message.builder().build();
        firstParser = Mockito.mock(Parser.class);
        secondParser = Mockito.mock(Parser.class);
    }

    @Test
    void straightChain() {
        ChainLink head = new ChainBuilder()
                .then(firstParser)
                .then(secondParser)
                .head();

        assertEquals(firstParser, head.getParser(), 
            "Expected the first parser to be at the head of the chain.");
        assertTrue(head.getNext(message).isPresent(),
            "Expected the first parser to link to the second parser.");
        ChainLink next = head.getNext(message).get();
        assertEquals(secondParser, next.getParser(), 
            "Expected the second parser to be next in the chain.");
        assertFalse(next.getNext(message).isPresent(), 
            "Expected the second parser to be last in the chain.");
    }

    @Test
    void chainWithRouter() {
        ChainLink head = new ChainBuilder()
                .then(firstParser)
                .routeBy(FieldName.of("timestamp"))
                .thenMatch(Regex.of("[0-9]+"), secondParser)
                .head();

        assertEquals(firstParser, head.getParser(), 
            "Expected the first parser to be at the head of the chain.");
        assertTrue(head.getNext(message).isPresent(),
            "Expected the first parser to link to the router.");
        ChainLink next = head.getNext(message).get();
        assertTrue(next instanceof RouterLink, 
            "Expected the next parser to be a router.");
    }

    @Test
    void routerFirstInChain() {
        ChainLink head = new ChainBuilder()
                .routeBy(FieldName.of("timestamp"))
                .thenMatch(Regex.of("[0-9]+"), secondParser)
                .head();
        assertTrue(head instanceof RouterLink,
            "Expected a router to be at the head of the chain.");
    }

    @Test
    void routerWithSubChains() {
        ChainLink subChain = new ChainBuilder()
                .then(secondParser)
                .head();
        ChainLink mainChain = new ChainBuilder()
                .then(firstParser)
                .routeBy(FieldName.of("timestamp"))
                .thenMatch(Regex.of("[0-9]+"), subChain)
                .head();

        assertEquals(firstParser, mainChain.getParser(), 
            "Expected the first parser to be at the head of the main chain.");
        assertTrue(mainChain.getNext(message).isPresent(),
            "Expected the first parser to link to a router.");
        ChainLink next = mainChain.getNext(message).get();
        assertTrue(next instanceof RouterLink, 
            "Expected the next parser to be a router.");
    }

    @Test
    void noChain() {
        assertThrows(NullPointerException.class,
                () -> new ChainBuilder().head(),
                "No chain was defined before calling head().");
    }

    @Test
    void onlyRoutesAfterRouteBy() {
        assertThrows(IllegalStateException.class,
                () -> new ChainBuilder()
                        .routeBy(FieldName.of("timestamp"))
                        .then(secondParser)
                        .head(),
                "After calling 'routeBy' all subsequent calls must define a route.");
    }

    @Test
    void noRoutesBeforeRouteBy() {
        assertThrows(IllegalStateException.class,
                () -> new ChainBuilder()
                        .thenMatch(Regex.of("[0-9]+"), secondParser)
                        .head(), 
                "Cannot define a route before calling 'routeBy'.");
    }
}
