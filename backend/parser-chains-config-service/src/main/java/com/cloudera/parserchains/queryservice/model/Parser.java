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

package com.cloudera.parserchains.queryservice.model;

import java.util.HashMap;
import java.util.Map;

public class Parser {

  private String name;
  private String type;
  private String id;
  private Map<Object, Object> config;
  private Map<Object, Object> outputs;
  private Map<Object, Object> advanced;

  public Parser() {
    config = new HashMap<>();
    outputs = new HashMap<>();
    advanced = new HashMap<>();
  }

  public String getName() {
    return name;
  }

  public Parser setName(String name) {
    this.name = name;
    return this;
  }

  public String getType() {
    return type;
  }

  public Parser setType(String type) {
    this.type = type;
    return this;
  }

  public String getId() {
    return id;
  }

  public Parser setId(String id) {
    this.id = id;
    return this;
  }

  public Map<Object, Object> getConfig() {
    return config;
  }

  public Parser setConfig(Map<Object, Object> config) {
    this.config = config;
    return this;
  }

  public Map<Object, Object> getOutputs() {
    return outputs;
  }

  public Parser setOutputs(Map<Object, Object> outputs) {
    this.outputs = outputs;
    return this;
  }

  public Map<Object, Object> getAdvanced() {
    return advanced;
  }

  public Parser setAdvanced(Map<Object, Object> advanced) {
    this.advanced = advanced;
    return this;
  }
}
