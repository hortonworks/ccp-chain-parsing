package com.cloudera.parserchains.queryservice.model.define;

import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.catalog.AnnotationBasedParserInfoBuilder;
import com.cloudera.parserchains.core.catalog.ParserInfo;
import com.cloudera.parserchains.core.catalog.ParserInfoBuilder;
import com.cloudera.parserchains.parsers.AlwaysFailParser;
import com.cloudera.parserchains.parsers.DelimitedTextParser;
import com.cloudera.parserchains.parsers.RenameFieldParser;
import com.cloudera.parserchains.parsers.SyslogParser;
import com.cloudera.parserchains.parsers.TimestampParser;
import com.cloudera.parserchains.queryservice.common.utils.JSONUtils;
import com.cloudera.parserchains.queryservice.model.ParserID;
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
    private String chainWithParsersExpectedJSON;

    @Test
    void chainWithParsersToJSON() throws Exception {
        // create a parser
        ParserSchema syslogParserSchema = createParser(SyslogParser.class)
                .setLabel("26bf648f-930e-44bf-a4de-bfd34ac16165")
                .addConfig("inputField",
                        new ConfigValueSchema()
                                .addValue("inputField", "input"))
                .addConfig("specification",
                        new ConfigValueSchema()
                                .addValue("specification", "RFC_5424"));

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
        assertThat(actual, equalToCompressingWhiteSpace(chainWithParsersExpectedJSON));
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
     *       "outputField" : [ {
     *         "fieldIndex" : "0",
     *         "fieldName" : "name"
     *       } ]
     *     },
     *     "outputs" : { }
     *   }, {
     *     "id" : "123e4567-e89b-12d3-a456-556642440000",
     *     "name" : "Router",
     *     "type" : "Router",
     *     "config" : { },
     *     "outputs" : { },
     *     "routing" : {
     *       "matchingField" : "name",
     *       "routes" : [ {
     *         "id" : "3b31e549-340f-47ce-8a71-d702685137f4",
     *         "name" : "successRoute",
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
     *             },
     *             "outputs" : { }
     *           } ]
     *         }
     *       }, {
     *         "id" : "cdb0729f-a929-4f3c-9cb7-675b57d10a73",
     *         "name" : "defaultRoute",
     *         "matchingValue" : "",
     *         "default" : true,
     *         "subchain" : {
     *           "id" : "cdb0729f-a929-4f3c-9cb7-675b57d10a73",
     *           "name" : "Default Chain",
     *           "parsers" : [ {
     *             "id" : "ceb95dd5-1e3f-41f2-bf60-ee2fe2c962c6",
     *             "name" : "Error",
     *             "type" : "com.cloudera.parserchains.parsers.AlwaysFailParser",
     *             "config" : { },
     *             "outputs" : { }
     *           } ]
     *         }
     *       } ]
     *     }
     *   } ]
     * }
     */
    @Multiline
    private String chainWithRoutingExpectedJSON;

    @Test
    void chainWithRoutingToJSON() throws Exception {
        // create the "success" route -> timestamp
        ParserSchema timestamper = createParser(TimestampParser.class)
                .setLabel("123e4567-e89b-12d3-a456-556642440000")
                .addConfig("outputField",
                        new ConfigValueSchema()
                                .addValue("outputField", "processing_time"));
        ParserChainSchema timestamperChain = new ParserChainSchema()
                .setId("3b31e549-340f-47ce-8a71-d702685137f4")
                .setName("Success Chain")
                .addParser(timestamper);
        RouteSchema successRoute = new RouteSchema()
                .setLabel("3b31e549-340f-47ce-8a71-d702685137f4")
                .setName(ParserName.of("successRoute"))
                .setDefault(false)
                .setMatchingValue("Ada Lovelace")
                .setSubChain(timestamperChain);

        // create the "default" route -> error
        ParserSchema error = createParser(AlwaysFailParser.class)
                .setLabel("ceb95dd5-1e3f-41f2-bf60-ee2fe2c962c6");
        ParserChainSchema errorChain = new ParserChainSchema()
                .setId("cdb0729f-a929-4f3c-9cb7-675b57d10a73")
                .setName("Default Chain")
                .addParser(error);
        RouteSchema defaultRoute = new RouteSchema()
                .setLabel("cdb0729f-a929-4f3c-9cb7-675b57d10a73")
                .setName(ParserName.of("defaultRoute"))
                .setDefault(true)
                .setMatchingValue("")
                .setSubChain(errorChain);

        // define the available routes
        RoutingSchema routingSchema = new RoutingSchema()
                .setMatchingField("name")
                .addRoute(successRoute)
                .addRoute(defaultRoute);
        ParserSchema routerSchema = new ParserSchema()
                .setId(ParserID.router())
                .setLabel("123e4567-e89b-12d3-a456-556642440000")
                .setName(ParserName.router())
                .setRouting(routingSchema);

        // create the main chain
        ParserSchema csvParser = createParser(DelimitedTextParser.class)
                .setLabel("26bf648f-930e-44bf-a4de-bfd34ac16165")
                .addConfig("outputField", new ConfigValueSchema()
                        .addValue("fieldName", "name")
                        .addValue("fieldIndex", "0"));
        ParserChainSchema mainChain = new ParserChainSchema()
                .setId("3b31e549-340f-47ce-8a71-d702685137f4")
                .setName("My Parser Chain")
                .addParser(csvParser)
                .addParser(routerSchema);

        String actual = JSONUtils.INSTANCE.toJSON(mainChain, true);
        assertThat(actual, equalToCompressingWhiteSpace(chainWithRoutingExpectedJSON));
    }

    private ParserSchema createParser(Class<? extends Parser> parserClass) {
        ParserInfo parserInfo = parserInfoBuilder.build(parserClass).get();
        ParserSummary parserSummary = new ParserSummaryMapper().reform(parserInfo);
        return new ParserSchema()
                .setId(parserSummary.getId())
                .setName(ParserName.of(parserInfo.getName()));
    }
}
