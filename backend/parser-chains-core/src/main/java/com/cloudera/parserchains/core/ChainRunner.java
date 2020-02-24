package com.cloudera.parserchains.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Parses a {@link Message} using a parser chain.
 */
public class ChainRunner {
    private FieldName inputField;

    public ChainRunner() {
        inputField = FieldName.of("original_string");
    }

    /**
     * @param inputField The name of the field that is initialized with
     *                   the raw input. Defaults to "original_input".
     */
    public ChainRunner withInputField(FieldName inputField) {
        this.inputField = inputField;
        return this;
    }

    public FieldName getInputField() {
        return inputField;
    }

    /**
     * Parses raw input using a parser chain.
     * @param toParse The input to parse.
     * @param chain The parser chain that parses each message.
     * @return
     */
    public List<Message> run(String toParse, ChainLink chain) {
        // create the initial message
        Message message = Message.builder()
                .addField(inputField, FieldValue.of(toParse))
                .build();

        List<Message> results = new ArrayList<>();
        results.add(message);
        Optional<ChainLink> nextLink = Optional.of(chain);
        do {
            // parse the message
            Message input = results.get(results.size()-1);
            Parser parser = nextLink.get().getParser();
            Message output = parser.parse(input);
            results.add(output);

            // get the next link in the chain
            nextLink = nextLink.get().getNext(output);

            // if there is an error, stop parsing the message
            if(output.getError().isPresent()) {
                break;
            }

        } while(nextLink.isPresent());

        return results;
    }
}
