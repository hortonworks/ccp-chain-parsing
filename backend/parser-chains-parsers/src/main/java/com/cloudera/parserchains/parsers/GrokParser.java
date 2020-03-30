package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.Constants;
import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.catalog.MessageParser;
import com.cloudera.parserchains.core.model.config.ConfigDescriptor;
import com.cloudera.parserchains.core.model.config.ConfigKey;
import com.cloudera.parserchains.core.model.config.ConfigName;
import com.cloudera.parserchains.core.model.config.ConfigValue;
import io.krakens.grok.api.Grok;
import io.krakens.grok.api.GrokCompiler;

import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

@MessageParser(
        name = "Grok",
        description = "Parses a message using Grok expressions."
)
public class GrokParser implements Parser {
    private static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.UTC;
    private FieldName inputField;
    private ZoneOffset zoneOffset;
    private Configurer configurer;
    private GrokCompiler grokCompiler;
    private List<Grok> grokExpressions;

    public GrokParser() {
        inputField = Constants.DEFAULT_INPUT_FIELD;
        zoneOffset = DEFAULT_ZONE_OFFSET;
        configurer = new Configurer(this);
        grokCompiler = GrokCompiler.newInstance();
        grokCompiler.registerDefaultPatterns();
        grokExpressions = new ArrayList<>();
    }

    @Override
    public Message parse(Message input) {
        Message.Builder output = Message.builder().withFields(input);
        if(inputField == null) {
            output.withError("Input Field has not been defined.");
        } else if(!input.getField(inputField).isPresent()) {
            output.withError(format("Missing expected input field '%s'", inputField.toString()));
        } else {
            input.getField(inputField).ifPresent(val -> doParse(val.toString(), output));
        }
        return output.build();
    }

    private void doParse(String textToParse, Message.Builder output) {
        for(Grok grokPattern: grokExpressions) {
            grokPattern.match(textToParse)
                    .capture()
                    .entrySet()
                    .stream()
                    .filter(e -> e.getKey() != null && e.getValue() != null)
                    .forEach(e -> output.addField(e.getKey(), e.getValue().toString()));
        }
    }

    public GrokParser expression(String grokExpression) {
        Grok grok = grokCompiler.compile(grokExpression, zoneOffset, false);
        this.grokExpressions.add(grok);
        return this;
    }

    public List<Grok> getGrokExpressions() {
        return Collections.unmodifiableList(grokExpressions);
    }

    public GrokParser inputField(FieldName inputField) {
        this.inputField = inputField;
        return this;
    }

    public FieldName getInputField() {
        return inputField;
    }

    public GrokParser zoneOffset(ZoneOffset zoneOffset) {
        this.zoneOffset = zoneOffset;
        return this;
    }

    public ZoneOffset getZoneOffset() {
        return zoneOffset;
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
     * Responsible for configuring a {@link GrokParser}.
     */
    static class Configurer {
        // input field
        static final ConfigKey inputFieldKey = ConfigKey.builder()
                .key("inputField")
                .label("Input Field")
                .description("The name of the input field to parse.")
                .defaultValue(Constants.DEFAULT_INPUT_FIELD.get())
                .build();
        static final ConfigDescriptor inputFieldConfig = ConfigDescriptor
                .builder()
                .acceptsValue(inputFieldKey)
                .isRequired(false)
                .build();

        // grok expressions
        static final ConfigKey expressionKey = ConfigKey.builder()
                .key("grokExpression")
                .label("Grok Expression(s)")
                .description("Define a grok expression.")
                .build();
        static final ConfigDescriptor expressionConfig = ConfigDescriptor
                .builder()
                .name("Grok Expression(s)")
                .description("Define a grok expression.")
                .acceptsValue(expressionKey)
                .isRequired(true)
                .build();

        // zone offset
        static final ConfigKey zoneOffsetKey = ConfigKey.builder()
                .key("zoneOffset")
                .label("Zone Offset")
                .description("Set the zone offset. For example \"+02:00\".")
                .defaultValue(DEFAULT_ZONE_OFFSET.getDisplayName(TextStyle.FULL, Locale.getDefault()))
                .build();
        static final ConfigDescriptor zoneOffsetConfig = ConfigDescriptor
                .builder()
                .name("Zone Offset")
                .description("Set the zone offset. For example \"+02:00\".")
                .acceptsValue(zoneOffsetKey)
                .isRequired(false)
                .build();

        private GrokParser parser;

        public Configurer(GrokParser parser) {
            this.parser = parser;
        }

        public List<ConfigDescriptor> validConfigurations() {
            return Arrays.asList(inputFieldConfig, expressionConfig, zoneOffsetConfig);
        }

        public void configure(ConfigName name, Map<ConfigKey, ConfigValue> values) {
            if(inputFieldConfig.getName().equals(name)) {
                configureInput(values);
            } else if(expressionConfig.getName().equals(name)) {
                configureGrokExpression(values);
            } else if(zoneOffsetConfig.getName().equals(name)) {
                configureZoneOffset(values);
            } else {
                throw new IllegalArgumentException(String.format("Unexpected configuration; name=%s", name.get()));
            }
        }

        private void configureInput(Map<ConfigKey, ConfigValue> values) {
            Optional.ofNullable(values.get(inputFieldKey)).ifPresent(value -> {
                FieldName inputField = FieldName.of(value.get());
                parser.inputField(inputField);
            });
        }

        private void configureGrokExpression(Map<ConfigKey, ConfigValue> values) {
            Optional.ofNullable(values.get(expressionKey)).ifPresent(value -> {
                String expression = value.get();
                parser.expression(expression);
            });
        }

        private void configureZoneOffset(Map<ConfigKey, ConfigValue> values) {
            Optional.ofNullable(values.get(zoneOffsetKey)).ifPresent(value -> {
                String offset = value.get();
                parser.zoneOffset(ZoneOffset.of(offset));
            });
        }
    }
}
