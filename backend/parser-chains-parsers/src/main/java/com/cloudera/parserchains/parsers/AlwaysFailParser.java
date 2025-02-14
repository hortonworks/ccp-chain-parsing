package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.RouterLink;
import com.cloudera.parserchains.core.catalog.MessageParser;
import com.cloudera.parserchains.core.catalog.Configurable;
import com.cloudera.parserchains.core.catalog.Parameter;
import org.apache.commons.lang.StringUtils;

import java.util.Objects;

/**
 * A {@link Parser} that always fails.
 *
 * <p>This can be used with a {@link RouterLink}
 * to flag when unexpected conditions are encountered in the data.
 */
@MessageParser(
    name="Error",
    description = "Always results in an error. Can be used with a router to flag unexpected data.")
public class AlwaysFailParser implements Parser {
    private static final String DEFAULT_ERROR_MESSAGE = "Parsing error encountered";
    private Throwable error;

    public AlwaysFailParser() {
        error = new IllegalStateException(DEFAULT_ERROR_MESSAGE);
    }

    @Override
    public Message parse(Message message) {
        return Message.builder()
                .withFields(message)
                .withError(error)
                .build();
    }

    @Configurable(key="errorMessage")
    public AlwaysFailParser withError(
            @Parameter(key="errorMessage",
                    label="Error Message",
                    description="The error message explaining the error.",
                    defaultValue=DEFAULT_ERROR_MESSAGE) String message) {
        if(StringUtils.isNotEmpty(message)) {
            error = new IllegalStateException(message);
        }
        return this;
    }

    public AlwaysFailParser withError(Throwable error) {
        this.error = Objects.requireNonNull(error, "A valid error message is required.");
        return this;
    }

    Throwable getError() {
        return error;
    }
}
