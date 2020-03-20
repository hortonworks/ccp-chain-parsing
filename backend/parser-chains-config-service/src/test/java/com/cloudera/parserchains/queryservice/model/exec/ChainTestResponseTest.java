package com.cloudera.parserchains.queryservice.model.exec;

import com.cloudera.parserchains.core.utils.JSONUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.adrianwalker.multilinestring.Multiline;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualCompressingWhiteSpace.equalToCompressingWhiteSpace;

public class ChainTestResponseTest {

    /**
     * {
     *   "results" : [ {
     *     "input" : {
     *       "original_string" : "foo, bar, baz"
     *     },
     *     "output" : {
     *       "original_string" : "foo, bar, baz",
     *       "timestamp" : "1584721517455"
     *     },
     *     "log" : {
     *       "type" : "info",
     *       "message" : "success",
     *       "parserId" : "74d10881-ae37-4c90-95f5-ae0c10aae1f4"
     *     }
     *   }, {
     *     "input" : {
     *       "original_string" : "foo, bar, baz"
     *     },
     *     "output" : {
     *       "original_string" : "foo, bar, baz",
     *       "timestamp" : "1584721517458"
     *     },
     *     "log" : {
     *       "type" : "info",
     *       "message" : "success",
     *       "parserId" : "3b31e549-340f-47ce-8a71-d702685137f4"
     *     }
     *   } ]
     * }
     */
    @Multiline
    private String expected;

    @Test
    void toJSON() throws JsonProcessingException {
        ChainTestResponse response = new ChainTestResponse()
                .addResult(new ParserResult()
                        .addInput("original_string", "foo, bar, baz")
                        .addOutput("original_string", "foo, bar, baz")
                        .addOutput("timestamp", "1584721517455")
                        .setLog(new ResultLog()
                                .setMessage("success")
                                .setParserId("74d10881-ae37-4c90-95f5-ae0c10aae1f4")
                                .setType("info")
                        )
                )
                .addResult(new ParserResult()
                        .addInput("original_string", "foo, bar, baz")
                        .addOutput("original_string", "foo, bar, baz")
                        .addOutput("timestamp", "1584721517458")
                        .setLog(new ResultLog()
                                .setMessage("success")
                                .setParserId("3b31e549-340f-47ce-8a71-d702685137f4")
                                .setType("info")
                        )
                );
        String actual = JSONUtils.INSTANCE.toJSON(response, true);
        assertThat(actual, equalToCompressingWhiteSpace(expected));
    }
}
