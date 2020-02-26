package com.cloudera.parserchains.queryservice.model;

import com.cloudera.parserchains.queryservice.common.utils.JSONUtils;
import com.cloudera.parserchains.queryservice.model.summary.ParserSummary;
import org.adrianwalker.multilinestring.Multiline;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualCompressingWhiteSpace.equalToCompressingWhiteSpace;

public class ParserSummaryTest {

    /**
     * {
     *  "id" : "com.cloudera.parserchains.parsers.SyslogParser",
     *  "name" : "Syslog"
     * }
     */
    @Multiline
    private String expectedJSON;

    @Test
    void toJSON() throws Exception {
        ParserSummary parserSummary = new ParserSummary()
                .setName("Syslog")
                .setDescription("Parses Syslog according to RFC 3164 and 5424.")
                .setId("com.cloudera.parserchains.parsers.SyslogParser");

        String actual = JSONUtils.INSTANCE.toJSON(parserSummary, true);
        assertThat(actual, equalToCompressingWhiteSpace(expectedJSON));
    }
}
