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
    LinkName firstLinkName;
    LinkName secondLinkName;
    LinkName thirdLinkName;
    LinkName fourthLinkName;

    @BeforeEach
    void setup() {
        message = Message.builder().createdBy(LinkName.of("original")).build();
        firstParser = Mockito.mock(Parser.class);
        secondParser = Mockito.mock(Parser.class);
        thirdParser = Mockito.mock(Parser.class);
        firstLinkName = LinkName.of("parser1");
        secondLinkName = LinkName.of("parser2");
        thirdLinkName = LinkName.of("parser3");
        fourthLinkName = LinkName.of("parser4");
    }

    @Test
    void parserThenParser() {
        ChainLink head = ChainBuilder.init()
                .then(firstParser, firstLinkName)
                .then(secondParser, secondLinkName)
                .head();

        assertEquals(firstParser, head.getParser(), 
            "Expected firstParser to be at the head of the chain.");
        assertTrue(head.getNext(message).isPresent(),
            "Expected firstParser to link to the second parser.");
        assertEquals(firstLinkName, head.getLinkName(),
                "Expected the link to be named: " + firstLinkName);
        ChainLink next = head.getNext(message).get();
        assertEquals(secondParser, next.getParser(), 
            "Expected the second parser to be next in the chain.");
        assertFalse(next.getNext(message).isPresent(), 
            "Expected the second parser to be last in the chain.");
        assertEquals(secondLinkName, next.getLinkName(),
                "Expected the link to be named: " + secondLinkName);
    }

    @Test
    void parserThenRouter() {
        ChainLink head = ChainBuilder.init()
                .then(firstParser, firstLinkName)
                .routeBy(FieldName.of("timestamp"), secondLinkName)
                .thenMatch(Regex.of("[0-9]+"), secondParser, thirdLinkName)
                .head();

        assertEquals(firstParser, head.getParser(), 
            "Expected firstParser to be at the head of the chain.");
        assertEquals(firstLinkName, head.getLinkName(),
                "Expected the first link to be named: " + firstLinkName);
        assertTrue(head.getNext(message).isPresent(),
            "Expected firstParser to link to the router.");
        ChainLink next = head.getNext(message).get();
        assertTrue(next instanceof RouterLink, 
            "Expected the next parser to be a router.");
        assertEquals(secondLinkName, next.getLinkName(),
                "Expected the first link to be named: " + secondLinkName);
    }

    @Test
    void parserThenChain() {
        ChainLink subChain = ChainBuilder.init()
                .then(secondParser, thirdLinkName)
                .head();

        ChainLink head = ChainBuilder.init()
                .then(firstParser, firstLinkName)
                .routeBy(FieldName.of("timestamp"), secondLinkName)
                .thenMatch(Regex.of("[0-9]+"), subChain)
                .head();

        assertEquals(firstParser, head.getParser(), 
            "Expected firstParser to be at the head of the chain.");
        assertEquals(firstLinkName, head.getLinkName(),
                "Expected the first link to be named: " + firstLinkName);
        assertTrue(head.getNext(message).isPresent(),
            "Expected firstParser to link to the router.");
        ChainLink next = head.getNext(message).get();
        assertTrue(next instanceof RouterLink, 
            "Expected the next parser to be a router.");
        assertEquals(secondLinkName, next.getLinkName(),
                "Expected the first link to be named: " + secondLinkName);
    }    

    @Test
    void parserThenParserThenRouter() {
        ChainLink head = ChainBuilder.init()
                .then(firstParser, firstLinkName)
                .then(secondParser, secondLinkName)
                .routeBy(FieldName.of("timestamp"), thirdLinkName)
                .thenMatch(Regex.of("[0-9]+"), thirdParser, fourthLinkName)
                .head();

        assertEquals(firstParser, head.getParser(), 
            "Expected firstParser to be at the head of the chain.");
        assertEquals(firstLinkName, head.getLinkName(),
                "Expected the link to be named: " + firstLinkName);
        assertTrue(head.getNext(message).isPresent(),
            "Expected firstParser to link to the second parser.");
        ChainLink next = head.getNext(message).get();
        assertEquals(secondLinkName, next.getLinkName(),
                "Expected the link to be named: " + secondLinkName);
        assertEquals(secondParser, next.getParser(), 
            "Expected secondParser to be next in the chain.");
        assertTrue(next.getNext(message).isPresent(),
            "Expected secondParser to link to the router.");
        ChainLink thirdOne = next.getNext(message).get();
        assertTrue(thirdOne instanceof RouterLink, 
            "Expected the third parser to be a router.");
        assertEquals(thirdLinkName, thirdOne.getLinkName(),
                "Expected the link to be named: " + thirdLinkName);
    }

    @Test
    void routerOnly() {
        FieldName routeBy = FieldName.of("timestamp");
        ChainLink head = ChainBuilder.init()
                .routeBy(routeBy, firstLinkName)
                .thenMatch(Regex.of("[0-9]+"), secondParser, secondLinkName)
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
        assertEquals(firstLinkName, head.getLinkName(),
                "Expected the link to be named: " + firstLinkName);
    }

    @Test
    void defaultRoute() {
        FieldName routeBy = FieldName.of("timestamp");
        ChainLink head = ChainBuilder.init()
                .routeBy(routeBy, firstLinkName)
                .thenMatch(Regex.of("[0-9]+"), secondParser, secondLinkName)
                .thenDefault(firstParser, thirdLinkName)
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
                .then(secondParser, secondLinkName)
                .head();
        ChainLink mainChain = ChainBuilder.init()
                .routeBy(FieldName.of("timestamp"), firstLinkName)
                .thenMatch(Regex.of("[0-9]+"), subChain)
                .head();

        assertTrue(mainChain instanceof RouterLink, 
            "Expected the first link to be a router.");
        RouterLink router = RouterLink.class.cast(mainChain);
        assertEquals(1, router.getRoutes().size(),
            "Expected 1 route to be defined.");
        assertEquals(firstLinkName, router.getLinkName(),
                "Expected the link to be named: " + firstLinkName);
        assertEquals(subChain, router.getRoutes().get(0).next,
            "Expect the subChain to be the only route.");
    }
}