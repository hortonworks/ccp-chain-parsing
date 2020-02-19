package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.catalog.MessageParser;
import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.RouterLink;

import java.util.Collections;
import java.util.List;
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

    Throwable getError() {
        return error;
    }
}
