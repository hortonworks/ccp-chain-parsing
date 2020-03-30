package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.Constants;
import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.FieldValue;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.catalog.MessageParser;
import com.cloudera.parserchains.core.model.config.ConfigDescriptor;
import com.cloudera.parserchains.core.model.config.ConfigKey;
import com.cloudera.parserchains.core.model.config.ConfigName;
import com.cloudera.parserchains.core.model.config.ConfigValue;
import com.github.palindromicity.syslog.SyslogParserBuilder;
import com.github.palindromicity.syslog.SyslogSpecification;

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
    private static final SyslogSpecification DEFAULT_SYSLOG_SPEC = SyslogSpecification.RFC_5424;
    private FieldName inputField;
    private SyslogSpecification specification;
    private Configurer configurer;

    public SyslogParser() {
        inputField = Constants.DEFAULT_INPUT_FIELD;
        configurer = new Configurer(this);
        specification = DEFAULT_SYSLOG_SPEC;
    }

    public SyslogParser withSpecification(SyslogSpecification specification) {
        this.specification = Objects.requireNonNull(specification, "A valid specification is required.");
        return this;
    }

    public SyslogParser withInputField(FieldName inputField) {
        this.inputField = Objects.requireNonNull(inputField, "A valid input field is required.");
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

        } catch(Exception e) {
            output.withError(e);
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

    public FieldName getInputField() {
        return inputField;
    }

    public SyslogSpecification getSpecification() {
        return specification;
    }

    /**
     * Handles configuration for the {@link SyslogParser}.
     */
    static class Configurer {
        // input field definition
        static final ConfigKey inputFieldKey = ConfigKey.builder()
                .key("inputField")
                .label("Input Field")
                .description("The name of the input field to parse.")
                .defaultValue(Constants.DEFAULT_INPUT_FIELD.get())
                .build();
        static final ConfigDescriptor inputFieldConfig = ConfigDescriptor
                .builder()
                .name("inputField")
                .description("Input Field")
                .acceptsValue(inputFieldKey)
                .isRequired(false)
                .build();

        // specification definition
        static final ConfigKey specKey = ConfigKey.builder()
                .key("specification")
                .label("Specification")
                .description("The Syslog specification; 'RFC_5424' or 'RFC_3164'")
                .defaultValue(DEFAULT_SYSLOG_SPEC.toString())
                .build();
        static final ConfigDescriptor specConfig = ConfigDescriptor
                .builder()
                .name("specification")
                .description("Specification")
                .acceptsValue(specKey)
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
                throw new IllegalArgumentException(String.format("Unexpected configuration; name=%s", name.get()));
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
