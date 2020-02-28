package com.cloudera.parserchains.queryservice.service;

import com.cloudera.parserchains.core.ReflectiveParserBuilder;
import com.cloudera.parserchains.core.catalog.ClassIndexParserCatalog;
import com.cloudera.parserchains.queryservice.common.utils.JSONUtils;
import com.cloudera.parserchains.queryservice.model.define.ParserChainSchema;
import com.cloudera.parserchains.queryservice.model.exec.ParserResult;
import org.adrianwalker.multilinestring.Multiline;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DefaultChainExecutorServiceTest {
    private DefaultChainExecutorService service;

    @BeforeEach
    void beforeEach() {
        service = new DefaultChainExecutorService(new ReflectiveParserBuilder(), new ClassIndexParserCatalog());
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
     *         "outputField" : [ {
     *           "outputField": "processing_time"
     *         }]
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
    private String parserChainJSON;

    @Test
    void success() throws IOException {
        // build a CSV to parse
        final String nameField = "Jane Doe";
        final String addressField = "1600 Pennsylvania Ave";
        final String phoneField = "614-867-5309";
        final String toParse = StringUtils.join(new String[] { nameField, addressField, phoneField }, ",");

        // execute a parser chain
        ParserChainSchema schema = JSONUtils.INSTANCE.load(parserChainJSON, ParserChainSchema.class);
        ParserResult result = service.execute(schema, toParse);

        assertThat("Expected to have 1 input field.", result.getInput().size(), is(1));
        assertThat("Expected to have 5 output fields.", result.getOutput().size(), is(5));
        expectField(result.getInput(), "original_string", toParse);
        expectField(result.getOutput(), "original_string", toParse);
        expectField(result.getOutput(), "name", nameField);
        expectField(result.getOutput(), "address", addressField);
        expectField(result.getOutput(), "phone", phoneField);
        assertThat(result.getOutput().keySet(), hasItem("processing_time"));
        assertThat("Expected the 'info' type on success.",
                result.getLog().getType(), is(DefaultChainExecutorService.INFO_TYPE));
        assertThat("Expected the 'success' message on success.",
                result.getLog().getMessage(), is(DefaultChainExecutorService.SUCCESS_MESSAGE));
        assertThat("Expected the parserId to be set to the last parser in the chain.",
                result.getLog().getParserId(), is("3b31e549-340f-47ce-8a71-d702685137f4"));
    }

    @Test
    void error() throws IOException {
        // build a CSV to parse. there are not enough fields, which should result in an error
        final String nameField = "Jane Doe";
        final String toParse = StringUtils.join(new String[] { nameField }, ",");

        // execute a parser chain
        ParserChainSchema schema = JSONUtils.INSTANCE.load(parserChainJSON, ParserChainSchema.class);
        ParserResult result = service.execute(schema, toParse);

        assertThat("Expected to have 1 input field.", result.getInput().size(), is(1));
        assertThat("Expected to have 3 output fields.", result.getOutput().size(), is(3));
        expectField(result.getInput(), "original_string", toParse);
        expectField(result.getOutput(), "original_string", toParse);
        expectField(result.getOutput(), "name", nameField);
        assertThat(result.getOutput().keySet(), hasItem("processing_time"));
        assertThat("Expected the 'error' type when an error occurs.",
                result.getLog().getType(), is(DefaultChainExecutorService.ERROR_TYPE));
        assertThat("Expected the error message to be included in the result.",
                result.getLog().getMessage(), is("IllegalStateException: Found 1 column(s), index 2 does not exist."));
    }


    /**
     * {
     *     "id" : "3b31e549-340f-47ce-8a71-d702685137f4",
     *     "name" : "My Parser Chain",
     *     "parsers" : [ {
     *       "id" : "8673f8f4-a308-4689-822c-0b01477ef378",
     *       "name" : "Bad Parser",
     *       "type" : "com.cloudera.parserchains.queryservice.service.MisbehavingParser",
     *       "config" : { },
     *       "outputs" : { }
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
        ParserResult result = service.execute(schema, textToParse);

        expectField(result.getInput(), "original_string", textToParse);
        assertThat("Expected the 'error' type to indicate an error was caught and reported.",
                result.getLog().getType(), is(DefaultChainExecutorService.ERROR_TYPE));
    }

    private void expectField(Map<String, String> fields, String fieldName, String expectedValue) {
        assertThat(String.format("Expected a field that does not exist; %s=%s", fieldName, expectedValue),
                fields.get(fieldName), is(expectedValue));
    }
}
