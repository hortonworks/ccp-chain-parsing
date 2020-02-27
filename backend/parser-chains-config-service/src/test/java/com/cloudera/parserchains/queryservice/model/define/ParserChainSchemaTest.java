package com.cloudera.parserchains.queryservice.model.define;

import com.cloudera.parserchains.core.catalog.AnnotationBasedParserInfoBuilder;
import com.cloudera.parserchains.core.catalog.ParserInfo;
import com.cloudera.parserchains.core.catalog.ParserInfoBuilder;
import com.cloudera.parserchains.parsers.RenameFieldParser;
import com.cloudera.parserchains.parsers.SyslogParser;
import com.cloudera.parserchains.queryservice.common.utils.JSONUtils;
import com.cloudera.parserchains.queryservice.model.ParserName;
import com.cloudera.parserchains.queryservice.model.summary.ParserSummary;
import com.cloudera.parserchains.queryservice.model.summary.ParserSummaryMapper;
import org.adrianwalker.multilinestring.Multiline;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualCompressingWhiteSpace.equalToCompressingWhiteSpace;

public class ParserChainSchemaTest {
    private ParserInfoBuilder parserInfoBuilder = new AnnotationBasedParserInfoBuilder();

    /**
     * {
     *   "id" : "3b31e549-340f-47ce-8a71-d702685137f4",
     *   "name" : "My Parser Chain",
     *   "parsers" : [ {
     *     "id" : "26bf648f-930e-44bf-a4de-bfd34ac16165",
     *     "name" : "Syslog",
     *     "type" : "com.cloudera.parserchains.parsers.SyslogParser",
     *     "config" : {
     *       "inputField" : [ {
     *         "inputField" : "input"
     *       } ],
     *       "specification" : [ {
     *         "specification" : "RFC_5424"
     *       } ]
     *     },
     *     "outputs" : { }
     *   }, {
     *     "id" : "bdf7d8be-50b1-4998-8b3f-f525d1e95931",
     *     "name" : "Rename Field(s)",
     *     "type" : "com.cloudera.parserchains.parsers.RenameFieldParser",
     *     "config" : {
     *       "fieldToRename" : [ {
     *         "from" : "syslog.header.timestamp",
     *         "to" : "timestamp"
     *       }, {
     *         "from" : "syslog.header.hostName",
     *         "to" : "host"
     *       } ]
     *     },
     *     "outputs" : { }
     *   } ]
     * }
     */
    @Multiline
    private String expectedJSON;

    @Test
    void toJSON() throws Exception {
        // create a parser
        ParserInfo syslogInfo = parserInfoBuilder.build(SyslogParser.class).get();
        ParserSummary syslogType = new ParserSummaryMapper()
                .reform(syslogInfo);
        ParserSchema syslogParserSchema = new ParserSchema()
                .setId(syslogType.getId())
                .setLabel("26bf648f-930e-44bf-a4de-bfd34ac16165")
                .setName(ParserName.of(syslogInfo.getName()))
                .addConfig("inputField",
                        new ConfigValueSchema().addValue("inputField", "input"))
                .addConfig("specification",
                        new ConfigValueSchema().addValue("specification", "RFC_5424"));

        // create another parser
        ParserInfo renameInfo = parserInfoBuilder.build(RenameFieldParser.class).get();
        ParserSummary renameType = new ParserSummaryMapper()
                .reform(renameInfo);
        ParserSchema renameParserSchema = new ParserSchema()
                .setId(renameType.getId())
                .setLabel("bdf7d8be-50b1-4998-8b3f-f525d1e95931")
                .setName(ParserName.of(renameInfo.getName()))
                .addConfig("fieldToRename",
                        new ConfigValueSchema()
                                .addValue("from", "syslog.header.timestamp")
                                .addValue("to", "timestamp"))
                .addConfig("fieldToRename",
                        new ConfigValueSchema()
                                .addValue("from", "syslog.header.hostName")
                                .addValue("to", "host"));

        // create the chain
        ParserChainSchema chain = new ParserChainSchema()
                .setId("3b31e549-340f-47ce-8a71-d702685137f4")
                .setName("My Parser Chain")
                .addParser(syslogParserSchema)
                .addParser(renameParserSchema);

        String actual = JSONUtils.INSTANCE.toJSON(chain, true);
        assertThat(actual, equalToCompressingWhiteSpace(expectedJSON));
    }
}
