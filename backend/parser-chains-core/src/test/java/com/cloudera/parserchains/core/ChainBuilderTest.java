package com.cloudera.parserchains.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChainBuilderTest {
    Message message;
    Parser firstParser;
    Parser secondParser;
    Parser thirdParser;

    @BeforeEach
    void setup() {
        message = Message.builder().build();
        firstParser = Mockito.mock(Parser.class);
        secondParser = Mockito.mock(Parser.class);
        thirdParser = Mockito.mock(Parser.class);
    }

    @Test
    void parserThenParser() {
        ChainLink head = ChainBuilder.init()
                .then(firstParser)
                .then(secondParser)
                .head();

        assertEquals(firstParser, head.getParser(), 
            "Expected firstParser to be at the head of the chain.");
        assertTrue(head.getNext(message).isPresent(),
            "Expected firstParser to link to the second parser.");
        ChainLink next = head.getNext(message).get();
        assertEquals(secondParser, next.getParser(), 
            "Expected the second parser to be next in the chain.");
        assertFalse(next.getNext(message).isPresent(), 
            "Expected the second parser to be last in the chain.");
    }

    @Test
    void parserThenRouter() {
        ChainLink head = ChainBuilder.init()
                .then(firstParser)
                .routeBy(FieldName.of("timestamp"))
                .thenMatch(Regex.of("[0-9]+"), secondParser)
                .head();

        assertEquals(firstParser, head.getParser(), 
            "Expected firstParser to be at the head of the chain.");
        assertTrue(head.getNext(message).isPresent(),
            "Expected firstParser to link to the router.");
        ChainLink next = head.getNext(message).get();
        assertTrue(next instanceof RouterLink, 
            "Expected the next parser to be a router.");
    }

    @Test
    void parserThenChain() {
        ChainLink subChain = ChainBuilder.init()
                .then(secondParser)
                .head();

        ChainLink head = ChainBuilder.init()
                .then(firstParser)
                .routeBy(FieldName.of("timestamp"))
                .thenMatch(Regex.of("[0-9]+"), subChain)
                .head();

        assertEquals(firstParser, head.getParser(), 
            "Expected firstParser to be at the head of the chain.");
        assertTrue(head.getNext(message).isPresent(),
            "Expected firstParser to link to the router.");
        ChainLink next = head.getNext(message).get();
        assertTrue(next instanceof RouterLink, 
            "Expected the next parser to be a router.");
    }    

    @Test
    void parserThenParserThenRouter() {
        ChainLink head = ChainBuilder.init()
                .then(firstParser)
                .then(secondParser)
                .routeBy(FieldName.of("timestamp"))
                .thenMatch(Regex.of("[0-9]+"), thirdParser)
                .head();

        assertEquals(firstParser, head.getParser(), 
            "Expected firstParser to be at the head of the chain.");
        assertTrue(head.getNext(message).isPresent(),
            "Expected firstParser to link to the second parser.");
        ChainLink next = head.getNext(message).get();
        assertEquals(secondParser, next.getParser(), 
            "Expected secondParser to be next in the chain.");
        assertTrue(next.getNext(message).isPresent(),
            "Expected secondParser to link to the router.");
        ChainLink thirdOne = next.getNext(message).get();
        assertTrue(thirdOne instanceof RouterLink, 
            "Expected the third parser to be a router.");
    }

    @Test
    void routerOnly() {
        FieldName routeBy = FieldName.of("timestamp");
        ChainLink head = ChainBuilder.init()
                .routeBy(routeBy)
                .thenMatch(Regex.of("[0-9]+"), secondParser)
                .head();
        assertTrue(head instanceof RouterLink,
            "Expected a router to be at the head of the chain.");
        RouterLink router = RouterLink.class.cast(head);
        assertEquals(1, router.getRoutes().size(),
            "Expected 1 route to be defined.");
        assertEquals(routeBy, router.getInputField(), 
            "Expected routeBy field to be: " + routeBy);
        assertEquals(secondParser, router.getRoutes().get(0).next.getParser(),
            "Expected the secondParser to be the route destination.");
    }

    @Test
    void defaultRoute() {
        FieldName routeBy = FieldName.of("timestamp");
        ChainLink head = ChainBuilder.init()
                .routeBy(routeBy)
                .thenMatch(Regex.of("[0-9]+"), secondParser)
                .thenDefault(firstParser)
                .head();

        assertTrue(head instanceof RouterLink,
            "Expected a router to be at the head of the chain.");
        RouterLink router = RouterLink.class.cast(head);
        assertEquals(1, router.getRoutes().size(),
            "Expected 1 route to be defined.");
        assertEquals(routeBy, router.getInputField(), 
            "Expected routeBy field to be: " + routeBy);
        assertEquals(secondParser, router.getRoutes().get(0).next.getParser(),
            "Expected the secondParser to be the route destination.");
        assertTrue(head instanceof RouterLink,
            "Expected a router to be at the head of the chain.");
        assertTrue(router.getDefault().isPresent(),
            "Expected the default route to be defined.");   
    }

    @Test
    void routerThenSubChain() {
        ChainLink subChain = ChainBuilder.init()
                .then(secondParser)
                .head();
        ChainLink mainChain = ChainBuilder.init()
                .routeBy(FieldName.of("timestamp"))
                .thenMatch(Regex.of("[0-9]+"), subChain)
                .head();

        assertTrue(mainChain instanceof RouterLink, 
            "Expected the first link to be a router.");
        RouterLink router = RouterLink.class.cast(mainChain);
        assertEquals(1, router.getRoutes().size(),
            "Expected 1 route to be defined.");
        assertEquals(subChain, router.getRoutes().get(0).next,
            "Expect the subChain to be the only route.");
    }
}