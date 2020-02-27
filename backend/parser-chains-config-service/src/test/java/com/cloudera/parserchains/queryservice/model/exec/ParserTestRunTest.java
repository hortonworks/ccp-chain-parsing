package com.cloudera.parserchains.queryservice.model.exec;

import com.cloudera.parserchains.core.catalog.AnnotationBasedParserInfoBuilder;
import com.cloudera.parserchains.core.catalog.ParserInfo;
import com.cloudera.parserchains.core.catalog.ParserInfoBuilder;
import com.cloudera.parserchains.parsers.DelimitedTextParser;
import com.cloudera.parserchains.parsers.SyslogParser;
import com.cloudera.parserchains.parsers.TimestampParser;
import com.cloudera.parserchains.queryservice.common.utils.JSONUtils;
import com.cloudera.parserchains.queryservice.model.ParserName;
import com.cloudera.parserchains.queryservice.model.define.ConfigValueSchema;
import com.cloudera.parserchains.queryservice.model.define.ParserChainSchema;
import com.cloudera.parserchains.queryservice.model.define.ParserSchema;
import com.cloudera.parserchains.queryservice.model.summary.ParserSummary;
import com.cloudera.parserchains.queryservice.model.summary.ParserSummaryMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.adrianwalker.multilinestring.Multiline;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualCompressingWhiteSpace.equalToCompressingWhiteSpace;

public class ParserTestRunTest {
    private ParserInfoBuilder parserInfoBuilder = new AnnotationBasedParserInfoBuilder();

    /**
     * {
     *   "sampleData" : {
     *     "type" : "manual",
     *     "source" : "Marie, Curie"
     *   },
     *   "chainConfig" : {
     *     "id" : "3b31e549-340f-47ce-8a71-d702685137f4",
     *     "name" : "My Parser Chain",
     *     "parsers" : [ {
     *       "id" : "3b31e549-340f-47ce-8a71-d702685137f4",
     *       "name" : "Delimited Text",
     *       "type" : "com.cloudera.parserchains.parsers.DelimitedTextParser",
     *       "config" : {
     *         "outputField" : [ {
     *           "fieldIndex" : "0",
     *           "fieldName" : "firstName"
     *         }, {
     *           "fieldIndex" : "1",
     *           "fieldName" : "lastName"
     *         } ]
     *       },
     *       "outputs" : { }
     *     }, {
     *       "id" : "74d10881-ae37-4c90-95f5-ae0c10aae1f4",
     *       "name" : "Timestamp",
     *       "type" : "com.cloudera.parserchains.parsers.TimestampParser",
     *       "config" : {
     *         "outputField" : [ {
     *           "outputField" : "timestamp"
     *         } ]
     *       },
     *       "outputs" : { }
     *     } ]
     *   }
     *}
     */
    @Multiline
    private String expected;

    @Test
    void toJSON() throws JsonProcessingException {
        // create a delimited text parser
        ParserInfo csvInfo = parserInfoBuilder.build(DelimitedTextParser.class).get();
        ParserSummary csvType = new ParserSummaryMapper()
                .reform(csvInfo);
        ParserSchema csvParserSchema = new ParserSchema()
                .setId(csvType.getId())
                .setLabel("3b31e549-340f-47ce-8a71-d702685137f4")
                .setName(ParserName.of(csvInfo.getName()))
                .addConfig("outputField",
                        new ConfigValueSchema()
                                .addValue("fieldName", "firstName")
                                .addValue("fieldIndex", "0"))
                .addConfig("outputField",
                        new ConfigValueSchema()
                                .addValue("fieldName", "lastName")
                                .addValue("fieldIndex", "1"));

        // create a timestamp parser
        ParserInfo timestampInfo = parserInfoBuilder.build(TimestampParser.class).get();
        ParserSummary timestampType = new ParserSummaryMapper()
                .reform(timestampInfo);
        ParserSchema timestampParserSchema = new ParserSchema()
                .setId(timestampType.getId())
                .setLabel("74d10881-ae37-4c90-95f5-ae0c10aae1f4")
                .setName(ParserName.of(timestampInfo.getName()))
                .addConfig("outputField",
                        new ConfigValueSchema().addValue("outputField", "timestamp"));

        // create the parser chain
        ParserChainSchema parserChainSchema = new ParserChainSchema()
                .setId("3b31e549-340f-47ce-8a71-d702685137f4")
                .setName("My Parser Chain")
                .addParser(csvParserSchema)
                .addParser(timestampParserSchema);

        ParserTestRun testRun = new ParserTestRun();
        testRun.setParserChainSchema(parserChainSchema);
        testRun.setSampleData(new ParserTestRun.SampleData()
                .setSource("Marie, Curie")
                .setType("manual"));

        String actual = JSONUtils.INSTANCE.toJSON(testRun, true);
        assertThat(actual, equalToCompressingWhiteSpace(expected));
    }
}
