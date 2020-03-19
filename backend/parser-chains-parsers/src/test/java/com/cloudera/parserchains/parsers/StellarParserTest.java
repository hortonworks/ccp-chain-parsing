package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.Message;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class StellarParserTest {

    @Test
    void addField() throws Exception {
        Message input = Message.builder()
                .addField("field1", "value1")
                .build();
        Message output = new StellarParser()
                .expression("field2", "TO_UPPER(field1)")
                .parse(input);
        Message expected = Message.builder()
                .addField("field1", "value1")
                .addField("field2", "VALUE1")
                .build();
        assertThat("Expected a field to be added using a Stellar expression.",
                output, is(expected));
    }

    @Test
    void updateField() throws Exception {
        Message input = Message.builder()
                .addField("field1", "value1")
                .addField("field2", "value2")
                .build();
        Message output = new StellarParser()
                .expression("field2", "TO_UPPER(field2)")
                .parse(input);
        Message expected = Message.builder()
                .addField("field1", "value1")
                .addField("field2", "VALUE2")
                .build();
        assertThat("Expected a field to be updated using a Stellar expression.",
                output, is(expected));
    }

    @Test
    void sequentialExpressions() throws Exception {
        Message input = Message.builder()
                .addField("field1", "value1")
                .build();
        Message output = new StellarParser()
                .expression("field2", "TO_UPPER(field1)")
                .expression("field3", "LENGTH(field2)")
                .parse(input);
        Message expected = Message.builder()
                .addField("field1", "value1")
                .addField("field2", "VALUE1")
                .addField("field3", "6")
                .build();
        assertThat("Expected an expression to be able to refer to the result of a previous one.",
                output, is(expected));
    }

    @Test
    void numericValues() throws Exception {
        Message input = Message.builder()
                .addField("field1", "1")
                .addField("field2", "1")
                .build();
        // all values in the input message are strings and must be converted to integers
        Message output = new StellarParser()
                .expression("field3", "TO_INTEGER(field1) + TO_INTEGER(field2)")
                .parse(input);
        Message expected = Message.builder()
                .addField("field1", "1")
                .addField("field2", "1")
                .addField("field3", "2")
                .build();
        assertThat("Expected to be able to add two numbers after conversion.",
                output, is(expected));
    }

    @Test
    void fibonacciSeries() throws Exception {
        Message input = Message.builder()
                .addField("field1", "1")
                .addField("field2", "1")
                .build();
        Message output = new StellarParser()
                .expression("field3", "TO_INTEGER(field1) + TO_INTEGER(field2)")
                .expression("field4", "TO_INTEGER(field2) + field3")
                .expression("field5", "field3 + field4")
                .parse(input);
        Message expected = Message.builder()
                .addField("field1", "1")
                .addField("field2", "1")
                .addField("field3", "2")
                .addField("field4", "3")
                .addField("field5", "5")
                .build();
        assertThat("Expected to be able to generate the Fibonacci Series.",
                output, is(expected));
    }

    @Test
    void error() throws Exception {
        Message input = Message.builder()
                .addField("field1", "1")
                .build();
        Message output = new StellarParser()
                .expression("field2", "2 / 0")
                .parse(input);
        assertThat("Expected an error to be reported.",
                output.getError().isPresent());
        assertThat("Expected the same input fields to exist on the output, even with an error.",
                output.getFields().equals(input.getFields()));
    }

    @Test
    void stellarParseError() throws Exception {
        Message input = Message.builder()
                .addField("field1", "field1")
                .build();
        Message output = new StellarParser()
                .expression("field2", "TO_UPPER(field ")
                .parse(input);
        assertThat("Expected an error to be reported.",
                output.getError().isPresent());
        assertThat("Expected the same input fields to exist on the output, even with an error.",
                output.getFields().equals(input.getFields()));
    }
}
