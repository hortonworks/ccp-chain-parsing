package com.cloudera.parserchains.queryservice.model;

import com.cloudera.parserchains.core.catalog.ParserInfo;
import com.cloudera.parserchains.parsers.SyslogParser;
import com.cloudera.parserchains.queryservice.model.summary.ParserSummary;
import com.cloudera.parserchains.queryservice.model.summary.ParserSummaryMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserSummaryMapperTest {

    @Test
    void reform() {
        ParserInfo parserInfo = ParserInfo
                .builder()
                .name("Syslog")
                .description("Parses Syslog according to RFC 3164 and 5424.")
                .parserClass(SyslogParser.class)
                .build();
        ParserSummary expected = new ParserSummary()
                .setName("Syslog")
                .setDescription("Parses Syslog according to RFC 3164 and 5424.")
                .setId(ParserID.of(SyslogParser.class));
        assertEquals(expected, new ParserSummaryMapper().reform(parserInfo));
    }

    @Test
    void transform() {
        ParserSummary parserSummary = new ParserSummary()
                .setName("Syslog")
                .setDescription("Parses Syslog according to RFC 3164 and 5424.")
                .setId(ParserID.of(SyslogParser.class));
        ParserInfo expected = ParserInfo
                .builder()
                .name("Syslog")
                .description("Parses Syslog according to RFC 3164 and 5424.")
                .parserClass(SyslogParser.class)
                .build();
        assertEquals(expected, new ParserSummaryMapper().transform(parserSummary));
    }

    @Test
    void classNotFound() {
        ParserSummary parserSummary = new ParserSummary()
                .setName("Syslog")
                .setDescription("Parses Syslog according to RFC 3164 and 5424.")
                .setId("com.foo.class.does.not.exist.Parser");
        assertThrows(IllegalArgumentException.class, () -> new ParserSummaryMapper().transform(parserSummary));
    }

    @Test
    void classNotAParser() {
        ParserSummary parserSummary = new ParserSummary()
                .setName("Syslog")
                .setDescription("Parses Syslog according to RFC 3164 and 5424.")
                .setId("com.cloudera.parserchains.queryservice.model.ParserTypeMapperTest");
        assertThrows(IllegalArgumentException.class, () -> new ParserSummaryMapper().transform(parserSummary));
    }
}
