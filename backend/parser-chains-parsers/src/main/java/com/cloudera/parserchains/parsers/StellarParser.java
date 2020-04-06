package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.catalog.Configurable;
import com.cloudera.parserchains.core.catalog.MessageParser;
import com.cloudera.parserchains.core.catalog.Parameter;
import com.cloudera.parserchains.core.catalog.WidgetType;
import org.apache.metron.stellar.common.shell.DefaultStellarShellExecutor;
import org.apache.metron.stellar.common.shell.StellarResult;
import org.apache.metron.stellar.common.shell.StellarShellExecutor;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Properties;

/**
 * A parser that allows a message to be normalized using Stellar expressions.
 *
 * <p>Multiple expressions can be defined. Latter expressions can refer to the values of
 * earlier ones.
 *
 * <p>All input values must be assumed to be strings.  If numeric values are required, explicit
 * conversion must be performed.  For example, "TO_INTEGER(field1) + TO_INTEGER(field2)".
 */
@MessageParser(
        name = "Stellar",
        description = "Apply Stellar expression(s) to normalize data."
)
public class StellarParser implements Parser {
    private StellarShellExecutor stellarExecutor;
    private LinkedHashMap<FieldName, String> expressions;

    public StellarParser() throws Exception {
        // using a LinkedHashMap to ensure the expressions are executed in the order they were defined
        this.expressions = new LinkedHashMap<>();
        this.stellarExecutor = new DefaultStellarShellExecutor(new Properties(), Optional.empty());
    }

    @Override
    public Message parse(Message input) {
        Message.Builder output = Message.builder()
                .clone(input);

        // assign each message field so that expressions can refer to fields as variables
        input.getFields().forEach((fieldName, fieldValue) ->
            stellarExecutor.assign(fieldName.get(), fieldValue.get(), Optional.empty())
        );

        // execute each expression
        expressions.forEach((fieldName, expression) -> {
            StellarResult result = stellarExecutor.execute(expression);

            // assign the result of the stellar expression
            result.getValue().ifPresent(value -> {
                assignValue(fieldName, value, output);
            });

            // handle any errors
            result.getException().ifPresent(exception ->
                    output.withError(exception)
            );
        });

        return output.build();
    }

    private void assignValue(FieldName fieldName, Object value, Message.Builder output) {
        // add a field to the message
        output.addField(fieldName, value.toString());

        // assign a value in the executor so subsequent expressions can refer to it
        stellarExecutor.assign(fieldName.get(), value, Optional.empty());
    }

    /**
     * Add a Stellar expression that will be executed.
     * @param fieldName The name of the field to create or modify.
     * @param expression The Stellar expression to execute.
     * @return
     */
    public StellarParser expression(FieldName fieldName, String expression) {
        this.expressions.put(fieldName, expression);
        return this;
    }

    /**
     * Add a Stellar expression that will be executed.
     * @param fieldName The name of the field to create or modify.
     * @param expression The Stellar expression to execute.
     * @return
     */
    @Configurable(key="stellarExpression")
    public StellarParser expression(
            @Parameter(
                    key="fieldName",
                    label="Field Name",
                    description="The field to create or modify."
            ) String fieldName,
            @Parameter(
                    key="expression",
                    label="Stellar",
                    description="The Stellar expression to execute.",
                    widgetType = WidgetType.TEXTAREA
            ) String expression) {
        return expression(FieldName.of(fieldName), expression);
    }
}
