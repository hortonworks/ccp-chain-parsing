package com.cloudera.parserchains.core;

import com.cloudera.parserchains.core.catalog.ClassIndexParserCatalog;
import com.cloudera.parserchains.core.model.define.InvalidParserException;
import com.cloudera.parserchains.core.model.define.ParserChainSchema;
import com.cloudera.parserchains.core.utils.JSONUtils;
import org.adrianwalker.multilinestring.Multiline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultChainBuilderTest {

    @Mock
    private Message message;
    private DefaultChainBuilder chainBuilder;

    @BeforeEach
    void beforeEach() {
        chainBuilder = new DefaultChainBuilder(new ReflectiveParserBuilder(), new ClassIndexParserCatalog());
    }

    /**
     * {
     *     "id" : "3b31e549-340f-47ce-8a71-d702685137f4",
     *     "name" : "My Parser Chain",
     *     "parsers" : [ {
     *       "id" : "8673f8f4-a308-4689-822c-0b01477ef378",
     *       "name" : "Test Parser",
     *       "type" : "com.cloudera.parserchains.core.TestParser",
     *       "config" : {
     *         "inputField" : {
     *           "inputField": "original_string"
     *         }
     *       }
     *     }, {
     *       "id" : "3b31e549-340f-47ce-8a71-d702685137f4",
     *       "name" : "Test Parser",
     *       "type" : "com.cloudera.parserchains.core.TestParser",
     *       "config" : {
     *         "inputField" : [ {
     *           "inputField": "original_string"
     *         }]
     *       }
     *     }]
     * }
     */
    @Multiline
    private String parserChain;

    @Test
    void build() throws InvalidParserException, IOException {
        ParserChainSchema schema = JSONUtils.INSTANCE.load(parserChain, ParserChainSchema.class);
        ChainLink head = chainBuilder.build(schema);

        assertThat("Expected the first link name to be set correctly.",
                head.getLinkName().get(), is("8673f8f4-a308-4689-822c-0b01477ef378"));
        assertThat("Expected the first parser to be a timestamp parser.",
                head.getParser(), instanceOf(TestParser.class));
        TestParser parser1 = (TestParser) head.getParser();
        assertThat("Expected the timestamp parser to have the input field set correctly.",
                parser1.getInputField().get(), is("original_string"));


        assertThat("Expected the next link to be set.",
                head.getNext(message).isPresent());
        ChainLink next = head.getNext(message).get();
        assertThat("Expected the csv parser to have the correct name.",
                next.getLinkName().get(), is("3b31e549-340f-47ce-8a71-d702685137f4"));
        assertThat("Expected the second parser to be a csv parser.",
                next.getParser(), instanceOf(TestParser.class));
        TestParser parser2 = (TestParser) next.getParser();
        assertThat("Expected the input fields to be set.",
                parser2.getInputField().get(), is("original_string"));
    }

    /**
     * {
     *    "id":"1",
     *    "name":"Hello, Chain",
     *    "parsers":[
     *       {
     *          "name":"Route by Name",
     *          "type":"Router",
     *          "id":"96f5f340-5d96-11ea-89de-3b83ec1839cd",
     *          "config":{
     *          },
     *          "routing":{
     *             "routes":[
     *             ]
     *          }
     *       }
     *    ]
     * }
     */
    @Multiline
    private String missingMatchingField;

    @Test
    void missingMatchingField() throws IOException {
        ParserChainSchema schema = JSONUtils.INSTANCE.load(missingMatchingField, ParserChainSchema.class);
        assertThrows(InvalidParserException.class, () -> chainBuilder.build(schema),
                "Expected exception because the router required a matching field to be defined.");
    }

    /**
     * {
     *       "id":"1",
     *       "name":"Hello Chain",
     *       "parsers":[
     *          {
     *             "name":"Route by Original",
     *             "type":"Router",
     *             "id":"c5df7b70-5d61-11ea-a9b6-9537bbab1bb1",
     *             "config":{
     *             },
     *             "routing":{
     *                "matchingField":"original_string",
     *                "routes":[
     *                ]
     *             }
     *          },
     *          {
     *             "name":"Timestamper",
     *             "type":"com.cloudera.parserchains.parsers.TestParser",
     *             "id":"2313a690-5d62-11ea-a9b6-9537bbab1bb1",
     *             "config":{
     *             }
     *          }
     *       ]
     *
     * }
     */
    @Multiline
    private String routerMustBeLast;

    @Test
    void routerMustBeLast() throws IOException {
        ParserChainSchema schema = JSONUtils.INSTANCE.load(routerMustBeLast, ParserChainSchema.class);
        assertThrows(InvalidParserException.class, () -> chainBuilder.build(schema),
                "Expected exception because no parser can follow a router in a chain.");
    }
}
