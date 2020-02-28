package com.cloudera.parserchains.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Parses a {@link Message} using a parser chain.
 */
public class DefaultChainRunner implements ChainRunner {
    public static final LinkName ORIGINAL_MESSAGE_NAME = LinkName.of("original");
    private static final Logger logger = LogManager.getLogger(DefaultChainRunner.class);
    private FieldName inputField;

    public DefaultChainRunner() {
        inputField = Constants.DEFAULT_INPUT_FIELD;
    }

    /**
     * @param inputField The name of the field that is initialized with the raw input.
     */
    public DefaultChainRunner withInputField(FieldName inputField) {
        this.inputField = inputField;
        return this;
    }

    public FieldName getInputField() {
        return inputField;
    }

    @Override
    public List<Message> run(String toParse, ChainLink chain) {
        List<Message> results;
        Message original = originalMessage(toParse);
        try {
            results = doRun(original, chain);

        } catch(Throwable t) {
            String msg = "An unexpected error occurred while running a parser chain. " +
                    "Ensure that no parser is throwing an unchecked exception. Parsers should " +
                    "instead be reporting the error in the output message.";
            Message error = Message.builder()
                    .clone(original)
                    .withError(msg)
                    .build();
            results = Arrays.asList(error);
            logger.warn(msg, t);
        }
        return results;
    }

    @Override
    public Message originalMessage(String toParse) {
        return Message.builder()
                .addField(inputField, FieldValue.of(toParse))
                .createdBy(ORIGINAL_MESSAGE_NAME)
                .build();
    }

    private List<Message> doRun(Message original, ChainLink chain) {
        List<Message> results = new ArrayList<>();
        results.add(original);

        Optional<ChainLink> nextLink = Optional.ofNullable(chain);
        do {
            // parse the message
            Message input = results.get(results.size()-1);
            Parser parser = nextLink.get().getParser();
            Message output = parser.parse(input);

            // mark which link created this message
            LinkName linkName = nextLink.get().getLinkName();
            Message result = Message.builder()
                    .clone(output)
                    .createdBy(linkName)
                    .build();
            results.add(result);

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
