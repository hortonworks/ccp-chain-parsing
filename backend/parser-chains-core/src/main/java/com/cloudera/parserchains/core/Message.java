package com.cloudera.parserchains.core;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link Message} is consumed and parsed by a {@link Parser}.
 *
 * A {@link Message} is composed of a collection of fields. The message fields
 * are represented as ({@link FieldName}, {@link FieldValue}) pairs.
 *
 * A {@link Message} is immutable and a {@link Builder} should be used to
 * construct one.
 */
public class Message {

    /**
     * Constructs a {@link Message}.
     */
    public static class Builder {
        private Map<FieldName, FieldValue> fields;
        private Throwable error;

        public Builder() {
            this.fields = new HashMap<>();
        }

        /**
         * Adds all fields
         * @param message
         * @return
         */
        public Builder withFields(Message message) {
            Objects.requireNonNull(message);
            this.fields.putAll(message.fields);
            return this;
        }

        /**
         * Add a field to the message.
         * @param name The name of the field to add.
         * @param value The value of the field to add.
         * @return
         */
        public Builder addField(FieldName name, FieldValue value) {
            this.fields.put(Objects.requireNonNull(name), Objects.requireNonNull(value));
            return this;
        }

        /**
         * Remove a field from the message.
         * @param name The name of the field to remove.
         * @return
         */
        public Builder removeField(FieldName name) {
            this.fields.remove(Objects.requireNonNull(name));
            return this;
        }

        /**
         * Removes multiple fields from the message.
         * @param fieldNames The name of the fields to remove.
         * @return
         */
        public Builder removeFields(List<FieldName> fieldNames) {
            for(FieldName fieldName: fieldNames) {
                this.fields.remove(Objects.requireNonNull(fieldName));
            }
            return this;
        }

        /**
         * Renames a field, if the field exists within the message. If the
         * field does not exist, no action taken.
         * @param from The original field name.
         * @param to The new field name.
         * @return
         */
        public Builder renameField(FieldName from, FieldName to) {
            if(fields.containsKey(from)) {
                FieldValue value = fields.remove(from);
                fields.put(to, value);
            }
            return this;
        }

        /**
         * Adds an error to the message. This indicates that an error
         * occurred while parsing.
         * @param error The error that occurred.
         * @return
         */
        public Builder withError(Throwable error) {
            this.error = Objects.requireNonNull(error);
            return this;
        }

        /**
         * Adds an error to the message. This indicates that an error
         * occurred while parsing.
         * @param message The error message.
         * @return
         */
        public Builder withError(String message) {
            this.error = new IllegalStateException(Objects.requireNonNull(message));
            return this;
        }

        /**
         * Builds a {@link Message}.
         * @return The message.
         */
        public Message build() {
            return new Message(this);
        }
    }

    private Map<FieldName, FieldValue> fields;
    private Throwable error;

    private Message(Builder builder) {
        this.fields = new HashMap<>();
        this.fields.putAll(builder.fields);
        this.error = builder.error;
    }

    /**
     * @return A {@link Builder} that can be used to create a message.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the value of a field within this message.
     * @param fieldName The name of the field.
     * @return The value of the field or Optional.empty if it does not exist.
     */
    public Optional<FieldValue> getField(FieldName fieldName) {
        if(fields.containsKey(fieldName)) {
            return Optional.of(fields.get(fieldName));
        } else {
            return Optional.empty();
        }
    }

    public Map<FieldName, FieldValue> getFields() {
        return Collections.unmodifiableMap(fields);
    }

    public Optional<Throwable> getError() {
        return Optional.ofNullable(error);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        return new EqualsBuilder()
                .append(fields, message.fields)
                .append(error, message.error)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fields)
                .append(error)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Message{" +
                "fields=" + fields +
                ", error=" + error +
                '}';
    }
}
