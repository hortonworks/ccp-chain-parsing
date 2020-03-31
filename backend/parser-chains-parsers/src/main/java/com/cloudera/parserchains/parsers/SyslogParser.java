package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.Constants;
import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.FieldValue;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.catalog.MessageParser;
import com.cloudera.parserchains.core.catalog.Configurable;
import com.github.palindromicity.syslog.SyslogParserBuilder;
import com.github.palindromicity.syslog.SyslogSpecification;

import java.util.Objects;

import static java.lang.String.format;

@MessageParser(
    name="Syslog",
    description="Parses Syslog according to RFC 3164 and 5424.")
public class SyslogParser implements Parser {
    private static final String DEFAULT_SYSLOG_SPEC = "RFC_5424";
    private FieldName inputField;
    private SyslogSpecification specification;

    public SyslogParser() {
        inputField = FieldName.of(Constants.DEFAULT_INPUT_FIELD);
        specification = SyslogSpecification.valueOf(DEFAULT_SYSLOG_SPEC);
    }

    @Configurable(
            key="specification",
            label="Specification",
            description="The Syslog specification; 'RFC_5424' or 'RFC_3164'",
            defaultValue=DEFAULT_SYSLOG_SPEC)
    public void withSpecification(String specification) {
        SyslogSpecification spec = SyslogSpecification.valueOf(specification);
        withSpecification(spec);
    }

    public SyslogParser withSpecification(SyslogSpecification specification) {
        this.specification = Objects.requireNonNull(specification, "A valid specification is required.");
        return this;
    }

    public SyslogSpecification getSpecification() {
        return specification;
    }

    @Configurable(key="inputField",
            label="Input Field",
            description="The name of the input field to parse.",
            defaultValue = Constants.DEFAULT_INPUT_FIELD)
    public SyslogParser withInputField(FieldName inputField) {
        this.inputField = Objects.requireNonNull(inputField, "A valid input field is required.");
        return this;
    }

    public FieldName getInputField() {
        return inputField;
    }

    @Override
    public Message parse(Message input) {
        Message.Builder output = Message.builder().withFields(input);
        if(inputField == null) {
            output.withError("Input Field has not been defined.");

        } else if(!input.getField(inputField).isPresent()) {
            output.withError(format("Message missing expected input field '%s'", inputField.toString()));

        } else {
            input.getField(inputField).ifPresent(val -> doParse(val.toString(), output));
        }
        return output.build();
    }

    private void doParse(String valueToParse, Message.Builder output) {
        try {
            new SyslogParserBuilder()
                    .forSpecification(specification)
                    .build()
                    .parseLine(valueToParse)
                    .forEach((k, v) -> output.addField(FieldName.of(k), FieldValue.of(v.toString())));

        } catch(Exception e) {
            output.withError(e);
        }
    }
}
