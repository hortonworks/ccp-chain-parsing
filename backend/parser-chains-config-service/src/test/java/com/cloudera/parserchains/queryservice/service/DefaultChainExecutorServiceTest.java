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
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;

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
    void execute() throws IOException {
        // build a CSV to parse
        final String nameField = "Jane Doe";
        final String addressField = "1600 Pennsylvania Ave";
        final String phoneField = "614-867-5309";
        final String toParse = StringUtils.join(new String[] { nameField, addressField, phoneField }, ",");

        // execute a parser chain
        ParserChainSchema schema = JSONUtils.INSTANCE.load(parserChainJSON, ParserChainSchema.class);
        List<ParserResult> results = service.execute(schema, toParse);

        assertThat("Expected 1 result for each parser", results.size(), is(1));
        ParserResult result = results.get(0);
        assertThat("Expected to have 1 input field.", result.getInput().size(), is(1));
        assertThat("Expected to have 4 output fields.", result.getOutput().size(), is(4));
        expectField(result.getInput(), "original_string", toParse);
        expectField(result.getOutput(), "original_string", toParse);
        expectField(result.getOutput(), "name", nameField);
        expectField(result.getOutput(), "address", addressField);
        expectField(result.getOutput(), "phone", phoneField);
    }

    private void expectField(Map<String, String> fields, String fieldName, String expectedValue) {
        assertThat(String.format("Expected a field that does not exist; %s=%s", fieldName, expectedValue),
                fields.get(fieldName), is(expectedValue));
    }
}
