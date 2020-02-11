package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.MessageParser;
import com.cloudera.parserchains.core.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A parser which can remove fields from a message.
 */
@MessageParser(name="Remove Field(s)", description="Removes a message field.")
public class RemoveFieldParser implements Parser {
    private List<FieldName> fieldsToRemove;

    public RemoveFieldParser() {
        fieldsToRemove = new ArrayList<>();
    }

    public RemoveFieldParser removeField(FieldName fieldName) {
        fieldsToRemove.add(fieldName);
        return this;
    }

    @Override
    public Message parse(Message message) {
        return Message.builder()
                .withFields(message)
                .removeFields(fieldsToRemove)
                .build();
    }

    @Override
    public List<FieldName> outputFields() {
        return Collections.emptyList();
    }

    List<FieldName> getFieldsToRemove() {
        return fieldsToRemove;
    }
}
