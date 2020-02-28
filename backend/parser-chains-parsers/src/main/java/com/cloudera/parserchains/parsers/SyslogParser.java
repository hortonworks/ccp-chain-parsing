package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.Constants;
import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.FieldValue;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.catalog.MessageParser;
import com.cloudera.parserchains.core.config.ConfigDescriptor;
import com.cloudera.parserchains.core.config.ConfigKey;
import com.cloudera.parserchains.core.config.ConfigName;
import com.cloudera.parserchains.core.config.ConfigValue;
import com.github.palindromicity.syslog.SyslogParserBuilder;
import com.github.palindromicity.syslog.SyslogSpecification;
import com.github.palindromicity.syslog.dsl.ParseException;
import com.github.palindromicity.syslog.dsl.SyslogFieldKeys;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;

@MessageParser(
    name="Syslog",
    description="Parses Syslog according to RFC 3164 and 5424.")
public class SyslogParser implements Parser {
    private FieldName inputField;
    private SyslogSpecification specification;
    private Configurer configurer;

    public SyslogParser() {
        inputField = Constants.DEFAULT_INPUT_FIELD;
        configurer = new Configurer(this);
        specification = SyslogSpecification.RFC_5424;
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

        } catch(ParseException e) {
            output.withError(e);
        }
    }

    @Override
    public List<FieldName> outputFields() {
        if(SyslogSpecification.RFC_3164.equals(specification)) {
            return Arrays.asList(
                    FieldName.of(SyslogFieldKeys.MESSAGE.getField()),
                    FieldName.of(SyslogFieldKeys.HEADER_HOSTNAME.getField()),
                    FieldName.of(SyslogFieldKeys.HEADER_PRI.getField()),
                    FieldName.of(SyslogFieldKeys.HEADER_PRI_SEVERITY.getField()),
                    FieldName.of(SyslogFieldKeys.HEADER_PRI_FACILITY.getField()),
                    FieldName.of(SyslogFieldKeys.HEADER_TIMESTAMP.getField()));

        } else if (SyslogSpecification.RFC_5424.equals(specification)) {
            return Arrays.asList(
                    FieldName.of(SyslogFieldKeys.MESSAGE.getField()),
                    FieldName.of(SyslogFieldKeys.HEADER_APPNAME.getField()),
                    FieldName.of(SyslogFieldKeys.HEADER_HOSTNAME.getField()),
                    FieldName.of(SyslogFieldKeys.HEADER_PRI.getField()),
                    FieldName.of(SyslogFieldKeys.HEADER_PRI_SEVERITY.getField()),
                    FieldName.of(SyslogFieldKeys.HEADER_PRI_FACILITY.getField()),
                    FieldName.of(SyslogFieldKeys.HEADER_PROCID.getField()),
                    FieldName.of(SyslogFieldKeys.HEADER_TIMESTAMP.getField()),
                    FieldName.of(SyslogFieldKeys.HEADER_MSGID.getField()),
                    FieldName.of(SyslogFieldKeys.HEADER_VERSION.getField()));
        } else {
            throw new IllegalArgumentException("Unexpected specification: " + specification);
        }
    }

    @Override
    public List<ConfigDescriptor> validConfigurations() {
        return configurer.validConfigurations();
    }

    @Override
    public void configure(ConfigName name, Map<ConfigKey, ConfigValue> values) {
        configurer.configure(name, values);
    }

    /**
     * Handles configuration for the {@link SyslogParser}.
     */
    static class Configurer {
        static final ConfigKey inputFieldKey = ConfigKey.of("inputField");
        static final ConfigDescriptor inputFieldConfig = ConfigDescriptor
                .builder()
                .name("inputField")
                .description("Input Field")
                .acceptsValue(inputFieldKey, "The name of the input field to parse.")
                .isRequired(false)
                .build();

        static final ConfigKey specKey = ConfigKey.of("specification");
        static final ConfigDescriptor specConfig = ConfigDescriptor
                .builder()
                .name("specification")
                .description("Specification")
                .acceptsValue(specKey, "The Syslog specification; 'RFC_5424' or 'RFC_3164'. Defaults to 'RFC_5424'")
                .isRequired(false)
                .build();
        private SyslogParser parser;

        Configurer(SyslogParser parser) {
            this.parser = parser;
        }

        List<ConfigDescriptor> validConfigurations() {
            return Arrays.asList(inputFieldConfig, specConfig);
        }

        void configure(ConfigName name, Map<ConfigKey, ConfigValue> values) {
            if(inputFieldConfig.getName().equals(name)) {
                configureInputField(values);
            } else if(specConfig.getName().equals(name)) {
                configureSpec(values);
            } else {
                throw new IllegalArgumentException(String.format("Unexpected configuration; name=%s", name));
            }
        }

        private void configureSpec(Map<ConfigKey, ConfigValue> values) {
            Optional.ofNullable(values.get(specKey)).ifPresent(value -> {
                SyslogSpecification spec = SyslogSpecification.valueOf(value.get());
                parser.withSpecification(spec);
            });
        }

        private void configureInputField(Map<ConfigKey, ConfigValue> values) {
            Optional.ofNullable(values.get(inputFieldKey)).ifPresent(value -> {
                FieldName field = FieldName.of(value.get());
                parser.withInputField(field);
            });
        }
    }
}
