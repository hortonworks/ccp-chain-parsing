package com.cloudera.parserchains.queryservice.service;

import com.cloudera.parserchains.core.ChainLink;
import com.cloudera.parserchains.core.DefaultChainRunner;
import com.cloudera.parserchains.core.ReflectiveParserBuilder;
import com.cloudera.parserchains.core.catalog.ClassIndexParserCatalog;
import com.cloudera.parserchains.queryservice.common.utils.JSONUtils;
import com.cloudera.parserchains.queryservice.model.define.ParserChainSchema;
import com.cloudera.parserchains.queryservice.model.exec.ParserResult;
import org.adrianwalker.multilinestring.Multiline;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.cloudera.parserchains.queryservice.model.exec.ResultLog.DEFAULT_SUCCESS_MESSAGE;
import static com.cloudera.parserchains.queryservice.model.exec.ResultLog.ERROR_TYPE;
import static com.cloudera.parserchains.queryservice.model.exec.ResultLog.INFO_TYPE;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

public class DefaultChainExecutorServiceTest {
    private DefaultChainExecutorService service;
    private DefaultChainBuilderService chainBuilderService;

    @BeforeEach
    void beforeEach() {
        service = new DefaultChainExecutorService(
                new DefaultChainRunner());
        chainBuilderService = new DefaultChainBuilderService(
                new ReflectiveParserBuilder(),
                new ClassIndexParserCatalog());
    }

    private ChainLink buildChain(ParserChainSchema chainSchema) throws InvalidParserException {
        return chainBuilderService.build(chainSchema);
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
     *       }
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
     *       }
     *     }]
     * }
     */
    @Multiline
    private String parserChainJSON;

    @Test
    void success() throws Exception {
        // build a CSV to parse
        final String nameField = "Jane Doe";
        final String addressField = "1600 Pennsylvania Ave";
        final String phoneField = "614-867-5309";
        final String toParse = StringUtils.join(new String[] { nameField, addressField, phoneField }, ",");

        // execute a parser chain
        ParserChainSchema schema = JSONUtils.INSTANCE.load(parserChainJSON, ParserChainSchema.class);
        ChainLink chain = buildChain(schema);
        ParserResult result = service.execute(chain, toParse);

        assertThat("Expected to have 1 input field.", result.getInput().size(), is(1));
        assertThat("Expected to have 5 output fields.", result.getOutput().size(), is(5));
        expectField(result.getInput(), "original_string", toParse);
        expectField(result.getOutput(), "original_string", toParse);
        expectField(result.getOutput(), "name", nameField);
        expectField(result.getOutput(), "address", addressField);
        expectField(result.getOutput(), "phone", phoneField);
        assertThat(result.getOutput().keySet(), hasItem("processing_time"));
        assertThat("Expected the 'info' type on success.",
                result.getLog().getType(), is(INFO_TYPE));
        assertThat("Expected the 'success' message on success.",
                result.getLog().getMessage(), is(DEFAULT_SUCCESS_MESSAGE));
        assertThat("Expected the parserId to be set to the last parser in the chain.",
                result.getLog().getParserId(), is("3b31e549-340f-47ce-8a71-d702685137f4"));
    }

    @Test
    void error() throws Exception {
        // build a CSV to parse. there are not enough fields, which should result in an error
        final String nameField = "Jane Doe";
        final String toParse = StringUtils.join(new String[] { nameField }, ",");

        // execute a parser chain
        ParserChainSchema schema = JSONUtils.INSTANCE.load(parserChainJSON, ParserChainSchema.class);
        ChainLink chain = buildChain(schema);
        ParserResult result = service.execute(chain, toParse);

        assertThat("Expected to have 1 input field.", result.getInput().size(), is(1));
        assertThat("Expected to have 3 output fields.", result.getOutput().size(), is(3));
        expectField(result.getInput(), "original_string", toParse);
        expectField(result.getOutput(), "original_string", toParse);
        expectField(result.getOutput(), "name", nameField);
        assertThat(result.getOutput().keySet(), hasItem("processing_time"));
        assertThat("Expected the 'error' type when an error occurs.",
                result.getLog().getType(), is(ERROR_TYPE));
        assertThat("Expected the error message to be included in the result.",
                result.getLog().getMessage(), is("Found 1 column(s), index 2 does not exist."));
    }

    /**
     * {
     *     "id" : "3b31e549-340f-47ce-8a71-d702685137f4",
     *     "name" : "My Parser Chain",
     *     "parsers" : [ {
     *       "id" : "8673f8f4-a308-4689-822c-0b01477ef378",
     *       "name" : "Bad Parser",
     *       "type" : "com.cloudera.parserchains.queryservice.service.MisbehavingParser",
     *       "config" : { }
     *     } ]
     * }
     */
    @Multiline
    private String exceptionalChainJSON;

    @Test
    void handleException() throws Exception {
        // the service should catch and handle any exceptions thrown by a parser
        String textToParse = "this is some text to parse";
        ParserChainSchema schema = JSONUtils.INSTANCE.load(exceptionalChainJSON, ParserChainSchema.class);
        ChainLink chain = buildChain(schema);
        ParserResult result = service.execute(chain, textToParse);

        expectField(result.getInput(), "original_string", textToParse);
        assertThat("Expected the 'error' type to indicate an error was caught and reported.",
                result.getLog().getType(), is(ERROR_TYPE));
        assertThat("Expected the error message to be included in the result.",
                result.getLog().getMessage(), startsWith("An unexpected error occurred while"));
    }

    @Test
    void noChainDefined() throws Exception {
        ParserChainSchema emptyChain = new ParserChainSchema().setId("1").setName("Hello, Chain");
        String textToParse = "this is some text to parse";
        ChainLink chain = buildChain(emptyChain);
        ParserResult result = service.execute(chain, textToParse);

        expectField(result.getInput(), "original_string", textToParse);
        assertThat("Expected success to be indicated.",
                result.getLog().getType(), is(INFO_TYPE));
        assertThat("Expected a message indicated no parser chain defined.",
                result.getLog().getMessage(), is("No parser chain defined."));
    }

    /**
     * {
     *   "id" : "3b31e549-340f-47ce-8a71-d702685137f4",
     *   "name" : "My Parser Chain",
     *   "parsers" : [ {
     *     "id" : "26bf648f-930e-44bf-a4de-bfd34ac16165",
     *     "name" : "Delimited Text",
     *     "type" : "com.cloudera.parserchains.parsers.DelimitedTextParser",
     *     "config" : {
     *       "inputField" : [ {
     *         "inputField": "original_string"
     *       }],
     *       "outputField" : [ {
     *         "fieldIndex" : "0",
     *         "fieldName" : "name"
     *       }, {
     *         "fieldIndex" : "1",
     *         "fieldName" : "address"
     *       }, {
     *         "fieldIndex" : "2",
     *         "fieldName" : "phone"
     *       }  ]
     *     }
     *   }, {
     *     "id" : "123e4567-e89b-12d3-a456-556642440000",
     *     "name" : "Router",
     *     "type" : "Router",
     *     "config" : { },
     *     "routing" : {
     *       "matchingField" : "name",
     *       "routes" : [ {
     *         "matchingValue" : "Ada Lovelace",
     *         "default" : false,
     *         "subchain" : {
     *           "id" : "3b31e549-340f-47ce-8a71-d702685137f4",
     *           "name" : "Success Chain",
     *           "parsers" : [ {
     *             "id" : "123e4567-e89b-12d3-a456-556642440000",
     *             "name" : "Timestamp",
     *             "type" : "com.cloudera.parserchains.parsers.TimestampParser",
     *             "config" : {
     *               "outputField" : [ {
     *                 "outputField" : "processing_time"
     *               } ]
     *             }
     *           } ]
     *         }
     *       }, {
     *         "matchingValue" : "",
     *         "default" : true,
     *         "subchain" : {
     *           "id" : "cdb0729f-a929-4f3c-9cb7-675b57d10a73",
     *           "name" : "Default Chain",
     *           "parsers" : [ {
     *             "id" : "ceb95dd5-1e3f-41f2-bf60-ee2fe2c962c6",
     *             "name" : "Error",
     *             "type" : "com.cloudera.parserchains.parsers.AlwaysFailParser",
     *             "config" : { }
     *           } ]
     *         }
     *       } ]
     *     }
     *   } ]
     * }
     */
    @Multiline
    private String chainWithRouting;

    @Test
    void chainWithRouting() throws Exception {
        // build a CSV to parse
        final String nameField = "Ada Lovelace";
        final String addressField = "1600 Pennsylvania Ave";
        final String phoneField = "614-867-5309";
        final String toParse = StringUtils.join(new String[] { nameField, addressField, phoneField }, ",");

        // execute a parser chain
        ParserChainSchema schema = JSONUtils.INSTANCE.load(chainWithRouting, ParserChainSchema.class);
        ChainLink chain = buildChain(schema);
        ParserResult result = service.execute(chain, toParse);

        assertThat("Expected to have 1 input field.", result.getInput().size(), is(1));
        assertThat("Expected to have 5 output fields.", result.getOutput().size(), is(5));
        expectField(result.getInput(), "original_string", toParse);
        expectField(result.getOutput(), "original_string", toParse);
        expectField(result.getOutput(), "name", nameField);
        expectField(result.getOutput(), "address", addressField);
        expectField(result.getOutput(), "phone", phoneField);
        assertThat(result.getOutput().keySet(), hasItem("processing_time"));
        assertThat("Expected the 'info' type on success.",
                result.getLog().getType(), is(INFO_TYPE));
        assertThat("Expected the 'success' message on success.",
                result.getLog().getMessage(), is(DEFAULT_SUCCESS_MESSAGE));
        assertThat("Expected the parserId to be set to the last parser in the chain.",
                result.getLog().getParserId(), is("123e4567-e89b-12d3-a456-556642440000"));
    }

    private void expectField(Map<String, String> fields, String fieldName, String expectedValue) {
        assertThat(String.format("Expected a field that does not exist; %s=%s", fieldName, expectedValue),
                fields.get(fieldName), is(expectedValue));
    }
}
