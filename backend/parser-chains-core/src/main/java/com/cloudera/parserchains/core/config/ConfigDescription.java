package com.cloudera.parserchains.core.config;

import com.cloudera.parserchains.core.Regex;

import static com.cloudera.parserchains.core.Validator.mustMatch;

/**
 * Describes a parser's configuration parameter for a user.
 */
public class ConfigDescription {
    static final Regex validDescription = Regex.of("^[a-zA-Z_0-9 ,-.:;'\"()]{1,80}$");
    private final String description;

    /**
     * Creates a {@link ConfigDescription}.
     * @param desc The description.
     */
    public static ConfigDescription of(String desc) {
        return new ConfigDescription((desc));
    }

    /**
     * See {@link ConfigDescription#of(String)}.
     */
    private ConfigDescription(String description) {
        mustMatch(description, validDescription, "description");
        this.description = description;
    }

    public String get() {
        return description;
    }
}
