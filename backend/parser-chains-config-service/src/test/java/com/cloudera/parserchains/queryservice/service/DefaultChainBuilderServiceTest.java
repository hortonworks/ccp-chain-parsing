package com.cloudera.parserchains.queryservice.service;

import com.cloudera.parserchains.core.ChainLink;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.ReflectiveParserBuilder;
import com.cloudera.parserchains.core.catalog.ClassIndexParserCatalog;
import com.cloudera.parserchains.parsers.DelimitedTextParser;
import com.cloudera.parserchains.parsers.TimestampParser;
import com.cloudera.parserchains.queryservice.common.utils.JSONUtils;
import com.cloudera.parserchains.queryservice.model.define.ParserChainSchema;
import org.adrianwalker.multilinestring.Multiline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultChainBuilderServiceTest {

    @Mock
    private Message message;
    private DefaultChainBuilderService service;

    @BeforeEach
    void beforeEach() {
        service = new DefaultChainBuilderService(new ReflectiveParserBuilder(), new ClassIndexParserCatalog());
    }

    /**
     * {
     *     "id" : "3b31e549-340f-47ce-8a71-d702685137f4",
     *     "name" : "My Parser Chain",
     *     "parsers" : [ {
     *       "id" : "8673f8f4-a308-4689-822c-0b01477ef378",
     *       "name" : "Timestamp",
     *       "type" : "com.cloudera.parserchains.parsers.TimestampParser",
     *       "config" : {
     *         "outputField" : {
     *           "outputField": "processing_time"
     *         }
     *       },
     *       "outputs" : { }
     *     }, {
     *       "id" : "3b31e549-340f-47ce-8a71-d702685137f4",
     *       "name" : "Delimited Text",
     *       "type" : "com.cloudera.parserchains.parsers.DelimitedTextParser",
     *       "config" : {
     *         "inputField" : [ {
     *           "inputField": "original_string"
     *         }],
     *         "outputField" : [ {
     *           "fieldIndex" : "0",
     *           "fieldName" : "name"
     *         }, {
     *           "fieldIndex" : "1",
     *           "fieldName" : "address"
     *         }, {
     *           "fieldIndex" : "2",
     *           "fieldName" : "phone"
     *         }  ]
     *       },
     *       "outputs" : { }
     *     }]
     * }
     */
    @Multiline
    private String parserChain;

    @Test
    void build() throws InvalidParserException, IOException {
        ParserChainSchema schema = JSONUtils.INSTANCE.load(parserChain, ParserChainSchema.class);
        ChainLink head = service.build(schema);

        assertThat("Expected the first link name to be set correctly.",
                head.getLinkName().get(), is("8673f8f4-a308-4689-822c-0b01477ef378"));
        assertThat("Expected the first parser to be a timestamp parser.",
                head.getParser(), instanceOf(TimestampParser.class));
        TimestampParser timestampParser = (TimestampParser) head.getParser();
        assertThat("Expected the timestamp parser to have the input field set correctly.",
                timestampParser.getOutputField().get(), is("processing_time"));


        assertThat("Expected the next link to be set.",
                head.getNext(message).isPresent());
        ChainLink next = head.getNext(message).get();
        assertThat("Expected the csv parser to have the correct name.",
                next.getLinkName().get(), is("3b31e549-340f-47ce-8a71-d702685137f4"));
        assertThat("Expected the second parser to be a csv parser.",
                next.getParser(), instanceOf(DelimitedTextParser.class));
        DelimitedTextParser csvParser = (DelimitedTextParser) next.getParser();
        assertThat("Expected the input fields to be set.",
                csvParser.getInputField().get(), is("original_string"));
        assertThat("Expected the output fields to be set.",
                csvParser.getOutputFields().size(), is(3));
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
     *          "outputs":{
     *          },
     *          "advanced":{
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
        assertThrows(InvalidParserException.class, () -> service.build(schema),
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
     *             "outputs":{
     *             },
     *             "advanced":{
     *             },
     *             "routing":{
     *                "matchingField":"original_string",
     *                "routes":[
     *                ]
     *             }
     *          },
     *          {
     *             "name":"Timestamper",
     *             "type":"com.cloudera.parserchains.parsers.TimestampParser",
     *             "id":"2313a690-5d62-11ea-a9b6-9537bbab1bb1",
     *             "config":{
     *             },
     *             "outputs":{
     *             },
     *             "advanced":{
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
        assertThrows(InvalidParserException.class, () -> service.build(schema),
                "Expected exception because no parser can follow a router in a chain.");
    }
}
