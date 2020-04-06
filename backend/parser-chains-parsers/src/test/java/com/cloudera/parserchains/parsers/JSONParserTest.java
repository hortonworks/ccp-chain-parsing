package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.Message;
import org.adrianwalker.multilinestring.Multiline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.cloudera.parserchains.core.Constants.DEFAULT_INPUT_FIELD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JSONParserTest {
    JSONParser jsonParser;

    @BeforeEach
    void beforeEach() {
        jsonParser = new JSONParser();
    }

    /**
     * {
     *     "first": "one",
     *     "second": "two",
     *     "third": "three"
     * }
     */
    @Multiline
    static String fieldForEachElement;

    @Test
    void fieldForEachElement() {
        Message input = Message.builder()
                .addField(DEFAULT_INPUT_FIELD, fieldForEachElement)
                .build();
        Message output = jsonParser
                .parse(input);
        Message expected = Message.builder()
                .withFields(input)
                .addField("first", "one")
                .addField("second", "two")
                .addField("third", "three")
                .build();
        assertThat("Expected a new field for each JSON element.",
                output, is(expected));
    }

    /**
     * {
     *      "invalid",
     *      "json"
     * }
     */
    @Multiline
    static String invalidJSON;

    @Test
    void invalidJSON() {
        Message input = Message.builder()
                .addField(DEFAULT_INPUT_FIELD, invalidJSON)
                .build();
        Message output = jsonParser
                .parse(input);
        assertTrue(output.getError().isPresent(),
                "Expected an error because of the invalid JSON.");
    }

    @Test
    void missingInputField() {
        Message input = Message.builder()
                .build();
        Message output = jsonParser
                .parse(input);
        assertTrue(output.getError().isPresent(),
                "Expected a parsing error because there is no 'input' field to parse.");
    }

    /**
     * {
     *     "list": [ "one", "two", "three" ]
     * }
     */
    @Multiline
    static String arrays;

    @Test
    void arrays() {
        Message input = Message.builder()
                .addField(DEFAULT_INPUT_FIELD, arrays)
                .build();
        Message output = jsonParser
                .parse(input);
        Message expected = Message.builder()
                .withFields(input)
                .addField("list", "[one, two, three]")
                .build();
        assertThat(output, is(expected));
    }

    /**
     * {
     *    "object": {
     *       "first":"one",
     *       "second":"two",
     *       "third":"three"
     *    }
     * }
     */
    @Multiline
    static String objects;

    @Test
    void unfoldNestedObjects() {
        Message input = Message.builder()
                .addField(DEFAULT_INPUT_FIELD, objects)
                .build();
        Message output = jsonParser
                .normalizer("UNFOLD_NESTED")
                .parse(input);
        Message expected = Message.builder()
                .withFields(input)
                .addField("object.first", "one")
                .addField("object.second", "two")
                .addField("object.third", "three")
                .build();
        assertThat(output, is(expected));
    }

    @Test
    void dropNestedObjects() {
        Message input = Message.builder()
                .addField(DEFAULT_INPUT_FIELD, objects)
                .build();
        Message output = jsonParser
                .normalizer("DROP_NESTED")
                .parse(input);
        Message expected = Message.builder()
                .clone(input)
                .build();
        assertThat(output, is(expected));
    }

    @Test
    void allowNestedObjects() {
        Message input = Message.builder()
                .addField(DEFAULT_INPUT_FIELD, objects)
                .build();
        Message output = jsonParser
                .normalizer("ALLOW_NESTED")
                .parse(input);
        Message expected = Message.builder()
                .withFields(input)
                .addField("object", "{first=one, second=two, third=three}")
                .build();
        assertThat(output, is(expected));
    }

    @Test
    void disallowNestedObjects() {
        Message input = Message.builder()
                .addField(DEFAULT_INPUT_FIELD, objects)
                .build();
        Message output = jsonParser
                .normalizer("DISALLOW_NESTED")
                .parse(input);
        assertTrue(output.getError().isPresent(),
                "Expected an error because nested objects have been disallowed.");
    }

    @Test
    void invalidNormalizer() {
        Message input = Message.builder()
                .addField(DEFAULT_INPUT_FIELD, objects)
                .build();
        assertThrows(IllegalArgumentException.class, () -> jsonParser.normalizer("INVALID").parse(input));
    }
}
