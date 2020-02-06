package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.FieldValue;
import com.cloudera.parserchains.core.Message;
import com.github.palindromicity.syslog.SyslogSpecification;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SyslogParserTest {

    private static final String SYSLOG_5424 = "<14>1 2014-06-20T09:14:07+00:00 loggregator"
            + " d0602076-b14a-4c55-852a-981e7afeed38 DEA MSG-01"
            + " [exampleSDID@32473 iut=\"3\" eventSource=\"Application\" eventID=\"1011\"]"
            + "[exampleSDID@32480 iut=\"4\" eventSource=\"Other Application\" eventID=\"2022\"] "
            + "Removing instance";

    @Test
    void parse5424() {
        Message input = Message.builder()
                .addField(FieldName.of("original_string"), FieldValue.of(SYSLOG_5424))
                .build();
        Message output = new SyslogParser()
                .withInputField(FieldName.of("original_string"))
                .parse(input);
        Message expected = Message.builder()
                .withFields(input)
                .addField(FieldName.of("syslog.header.pri"), FieldValue.of("14"))
                .addField(FieldName.of("syslog.header.version"), FieldValue.of("1"))
                .addField(FieldName.of("syslog.header.appName"), FieldValue.of("d0602076-b14a-4c55-852a-981e7afeed38"))
                .addField(FieldName.of("syslog.header.procId"), FieldValue.of("DEA"))
                .addField(FieldName.of("syslog.header.timestamp"), FieldValue.of("2014-06-20T09:14:07+00:00"))
                .addField(FieldName.of("syslog.header.facility"), FieldValue.of("1"))
                .addField(FieldName.of("syslog.header.hostName"), FieldValue.of("loggregator"))
                .addField(FieldName.of("syslog.header.severity"), FieldValue.of("6"))
                .addField(FieldName.of("syslog.header.msgId"), FieldValue.of("MSG-01"))
                .addField(FieldName.of("syslog.structuredData.exampleSDID@32473.iut"), FieldValue.of("3"))
                .addField(FieldName.of("syslog.structuredData.exampleSDID@32473.eventID"), FieldValue.of("1011"))
                .addField(FieldName.of("syslog.structuredData.exampleSDID@32473.eventSource"), FieldValue.of("Application"))
                .addField(FieldName.of("syslog.structuredData.exampleSDID@32480.iut"), FieldValue.of("4"))
                .addField(FieldName.of("syslog.structuredData.exampleSDID@32480.eventID"), FieldValue.of("2022"))
                .addField(FieldName.of("syslog.structuredData.exampleSDID@32480.eventSource"), FieldValue.of("Other Application"))
                .addField(FieldName.of("syslog.message"), FieldValue.of("Removing instance"))
                .build();
        assertEquals(expected, output);
    }

    private static final String SYSLOG_3164 = "<181>2018-09-14T00:54:09+00:00 lzpqrst-admin.in.mycompany.com.lg " +
            "CISE_RADIUS_Accounting 0018032501 1 0 2018-09-14 10:54:09.095 +10:00";

    @Test
    void parse3164() {
        Message input = Message.builder()
                .addField(FieldName.of("original_string"), FieldValue.of(SYSLOG_3164))
                .build();
        Message output = new SyslogParser()
                .withInputField(FieldName.of("original_string"))
                .withSpecification(SyslogSpecification.RFC_3164)
                .parse(input);
        Message expected = Message.builder()
                .withFields(input)
                .addField(FieldName.of("syslog.header.pri"), FieldValue.of("181"))
                .addField(FieldName.of("syslog.header.severity"), FieldValue.of("5"))
                .addField(FieldName.of("syslog.header.timestamp"), FieldValue.of("2018-09-14T00:54:09+00:00"))
                .addField(FieldName.of("syslog.header.facility"), FieldValue.of("22"))
                .addField(FieldName.of("syslog.header.hostName"), FieldValue.of("lzpqrst-admin.in.mycompany.com.lg"))
                .addField(FieldName.of("syslog.message"), FieldValue.of("CISE_RADIUS_Accounting 0018032501 1 0 2018-09-14 10:54:09.095 +10:00"))
                .build();
        assertEquals(expected, output);
    }

    @Test
    void parseError() {
        Message input = Message.builder()
                .addField(FieldName.of("original_string"), FieldValue.of("<1> malformed input"))
                .build();
        Message output = new SyslogParser()
                .withInputField(FieldName.of("original_string"))
                .parse(input);

        // expect an error to be reported and the same fields
        assertTrue(output.getError().isPresent());
        assertEquals(input.getFields(), output.getFields());
    }

    @Test
    void inputFieldNotDefined() {
        Message input = Message.builder().build();
        assertThrows(IllegalStateException.class, () -> new SyslogParser().parse(input));
    }
}
