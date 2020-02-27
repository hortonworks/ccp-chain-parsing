package com.cloudera.parserchains.queryservice.model;


import com.cloudera.parserchains.parsers.SyslogParser;
import com.cloudera.parserchains.queryservice.common.utils.JSONUtils;
import com.cloudera.parserchains.queryservice.model.describe.ConfigDescriptor;
import com.cloudera.parserchains.queryservice.model.describe.ParserDescriptor;
import org.adrianwalker.multilinestring.Multiline;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualCompressingWhiteSpace.equalToCompressingWhiteSpace;

public class ParserDescriptorTest {

    /**
     * {
     *   "id" : "com.cloudera.parserchains.parsers.SyslogParser",
     *   "name" : "Syslog",
     *   "schemaItems" : [ {
     *     "name" : "outputField",
     *     "type" : "text",
     *     "label" : "Output Field",
     *     "description" : "The name of the output field.",
     *     "required" : "true",
     *     "path" : "config"
     *   }, {
     *     "name" : "inputField",
     *     "type" : "text",
     *     "label" : "Input Field",
     *     "description" : "The name of the input field.",
     *     "required" : "true",
     *     "path" : "config"
     *   } ]
     * }
     */
    @Multiline
    private String expectedJSON;

    @Test
    void toJSON() throws Exception {
        ParserDescriptor schema = new ParserDescriptor()
                .setParserID(ParserID.of(SyslogParser.class))
                .setParserName(ParserName.of("Syslog"))
                .addSchemaItem(new ConfigDescriptor()
                        .setName("outputField")
                        .setDescription("The name of the output field.")
                        .setLabel("Output Field")
                        .setPath("config")
                        .setRequired("true")
                        .setType("text"))
                .addSchemaItem(new ConfigDescriptor()
                        .setName("inputField")
                        .setDescription("The name of the input field.")
                        .setLabel("Input Field")
                        .setPath("config")
                        .setRequired("true")
                        .setType("text"));

        String actual = JSONUtils.INSTANCE.toJSON(schema, true);
        assertThat(actual, equalToCompressingWhiteSpace(expectedJSON));
    }

}