package com.cloudera.parserchains.parsers;

import com.cloudera.parserchains.core.FieldName;
import com.cloudera.parserchains.core.Message;
import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.catalog.Configurable;
import com.cloudera.parserchains.core.catalog.MessageParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cloudera.parserchains.core.Constants.DEFAULT_INPUT_FIELD;
import static java.lang.String.format;

@MessageParser(
        name="Simple JSON",
        description="Parses JSON by creating a field for each element.")
public class JSONParser implements Parser {
    private FieldName inputField;
    private ObjectReader reader;
    private List<Normalizer> normalizers;

    public JSONParser() {
        inputField = FieldName.of(DEFAULT_INPUT_FIELD);
        reader = new ObjectMapper().readerFor(Map.class);
        normalizers = new ArrayList<>();
    }

    @Configurable(key="input", label="Input Field", description="The input field to parse.", defaultValue=DEFAULT_INPUT_FIELD)
    public JSONParser inputField(String fieldName) {
        if(StringUtils.isNotBlank(fieldName)) {
            this.inputField = FieldName.of(fieldName);
        }
        return this;
    }

    @Configurable(key="norm", label="Normalizer", description="ALLOW_NESTED, DISALLOW_NESTED, DROP_NESTED, UNFOLD_NESTED")
    public JSONParser normalizer(String normalizer) {
        if(StringUtils.isNotBlank(normalizer)) {
            normalizers.add(Normalizers.valueOf(normalizer));
        }
        return this;
    }

    @Override
    public Message parse(Message input) {
        Message.Builder output = Message.builder().withFields(input);
        if(!input.getField(inputField).isPresent()) {
            output.withError(format("Message missing expected input field '%s'", inputField.toString()));
        } else {
            input.getField(inputField).ifPresent(val -> doParse(val.toString(), output));
        }
        return output.build();
    }

    public void doParse(String toParse, Message.Builder output) {
        try {
            Map<String, Object> values = reader.readValue(toParse);

            // normalize the JSON
            for(Normalizer normalizer: normalizers) {
                values = normalizer.normalize(values);
            }

            // add each element as a field in the message
            values.forEach((key, value) -> output.addField(key, value.toString()));

        } catch (IOException e) {
            output.withError(e);
        }
    }

    /**
     * Defines all of the available {@link Normalizer} types.
     */
    private enum Normalizers implements Normalizer {
        ALLOW_NESTED(new AllowNestedObjects()),
        DISALLOW_NESTED(new DisallowNestedObjects()),
        DROP_NESTED(new DropNestedObjects()),
        UNFOLD_NESTED(new UnfoldNestedObjects());

        private Normalizer normalizer;

        Normalizers(Normalizer normalizer) {
            this.normalizer = normalizer;
        }

        @Override
        public Map<String, Object> normalize(Map<String, Object> input) throws IOException {
            return normalizer.normalize(input);
        }
    }


    private interface Normalizer {
        Map<String, Object> normalize(Map<String, Object> input) throws IOException;
    }

    /**
     * Allow nested objects.
     */
    private static class AllowNestedObjects implements Normalizer {
        @Override
        public Map<String, Object> normalize(Map<String, Object> input) {
            return input;
        }
    }

    /**
     * An error is thrown if any nested objects exist.
     */
    private static class DisallowNestedObjects implements Normalizer {
        @Override
        public Map<String, Object> normalize(Map<String, Object> input) throws IOException {
            // throw an exception if any nested objects exist
            Optional<Object> nestedObject = input.values()
                    .stream()
                    .filter(v -> v instanceof Map)
                    .findFirst();
            if(nestedObject.isPresent()) {
                throw new IOException("Nested objects are not allowed.");
            }
            return input;
        }
    }

    /**
     * Drop any nested objects.
     */
    private static class DropNestedObjects implements Normalizer {
        @Override
        public Map<String, Object> normalize(Map<String, Object> input) {
            // drop any that is a JSON object (aka Map)
            return input.entrySet()
                    .stream()
                    .filter(e -> !(e.getValue() instanceof Map))
                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        }
    }

    /**
     * Unfold any nested objects.
     */
    private static class UnfoldNestedObjects implements Normalizer {
        @Override
        public Map<String, Object> normalize(Map<String, Object> input) {
            Map<String, Object> output = new HashMap<>();
            input.forEach((key, value) -> {
                if (value instanceof Map) {
                    Map<String, Object> mapValue = (Map) value;
                    unfold(key, mapValue, output);
                } else {
                    output.put(key, value);
                }
            });
            return output;
        }

        private void unfold(String rootKey, Map<String, Object> valueToUnfold, Map<String, Object> output) {
            valueToUnfold.forEach((key, value) -> {
                String newKey = String.join(".", rootKey, key);
                if (value instanceof Map) {
                    unfold(newKey, (Map)value, output);
                } else {
                    output.put(newKey, value);
                }
            });
        }
    }
}
