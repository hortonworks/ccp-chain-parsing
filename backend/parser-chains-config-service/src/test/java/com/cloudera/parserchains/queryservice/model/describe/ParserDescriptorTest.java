package com.cloudera.parserchains.queryservice.model.describe;


import com.cloudera.parserchains.core.catalog.WidgetType;
import com.cloudera.parserchains.parsers.SyslogParser;
import com.cloudera.parserchains.core.utils.JSONUtils;
import com.cloudera.parserchains.core.model.define.ParserID;
import com.cloudera.parserchains.core.model.define.ParserName;
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
     *     "required" : true,
     *     "path" : "config",
     *     "multiple" : false
     *   }, {
     *     "name" : "inputField",
     *     "type" : "text",
     *     "label" : "Input Field",
     *     "description" : "The name of the input field.",
     *     "required" : true,
     *     "path" : "config",
     *     "multiple" : false,
     *     "defaultValue" : [ {
     *       "outputField" : "original_string"
     *     } ]
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
                .addConfiguration(new ConfigParamDescriptor()
                        .setName("outputField")
                        .setDescription("The name of the output field.")
                        .setLabel("Output Field")
                        .setPath("config")
                        .setRequired(true)
                        .setType(WidgetType.TEXT))
                .addConfiguration(new ConfigParamDescriptor()
                        .setName("inputField")
                        .setDescription("The name of the input field.")
                        .setLabel("Input Field")
                        .setPath("config")
                        .setRequired(true)
                        .setType(WidgetType.TEXT)
                        .addDefaultValue("outputField", "original_string"));
        String actual = JSONUtils.INSTANCE.toJSON(schema, true);
        assertThat(actual, equalToCompressingWhiteSpace(expectedJSON));
    }

}
