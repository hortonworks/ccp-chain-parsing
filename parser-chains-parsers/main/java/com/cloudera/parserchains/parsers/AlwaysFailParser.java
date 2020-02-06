package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.ConfigName;
import com.cloudera.parserchains.core.ConfigValue;
import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.MessageParser;
import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.RouterLink;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.cloudera.parserchains.parsers.ParserUtils.requireN;

/**
 * A {@link Parser} that always fails.
 *
 * <p>This can be used with a {@link RouterLink}
 * to flag when unexpected conditions are encountered in the data.
 */
@MessageParser(name="Always Fails", description = "A parser that always fails to indicate an error condition.")
public class AlwaysFailParser implements Parser {
    static final ConfigName errorConfig = ConfigName.of("error", false);
    private Throwable error;

    public AlwaysFailParser() {
        error = new IllegalStateException("Parsing error encountered");
    }

    @Override
    public Message parse(Message message) {
        return Message.builder()
                .withFields(message)
                .withError(error)
                .build();
    }

    public AlwaysFailParser withError(String message) {
        Objects.requireNonNull(message);
        error = new IllegalStateException(message);
        return this;
    }

    public AlwaysFailParser withError(Throwable error) {
        this.error = Objects.requireNonNull(error);
        return this;
    }

    @Override
    public List<FieldName> outputFields() {
        return Collections.emptyList();
    }

    @Override
    public List<ConfigName> validConfigurations() {
        return Collections.emptyList();
    }

    @Override
    public void configure(ConfigName configName, List<ConfigValue> configValues) {
        if(errorConfig.equals(configName)) {
            requireN(errorConfig, configValues, 1);
            withError(configValues.get(0).getValue());

        } else {
            throw new IllegalArgumentException(String.format("Unexpected configuration; name=%s", configName));
        }
    }

    Throwable getError() {
        return error;
    }
}
