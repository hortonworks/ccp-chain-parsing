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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParserConfigSchema {

  private String id;
  private List<SchemaItem> schemaItems;

  public ParserConfigSchema() {
    schemaItems = new ArrayList<>();
  }

  public String getId() {
    return id;
  }

  public ParserConfigSchema setId(String id) {
    this.id = id;
    return this;
  }

  public List<SchemaItem> getSchemaItems() {
    return schemaItems;
  }

  public ParserConfigSchema setSchemaItems(List<SchemaItem> schemaItems) {
    this.schemaItems = schemaItems;
    return this;
  }

  public ParserConfigSchema addSchemaItem(SchemaItem item) {
    schemaItems.add(item);
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
    ParserConfigSchema that = (ParserConfigSchema) o;
    return Objects.equals(id, that.id) &&
        Objects.equals(schemaItems, that.schemaItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, schemaItems);
  }
}
