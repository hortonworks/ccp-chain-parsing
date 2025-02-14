package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.Message;
import com.jayway.jsonpath.InvalidPathException;
import org.adrianwalker.multilinestring.Multiline;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.cloudera.parserchains.core.Constants.DEFAULT_INPUT_FIELD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JSONPathParserTest {
    JSONPathParser parser;

    @BeforeEach
    void beforeEach() {
        parser = new JSONPathParser();
    }

    /**
     * {
     *     "store": {
     *         "book": [
     *             {
     *                 "category": "reference",
     *                 "author": "Nigel Rees",
     *                 "title": "Sayings of the Century",
     *                 "price": 8.95
     *             },
     *             {
     *                 "category": "fiction",
     *                 "author": "Evelyn Waugh",
     *                 "title": "Sword of Honour",
     *                 "price": 12.99
     *             },
     *             {
     *                 "category": "fiction",
     *                 "author": "Herman Melville",
     *                 "title": "Moby Dick",
     *                 "isbn": "0-553-21311-3",
     *                 "price": 8.99
     *             },
     *             {
     *                 "category": "fiction",
     *                 "author": "J. R. R. Tolkien",
     *                 "title": "The Lord of the Rings",
     *                 "isbn": "0-395-19395-8",
     *                 "price": 22.99
     *             }
     *         ],
     *         "bicycle": {
     *             "color": "red",
     *             "price": 19.95
     *         }
     *     },
     *     "expensive": 10
     * }
     */
    @Multiline
    static String json;

    @Test
    void expression() {
        Message input = Message.builder()
                .addField(DEFAULT_INPUT_FIELD, json)
                .build();
        Message output = parser
                .expression("numberOfBooks", "$..book.length()")
                .parse(input);
        Message expected = Message.builder()
                .withFields(input)
                .addField("numberOfBooks", "4")
                .build();
        assertThat(output, is(expected));
    }

    @Test
    void expressions() {
        Message input = Message.builder()
                .addField(DEFAULT_INPUT_FIELD, json)
                .build();
        Message output = parser
                .expression("book1", "$..book[0].title")
                .expression("book2", "$..book[1].title")
                .expression("book3", "$..book[2].title")
                .parse(input);
        Message expected = Message.builder()
                .withFields(input)
                .addField("book1", "Sayings of the Century")
                .addField("book2", "Sword of Honour")
                .addField("book3", "Moby Dick")
                .build();
        assertThat( output, is(expected));
    }

    @Test
    void noMatch() {
        Message input = Message.builder()
                .addField(DEFAULT_INPUT_FIELD, json)
                .build();
        Message output = parser
                .expression("date", "$..book[0].date")
                .parse(input);
        Message expected = Message.builder()
                .withFields(input)
                .addField("date", "")
                .build();
        assertThat("Expected the field to have a blank value, if there is no match.",
                output, is(expected));
    }

    @Test
    void invalidJsonPath() {
        assertThrows(InvalidPathException.class,
                () -> parser.expression("author", "$.store.book[?(@.price > 10)"));
    }

    @Test
    void predicate() {
        Message input = Message.builder()
                .addField(DEFAULT_INPUT_FIELD, json)
                .build();
        Message output = parser
                .expression("expensiveTitle", "$.store.book[?(@.price > 20)].title")
                .parse(input);
        Message expected = Message.builder()
                .withFields(input)
                .addField("expensiveTitle", "The Lord of the Rings")
                .build();
        assertThat(output, is(expected));
    }
}
