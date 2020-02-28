package com.cloudera.parserchains.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.description;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

public class ChainRunnerTest {
    static String inputToParse;
    static Message errorMessage;
    static LinkName linkName1;
    static LinkName linkName2;
    static LinkName linkName3;

    @BeforeAll
    static void setup() {
        inputToParse = "some input to parse";
        errorMessage = Message.builder()
                .withError("error")
                .createdBy(LinkName.of("parserInError"))
                .build();
        linkName1 = LinkName.of("parser1");
        linkName2 = LinkName.of("parser2");
        linkName3 = LinkName.of("parser3");
    }

    @Test
    void runChain() {
        // parser1 runs successfully and does not modify the message
        Parser parser1 = mock(Parser.class);
        when(parser1.parse(any(Message.class))).thenAnswer(i -> i.getArguments()[0]);

        // parser2 runs successfully and does not modify the message
        Parser parser2 = mock(Parser.class);
        when(parser2.parse(any(Message.class))).thenAnswer(i -> i.getArguments()[0]);

        // run the parser chain
        ChainLink chain = ChainBuilder.init()
                .then(parser1, linkName1)
                .then(parser2, linkName2)
                .head();
        ChainRunner runner = new ChainRunner();
        List<Message> results = runner.run(inputToParse, chain);

        // verify the input received by each parser
        Message expectedInput1 = Message.builder()
                .addField(runner.getInputField(), FieldValue.of(inputToParse))
                .createdBy(ChainRunner.ORIGINAL_MESSAGE)
                .build();
        verify(parser1, description("Expected parser1 to get the original message."))
                .parse(eq(expectedInput1));
        Message expectedInput2 = Message.builder()
                .addField(runner.getInputField(), FieldValue.of(inputToParse))
                .createdBy(linkName1)
                .build();
        verify(parser2, description("Expected parser2 to parse the next message."))
                .parse(eq(expectedInput2));

        // verify the list of results
        assertEquals(3, results.size(),
            "Expected 3 = 1 original input + 1 from parser1 + 1 from parser2.");
        assertEquals(ChainRunner.ORIGINAL_MESSAGE, results.get(0).getCreatedBy(),
                "Expected the 1st message to have 'createdBy' defined.");
        assertEquals(linkName1, results.get(1).getCreatedBy(),
                "Expected the 2nd message to have been 'createdBy' by " + linkName1);
        assertEquals(linkName2, results.get(2).getCreatedBy(),
                "Expected the 3rd message to have been 'createdBy' by " + linkName2);
    }

    @Test
    void changeInputFieldName() {
        // parser1 runs successfully and does not modify the message
        Parser parser1 = mock(Parser.class);
        when(parser1.parse(any(Message.class))).thenAnswer(i -> i.getArguments()[0]);

        ChainLink chain = ChainBuilder.init()
                .then(parser1, LinkName.of("parser1"))
                .head();

        // change the name of the input field.
        ChainRunner runner = new ChainRunner()
                .withInputField(FieldName.of("the_original_message"));
        List<Message> results = runner.run(inputToParse, chain);

        // the original message that we expect the ChainRunner to build
        Message expectedMessage = Message.builder()
                .addField(runner.getInputField(), FieldValue.of(inputToParse))
                .createdBy(ChainRunner.ORIGINAL_MESSAGE)
                .build();

        // 1 original input + 1 from parser1 = 2
        assertEquals(2, results.size());
        verify(parser1, times(1)).parse(eq(expectedMessage));
    }

    @Test
    void runChainWithError() {
        // parser1 does not modify the message
        Parser parser1 = mock(Parser.class);
        when(parser1.parse(any(Message.class))).thenAnswer(i -> i.getArguments()[0]);

        // parser2 will error out
        Parser parser2 = mock(Parser.class);
        when(parser2.parse(any(Message.class))).thenReturn(errorMessage);

        // parser3 does not modify the message
        Parser parser3 = Mockito.mock(Parser.class);
        when(parser3.parse(any(Message.class))).thenAnswer(i -> i.getArguments()[0]);

        // run the parser chain
        ChainLink chain = ChainBuilder.init()
                .then(parser1, linkName1)
                .then(parser2, linkName2)
                .then(parser3, linkName3)
                .head();
        ChainRunner runner = new ChainRunner();
        List<Message> results = runner.run(inputToParse, chain);

        // verify the input received by each parser
        Message expectedInput1 = Message.builder()
                .addField(runner.getInputField(), FieldValue.of(inputToParse))
                .createdBy(ChainRunner.ORIGINAL_MESSAGE)
                .build();
        verify(parser1, description("Expected parser1 to get the original message."))
                .parse(eq(expectedInput1));
        Message expectedInput2 = Message.builder()
                .addField(runner.getInputField(), FieldValue.of(inputToParse))
                .createdBy(linkName1)
                .build();
        verify(parser2, description("Expected parser2 to parse the next message."))
                .parse(eq(expectedInput2));
        verify(parser3, never()).parse(any());
        assertEquals(3, results.size(),
            "Expected 3 = 1 original + 1 from parser1 + 1 error from parser2 + 0 from parser3 (it was not executed).");
        assertEquals(ChainRunner.ORIGINAL_MESSAGE, results.get(0).getCreatedBy(),
                "Expected the 1st message to have 'createdBy' defined.");
        assertEquals(linkName1, results.get(1).getCreatedBy(),
                "Expected the 2nd message to have been 'createdBy' by " + linkName1);
        assertEquals(linkName2, results.get(2).getCreatedBy(),
                "Expected the 3rd message to have been 'createdBy' by " + linkName2);
    }

    @Test
    void nullChain() {
        ChainRunner runner = new ChainRunner();
        List<Message> results = runner.run(inputToParse, null);

        assertEquals(ChainRunner.ORIGINAL_MESSAGE, results.get(0).getCreatedBy(),
                "Expected the 1st message to have 'createdBy' defined.");
        Message expectedMessage = Message.builder()
                .addField(runner.getInputField(), FieldValue.of(inputToParse))
                .createdBy(ChainRunner.ORIGINAL_MESSAGE)
                .build();
        assertEquals(1, results.size(),
                "Expected 1 message to be returned to indicate there was an error.");
        assertTrue(results.get(0).getError().isPresent(),
                "Expected an error to be indicated on the message.");
    }
}
