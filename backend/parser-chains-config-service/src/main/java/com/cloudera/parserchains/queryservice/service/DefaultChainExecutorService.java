package com.cloudera.parserchains.queryservice.service;

import com.cloudera.parserchains.core.ChainLink;
import com.cloudera.parserchains.core.ChainRunner;
import com.cloudera.parserchains.core.LinkName;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.NextChainLink;
import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.ParserBuilder;
import com.cloudera.parserchains.core.catalog.ParserCatalog;
import com.cloudera.parserchains.core.catalog.ParserInfo;
import com.cloudera.parserchains.core.config.ConfigKey;
import com.cloudera.parserchains.core.config.ConfigName;
import com.cloudera.parserchains.core.config.ConfigValue;
import com.cloudera.parserchains.queryservice.model.ParserID;
import com.cloudera.parserchains.queryservice.model.define.ConfigValueSchema;
import com.cloudera.parserchains.queryservice.model.define.ParserChainSchema;
import com.cloudera.parserchains.queryservice.model.define.ParserSchema;
import com.cloudera.parserchains.queryservice.model.exec.ParserResult;
import com.cloudera.parserchains.queryservice.model.exec.ParserTestRun;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DefaultChainExecutorService implements ChainExecutorService {
    private static final Logger logger = LogManager.getLogger(DefaultChainExecutorService.class);
    public static final String SUCCESS_MESSAGE = "success";
    public static final String INFO_TYPE = "info";
    public static final String ERROR_TYPE = "error";
    private ParserBuilder parserBuilder;
    private ParserCatalog parserCatalog;
    private ChainRunner chainRunner;

    public DefaultChainExecutorService(ParserBuilder parserBuilder,
                                       ParserCatalog parserCatalog,
                                       ChainRunner chainRunner) {
        this.parserBuilder = parserBuilder;
        this.parserCatalog = parserCatalog;
        this.chainRunner = chainRunner;
    }

    @Override
    public ParserResult execute(ParserChainSchema chain, String textToParse) {
        Message original = chainRunner.originalMessage(textToParse);
        try {
            ChainLink head = buildChain(chain);
            if(head != null) {
                // use the parser chain to parse the message
                List<Message> messages = chainRunner.run(textToParse, head);
                return chainExecuted(messages);
            } else {
                // there are no parsers in the chain yet
                return chainNotDefined(original);
            }
        } catch(Throwable t) {
            // there was a problem constructing or executing the parser chain
            Message error = Message.builder().clone(original).withError(t).build();
            return chainFailed(error);
        }
    }

    /**
     * Build a parser chain.
     * @param parserChainSchema
     * @return A parser chain or Optional.empty if there are no parsers defined in the schema.
     */
    private ChainLink buildChain(ParserChainSchema parserChainSchema) {
        List<ParserInfo> parserInfos = parserCatalog.getParsers();

        // build the chain
        NextChainLink head = null;
        NextChainLink current = null;
        for(ParserSchema parserSchema : parserChainSchema.getParsers()) {
            if(ParserID.router().equals(parserSchema.getId())) {
                // construct the router
                // TODO implement me

            } else {
                // construct the parser
                LinkName linkName = LinkName.of(parserSchema.getLabel());
                Parser parser = buildParser(parserSchema, parserInfos);
                configureParser(parser, parserSchema);

                // connect the links in the chain
                if(head == null) {
                    head = new NextChainLink(parser, linkName);
                    current = head;
                } else {
                    NextChainLink next = new NextChainLink(parser, linkName);
                    current.setNext(next);
                    current = next;
                }
            }
        }
        return head;
    }

    private void configureParser(Parser parser, ParserSchema parserSchema) {
        // for each configuration parameter defined in the schema
        parserSchema.getConfig().forEach((configName, valuesSchema) -> {
            for(ConfigValueSchema valueSchema: valuesSchema) {
                Map<ConfigKey, ConfigValue> values = valueSchema
                        .getValues()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                e -> ConfigKey.of(e.getKey()),
                                e -> ConfigValue.of(e.getValue())));
                parser.configure(ConfigName.of(configName), values);
            }
        });
    }

    private Parser buildParser(ParserSchema parserSchema, List<ParserInfo> parserInfos) {
        String className = parserSchema.getId().getId();
        Optional<ParserInfo> parserInfo = parserInfos
                .stream()
                .filter(info -> className.equals(info.getParserClass().getCanonicalName()))
                .findFirst();
        if(parserInfo.isPresent()) {
            return parserBuilder.build(parserInfo.get());

        } else {
            String error = String.format("Unable to find parser in catalog; class={}", className);
            logger.error(error);
            throw new IllegalStateException(error);
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
        String message = SUCCESS_MESSAGE;
        String type = INFO_TYPE;
        if(output.getError().isPresent()) {
            Throwable rootCause = ExceptionUtils.getRootCause(output.getError().get());
            message = rootCause.getMessage();
            type = ERROR_TYPE;
        }
        result.setLog(new ParserTestRun.ResultLog()
                .setMessage(message)
                .setParserId(output.getCreatedBy().get())
                .setType(type));
        return result;
    }

    /**
     * Return a {@link ParserResult} indicating that an unexpected error occurred
     * while executing the parser chain.
     * @param original The original message to parse.
     */
    private ParserResult chainFailed(Message original) {
        ParserResult result = new ParserResult();

        // define the input fields
        result.setInput(original.getFields()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().get(),
                        e -> e.getValue().get())));

        // define the log section
        String message = "No parser chain defined.";
        String type = INFO_TYPE;
        if(original.getError().isPresent()) {
            Throwable rootCause = ExceptionUtils.getRootCause(original.getError().get());
            message = rootCause.getMessage();
            type = ERROR_TYPE;
        }
        result.setLog(new ParserTestRun.ResultLog()
                .setMessage(message)
                .setParserId(original.getCreatedBy().get())
                .setType(type));

        // there are no output fields
        return result;
    }

    /**
     * Return a {@link ParserResult} indicating that no parser chain has yet been
     * defined.
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

        // define the log section
        result.setLog(new ParserTestRun.ResultLog()
                .setMessage( "No parser chain defined.")
                .setParserId(original.getCreatedBy().get())
                .setType(INFO_TYPE));

        // there are no output fields
        return result;
    }
}
