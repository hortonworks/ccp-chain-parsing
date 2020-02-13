package com.cloudera.parserchains.core;

import org.junit.jupiter.api.BeforeAll;
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
import static org.mockito.Mockito.mock;

public class ChainRunnerTest {
    static String inputToParse;
    static Message errorMessage;

    @BeforeAll
    static void setup() {
        inputToParse = "some input to parse";
        errorMessage = Message.builder()
                .withError("error")
                .build();
    }

    @Test
    void runChain() {
        // parser1 runs successfully and does not modify the message
        Parser parser1 = mock(Parser.class);
        when(parser1.parse(any(Message.class))).thenAnswer(i -> i.getArguments()[0]);

        // parser2 runs successfully and does not modify the message
        Parser parser2 = mock(Parser.class);
        when(parser2.parse(any(Message.class))).thenAnswer(i -> i.getArguments()[0]);

        ChainLink chain = ChainBuilder.init()
                .then(parser1)
                .then(parser2)
                .head();

        // run the parser chain
        ChainRunner runner = new ChainRunner();
        List<Message> results = runner.run(inputToParse, chain);

        // the original message that we expect the ChainRunner to build
        Message expectedMessage = Message.builder()
                .addField(runner.getInputField(), FieldValue.of(inputToParse))
                .build();
        verify(parser1, times(1)).parse(eq(expectedMessage));
        verify(parser2, times(1)).parse(eq(expectedMessage));
        assertEquals(3, results.size(), 
            "Expected 3 = 1 original input + 1 from parser1 + 1 from parser2.");
    }

    @Test
    void changeInputFieldName() {
        // parser1 runs successfully and does not modify the message
        Parser parser1 = mock(Parser.class);
        when(parser1.parse(any(Message.class))).thenAnswer(i -> i.getArguments()[0]);

        ChainLink chain = ChainBuilder.init()
                .then(parser1)
                .head();

        // change the name of the input field.
        ChainRunner runner = new ChainRunner()
                .withInputField(FieldName.of("the_original_message"));
        List<Message> results = runner.run(inputToParse, chain);

        // the original message that we expect the ChainRunner to build
        Message expectedMessage = Message.builder()
                .addField(runner.getInputField(), FieldValue.of(inputToParse))
                .build();

        // 1 original input + 1 from parser1 = 2
        assertEquals(2, results.size());
        verify(parser1, times(1)).parse(eq(expectedMessage));
    }

    @Test
    void runChainWithError() {
        Message expectedMessage = Message.builder()
                .addField(FieldName.of("original_string"), FieldValue.of(inputToParse))
                .build();

        // parser1 does not modify the message
        Parser parser1 = mock(Parser.class);
        when(parser1.parse(any(Message.class))).thenAnswer(i -> i.getArguments()[0]);

        // parser2 will error out
        Parser parser2 = mock(Parser.class);
        when(parser2.parse(any(Message.class))).thenReturn(errorMessage);

        // parser3 does not modify the message
        Parser parser3 = Mockito.mock(Parser.class);
        when(parser3.parse(any(Message.class))).thenAnswer(i -> i.getArguments()[0]);

        ChainLink chain = ChainBuilder.init()
                .then(parser1)
                .then(parser2)
                .then(parser3)
                .head();

        // run the parser chain
        ChainRunner runner = new ChainRunner();
        List<Message> results = runner.run(inputToParse, chain);

        verify(parser1, times(1)).parse(eq(expectedMessage));
        verify(parser2, times(1)).parse(eq(expectedMessage));
        verify(parser3, never()).parse(any());
        assertEquals(3, results.size(),
            "Expected 3 = 1 original + 1 from parser1 + 1 error from parser2 + 0 from parser3 (it was not executed).");
    }
}
