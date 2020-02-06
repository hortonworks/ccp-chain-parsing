package com.cloudera.parserchains.core;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChainRunnerTest {

    @Test
    void runChain() {
        Message goodMessage = Message.builder().build();

        Parser parser1 = Mockito.mock(Parser.class);
        when(parser1.parse(Mockito.any(Message.class)))
                .thenReturn(goodMessage);

        Parser parser2 = Mockito.mock(Parser.class);
        when(parser2.parse(Mockito.any(Message.class)))
                .thenReturn(goodMessage);

        ChainLink chain = new ChainBuilder()
                .then(parser1)
                .then(parser2)
                .head();

        // run the parser chain
        ChainRunner runner = new ChainRunner();
        List<Message> results = runner.run(goodMessage, chain);

        // 1 original input + 1 from parser1 + 1 from parser2 = 3
        assertEquals(3, results.size());
        verify(parser1, times(1)).parse(eq(goodMessage));
        verify(parser2, times(1)).parse(eq(goodMessage));
    }

    @Test
    void runOne() {
        Message goodMessage = Message.builder().build();

        Parser parser1 = Mockito.mock(Parser.class);
        when(parser1.parse(Mockito.any(Message.class)))
                .thenReturn(goodMessage);

        ChainLink chain = new ChainBuilder()
                .then(parser1)
                .head();

        // run the parser chain
        ChainRunner runner = new ChainRunner();
        List<Message> results = runner.run(goodMessage, chain);

        // 1 original input + 1 from parser1 = 2
        assertEquals(2, results.size());
        verify(parser1, times(1)).parse(eq(goodMessage));
    }

    @Test
    void runChainWithError() {
        Message goodMessage = Message.builder().build();
        Message errorMessage = Message.builder().withError("error").build();

        Parser parser1 = Mockito.mock(Parser.class);
        when(parser1.parse(Mockito.any(Message.class)))
                .thenReturn(goodMessage);

        Parser parser2 = Mockito.mock(Parser.class);
        when(parser2.parse(Mockito.any(Message.class)))
                .thenReturn(errorMessage);

        Parser parser3 = Mockito.mock(Parser.class);
        when(parser3.parse(Mockito.any(Message.class)))
                .thenReturn(goodMessage);

        ChainLink chain = new ChainBuilder()
                .then(parser1)
                .then(parser2)
                .then(parser3)
                .head();

        // run the parser chain
        ChainRunner runner = new ChainRunner();
        List<Message> results = runner.run(goodMessage, chain);

        verify(parser1, times(1)).parse(eq(goodMessage));
        verify(parser2, times(1)).parse(eq(goodMessage));
        verify(parser3, never()).parse(any());

        // 1 original message, good message from parser1, error message from parser2, parser3 not executed
        assertEquals(3, results.size());
    }
}
