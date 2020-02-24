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

import java.util.Objects;

public class SchemaItem {
  private String name;
  private String type = "text";
  private String label;
  private String description;
  private String required;
  private String path = "config";

  public String getName() {
    return name;
  }

  public SchemaItem setName(String name) {
    this.name = name;
    return this;
  }

  public String getType() {
    return type;
  }

  public SchemaItem setType(String type) {
    this.type = type;
    return this;
  }

  public String getLabel() {
    return label;
  }

  public SchemaItem setLabel(String label) {
    this.label = label;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public SchemaItem setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getRequired() {
    return required;
  }

  public SchemaItem setRequired(String required) {
    this.required = required;
    return this;
  }

  public String getPath() {
    return path;
  }

  public SchemaItem setPath(String path) {
    this.path = path;
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
    SchemaItem that = (SchemaItem) o;
    return Objects.equals(name, that.name) &&
        Objects.equals(type, that.type) &&
        Objects.equals(label, that.label) &&
        Objects.equals(description, that.description) &&
        Objects.equals(required, that.required) &&
        Objects.equals(path, that.path);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, type, label, description, required, path);
  }
}
