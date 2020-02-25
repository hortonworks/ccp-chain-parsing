package com.cloudera.parserchains.core.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigDescriptorTest {

    @Test
    void valid() {
        ConfigDescriptor descriptor = ConfigDescriptor
                .builder()
                .name("fieldToRename")
                .description("Field to Rename")
                .isRequired(true)
                .acceptsValue("from", "The original name of the field.")
                .acceptsValue("to", "The new name of the field.")
                .build();
        assertEquals("fieldToRename", descriptor.getName().get());
        assertEquals("Field to Rename", descriptor.getDescription().get());
        assertEquals(true, descriptor.isRequired());
        assertEquals(2, descriptor.getAcceptedValues().size());
        assertEquals("The original name of the field.", descriptor.getAcceptedValues().get(ConfigKey.of("from")).get());
        assertEquals("The new name of the field.", descriptor.getAcceptedValues().get(ConfigKey.of("to")).get());
    }

    @Test
    void requiresName() {
        assertThrows(RuntimeException.class, () -> ConfigDescriptor
                .builder()
                .isRequired(true)
                .acceptsValue("from", "The original name of the field.")
                .acceptsValue("to", "The new name of the field.")
                .build());
    }

    @Test
    void requiresValues() {
        assertThrows(RuntimeException.class, () -> ConfigDescriptor
                .builder()
                .name("fieldToRename")
                .description("Field to Rename")
                .isRequired(true)
                .build());
    }
}
