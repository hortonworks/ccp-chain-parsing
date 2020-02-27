/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudera.parserchains.queryservice.model.define;

import com.cloudera.parserchains.queryservice.model.ParserID;
import com.cloudera.parserchains.queryservice.model.ParserName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Describes the structure of one parser within a {@link ParserChainSchema}.
 */
@JsonPropertyOrder({"id", "name", "type", "config", "outputs"})
public class ParserSchema {

  @JsonProperty("id")
  private String label;
  @JsonProperty("type")
  private ParserID id;
  private ParserName name;
  private Map<String, List<ConfigValueSchema>> config;
  private Map<String, Object> outputs;

  public ParserSchema() {
    config = new HashMap<>();
    outputs = new HashMap<>();
  }

  public ParserID getId() {
    return id;
  }

  public ParserSchema setId(ParserID id) {
    this.id = id;
    return this;
  }

  public ParserName getName() {
    return name;
  }

  public ParserSchema setName(ParserName name) {
    this.name = name;
    return this;
  }

  public String getLabel() {
    return label;
  }

  public ParserSchema setLabel(String label) {
    this.label = label;
    return this;
  }

  public Map<String, List<ConfigValueSchema>> getConfig() {
    return config;
  }

  public ParserSchema setConfig(Map<String, List<ConfigValueSchema>> config) {
    this.config = config;
    return this;
  }

  public ParserSchema addConfig(String key, ConfigValueSchema value) {
    List<ConfigValueSchema> values;
    if(config.containsKey(key)) {
      values = config.get(key);
    } else {
      values = new ArrayList<>();
      config.put(key, values);
    }
    values.add(value);
    return this;
  }

  public Map<String, Object> getOutputs() {
    return outputs;
  }

  public ParserSchema setOutputs(Map<String, Object> outputs) {
    this.outputs = outputs;
    return this;
  }

  public ParserSchema addOutput(String key, Object value) {
    outputs.put(key, value);
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParserSchema parserSchema = (ParserSchema) o;
    return new EqualsBuilder()
            .append(id, parserSchema.id)
            .append(name, parserSchema.name)
            .append(config, parserSchema.config)
            .append(outputs, parserSchema.outputs)
            .append(label, parserSchema.label)
            .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
            .append(id)
            .append(name)
            .append(config)
            .append(outputs)
            .append(label)
            .toHashCode();
  }
}
