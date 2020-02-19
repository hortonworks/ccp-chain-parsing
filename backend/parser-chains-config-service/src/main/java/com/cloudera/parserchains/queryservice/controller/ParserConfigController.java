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

package com.cloudera.parserchains.queryservice.controller;

import com.cloudera.parserchains.queryservice.common.ApplicationConstants;
import com.cloudera.parserchains.queryservice.config.AppProperties;
import com.cloudera.parserchains.queryservice.model.ParserChain;
import com.cloudera.parserchains.queryservice.model.ParserChainSummary;
import com.cloudera.parserchains.queryservice.service.ParserConfigService;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = ApplicationConstants.API_CHAINS_URL)
public class ParserConfigController {

  @Autowired
  ParserConfigService service;
  @Autowired
  AppProperties appProperties;

  @GetMapping()
  ResponseEntity<List<ParserChainSummary>> findAll() throws IOException {
    String configPath = appProperties.getConfigPath();
    List<ParserChainSummary> configs = service.findAll(Paths.get(configPath));
    return ResponseEntity.ok(configs);
  }

  @PostMapping()
  ResponseEntity<ParserChain> create(@RequestBody ParserChain chain) throws IOException {
    String configPath = appProperties.getConfigPath();
    ParserChain createdChain = service.create(chain, Paths.get(configPath));
    if (null == createdChain) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.created(URI.create(
          ApplicationConstants.API_CHAINS_READ_URL.replace("{id}", createdChain.getId())))
          .body(createdChain);
    }
  }

  @GetMapping(value = "/{id}")
  ResponseEntity<ParserChain> read(@PathVariable String id) throws IOException {
    String configPath = appProperties.getConfigPath();
    ParserChain chain = service.read(id, Paths.get(configPath));
    if (null == chain) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(chain);
    }
  }

  @PutMapping(value = "/{id}")
  ResponseEntity<ParserChain> update(@RequestBody ParserChain chain, @PathVariable String id)
      throws IOException {
    String configPath = appProperties.getConfigPath();
    try {
      ParserChain updatedChain = service.update(id, chain, Paths.get(configPath));
      if (null == updatedChain) {
        return ResponseEntity.notFound().build();
      } else {
        return ResponseEntity.noContent().build();
      }
    } catch (IOException ioe) {
      throw new RuntimeException("Unable to update configuration with id=" + id);
    }
  }

  @DeleteMapping(value = "/{id}")
  ResponseEntity<Void> delete(@PathVariable String id) throws IOException {
    String configPath = appProperties.getConfigPath();
    if (service.delete(id, Paths.get(configPath))) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

}
