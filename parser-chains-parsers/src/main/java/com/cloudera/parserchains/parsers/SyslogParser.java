package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.ConfigName;
import com.cloudera.parserchains.core.ConfigValue;
import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.FieldValue;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.MessageParser;
import com.cloudera.parserchains.core.Parser;
import com.github.palindromicity.syslog.SyslogParserBuilder;
import com.github.palindromicity.syslog.SyslogSpecification;
import com.github.palindromicity.syslog.dsl.ParseException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;
import static com.cloudera.parserchains.parsers.ParserUtils.requireN;

@MessageParser(name="Syslog", description="Parses Syslog according to RFC 3164 and 5424.")
public class SyslogParser implements Parser {
    public static final ConfigName inputFieldConfig = ConfigName.of("inputField", false);
    public static final ConfigName specConfig = ConfigName.of("specification", false);

    private FieldName inputField;
    private SyslogSpecification specification;

    public SyslogParser() {
        this.specification = SyslogSpecification.RFC_5424;
    }

    public SyslogParser withSpecification(SyslogSpecification specification) {
        this.specification = Objects.requireNonNull(specification);
        return this;
    }

    public SyslogParser withInputField(FieldName inputField) {
        this.inputField = Objects.requireNonNull(inputField);
        return this;
    }

    @Override
    public Message parse(Message input) {
        if(inputField == null) {
            throw new IllegalStateException("Input field has not been defined.");
        }
        Message.Builder output = Message.builder().withFields(input);
        Optional<FieldValue> value = input.getField(inputField);
        if(value.isPresent()) {
            doParse(output, value.get().toString());

        } else {
            output.withError(format("Message does not contain input field '%s'", inputField.toString()));
        }

        return output.build();
    }

    private void doParse(Message.Builder output, String value) {
        try {
            new SyslogParserBuilder()
                    .forSpecification(specification)
                    .build()
                    .parseLine(value)
                    .forEach((k, v) -> output.addField(FieldName.of(k), FieldValue.of(v.toString())));

        } catch(ParseException e) {
            output.withError(e);
        }
    }

    @Override
    public List<FieldName> outputFields() {
        // TODO implement me
        return Collections.emptyList();
    }

    @Override
    public List<ConfigName> validConfigurations() {
        return Arrays.asList(inputFieldConfig, specConfig);
    }

    @Override
    public void configure(ConfigName configName, List<ConfigValue> configValues) {
        if(inputFieldConfig.equals(configName)) {
            requireN(inputFieldConfig, configValues, 1);
            String inputFieldArg = configValues.get(0).getValue();
            withInputField(FieldName.of(inputFieldArg));

        } else if(specConfig.equals(configName)) {
            requireN(specConfig, configValues, 1);
            String specArg = configValues.get(0).getValue();
            withSpecification(SyslogSpecification.valueOf(specArg));

        } else {
            throw new IllegalArgumentException(String.format("Unexpected configuration; name=%s", configName));
        }
    }
}
