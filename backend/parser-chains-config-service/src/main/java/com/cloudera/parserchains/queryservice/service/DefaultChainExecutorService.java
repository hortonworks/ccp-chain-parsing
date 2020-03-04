package com.cloudera.parserchains.queryservice.service;

import com.cloudera.parserchains.core.ChainLink;
import com.cloudera.parserchains.core.ChainRunner;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.queryservice.model.exec.ParserResult;
import com.cloudera.parserchains.queryservice.model.exec.ParserTestRun;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.cloudera.parserchains.queryservice.model.exec.ParserTestRun.ResultLog.error;
import static com.cloudera.parserchains.queryservice.model.exec.ParserTestRun.ResultLog.success;

@Service
public class DefaultChainExecutorService implements ChainExecutorService {
    private static final Logger logger = LogManager.getLogger(DefaultChainExecutorService.class);
    private ChainRunner chainRunner;

    public DefaultChainExecutorService(ChainRunner chainRunner) {
        this.chainRunner = chainRunner;
    }

    @Override
    public ParserResult execute(ChainLink chain, String textToParse) {
        Message original = chainRunner.originalMessage(textToParse);
        try {
            if (chain != null) {
                List<Message> messages = chainRunner.run(textToParse, chain);
                return chainExecuted(messages);

            } else {
                return chainNotDefined(original);
            }

        } catch(Throwable e) {
            return chainFailed(original, e);
        }
    }

    /**
     * Returns a {@link ParserResult} after a parser chain was executed.
     * @param messages The result of executing the parser chain.
     */
    private ParserResult chainExecuted(List<Message> messages) {
        ParserResult result = new ParserResult();

        // define the input fields
        Message input = messages.get(0);
        result.setInput(input.getFields()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().get(),
                        e -> e.getValue().get())));

        // define the output fields
        Message output = messages.get(messages.size()-1);
        result.setOutput(output.getFields()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().get(),
                        e -> e.getValue().get())));

        // define the log section
        ParserTestRun.ResultLog log;
        String parserId = output.getCreatedBy().get();
        if(output.getError().isPresent()) {
            Throwable rootCause = ExceptionUtils.getRootCause(output.getError().get());
            log = error(parserId, rootCause.getMessage());

        } else {
            log = success(parserId);
        }
        return result.setLog(log);
    }

    /**
     * Return a {@link ParserResult} indicating that an unexpected error occurred
     * while executing the parser chain.
     * @param original The original message to parse.
     */
    private ParserResult chainFailed(Message original, Throwable t) {
        logger.info("There was a problem executing the parser chain.", t);
        ParserResult result = new ParserResult();

        // define the input fields
        result.setInput(original.getFields()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().get(),
                        e -> e.getValue().get())));

        // define the log section
        Throwable rootCause = ExceptionUtils.getRootCause(t);
        ParserTestRun.ResultLog log = error(original.getCreatedBy().get(), rootCause.getMessage());

        // there are no output fields
        return result.setLog(log);
    }

    /**
     * Return a {@link ParserResult} indicating that no parser chain has yet been
     * defined.  For example, there are no parsers in the chain.
     * <p>If a parser chain has not yet been defined by the user, the result returned
     * should indicate success even though we could not parse anything.
     * @param original The original message to parse.
     */
    private ParserResult chainNotDefined(Message original) {
        ParserResult result = new ParserResult();

        // define the input fields
        result.setInput(original.getFields()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().get(),
                        e -> e.getValue().get())));

        // there are no output fields
        // define the log section
        ParserTestRun.ResultLog log = success(original.getCreatedBy().get(), "No parser chain defined.");
        return result.setLog(log);
    }
}
