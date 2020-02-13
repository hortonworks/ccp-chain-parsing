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

package com.cloudera.parserchains.queryservice.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

import com.cloudera.parserchains.queryservice.common.utils.IDGenerator;
import com.cloudera.parserchains.queryservice.model.ParserChain;
import com.cloudera.parserchains.queryservice.model.ParserChainSummary;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FileBasedParserConfigServiceTest {

  @Mock
  private IDGenerator<Long> idGenerator;
  private FileBasedParserConfigService service;
  private Path configPath;

  @BeforeEach
  public void beforeEach() throws IOException {
    when(idGenerator.incrementAndGet()).thenReturn(1L, 2L, 3L, 4L, 5L);
    service = new FileBasedParserConfigService(idGenerator);
    String tempDirPrefix = this.getClass().getName();
    configPath = Files.createTempDirectory(tempDirPrefix);
  }

  @Test
  public void creates_parser_chain() throws IOException {
    ParserChain chain = new ParserChain().setName("chain1");
    ParserChain result = service.create(chain, configPath);
    ParserChain actual = service.read(result.getId(), configPath);
    ParserChain expected = new ParserChain().setId("1").setName("chain1");
    assertThat(actual, equalTo(expected));
  }

  @Test
  public void findAll_returns_all_existing_parser_chain() throws IOException {
    List<String> names = Arrays.asList("chain1", "chain2", "chain3", "chain4", "chain5");
    for (String name : names) {
      ParserChain chain = new ParserChain().setName(name);
      service.create(chain, configPath);
    }
    List<ParserChainSummary> actual = service.findAll(configPath);
    int id = 1;
    List<ParserChainSummary> expected = new ArrayList<>();
    for (String name : names) {
      ParserChainSummary summary = new ParserChainSummary().setId("" + id++).setName(name);
      expected.add(summary);
    }
    assertThat(actual, equalTo(expected));
  }

  @Test
  public void reads_existing_parser_chain() throws IOException {
    ParserChain chain = new ParserChain().setName("chain1");
    service.create(chain, configPath);
    ParserChain actual = service.read("1", configPath);
    ParserChain expected = new ParserChain().setId("1").setName("chain1");
    assertThat(actual, equalTo(expected));
  }

  @Test
  public void updates_existing_parser_chain() throws IOException {
    ParserChain chain = new ParserChain().setName("chain1");
    service.create(chain, configPath);
    chain.setName("UPDATEDchain1");
    ParserChain actual = service.update("1", chain, configPath);
    ParserChain expected = new ParserChain().setId("1").setName("UPDATEDchain1");
    assertThat(actual, equalTo(expected));
  }

  @Test
  public void updates_existing_parser_chain_without_changing_the_ID() throws IOException {
    ParserChain chain = new ParserChain().setName("chain1");
    service.create(chain, configPath);
    chain.setId("NOBUENO");
    chain.setName("UPDATEDchain1");
    ParserChain actual = service.update("1", chain, configPath);
    ParserChain expected = new ParserChain().setId("1").setName("UPDATEDchain1");
    assertThat(actual, equalTo(expected));
  }

  @Test
  public void returns_null_on_update_to_nonexistent_parser_chain() throws IOException {
    ParserChain chain = new ParserChain().setName("chain1");
    service.create(chain, configPath);
    chain.setName("UPDATEDchain1");
    ParserChain actual = service.update("5", chain, configPath);
    assertThat(actual, is(nullValue()));
  }

  @Test
  public void deletes_parser_chain_by_id() throws IOException {
    List<String> names = Arrays.asList("chain1", "chain2", "chain3");
    for (String name : names) {
      ParserChain chain = new ParserChain().setName("chain1");
      service.create(chain, configPath);
    }
    final String idToDelete = "2";
    assertThat("Should have 3 parser chains.", service.findAll(configPath), hasSize(names.size()));
    assertThat("Should have returned true for a successfully deleted config.",
        service.delete(idToDelete, configPath), equalTo(true));
    assertThat("Should have 2 parser chains.", service.findAll(configPath),
        hasSize(names.size() - 1));
    assertThat("Should not have deleted parser chain.", service.read(idToDelete, configPath),
        is(nullValue()));
    assertThat("Should have returned false for a config we already deleted.",
        service.delete(idToDelete, configPath), equalTo(false));
    assertThat("Should still have 2 parser chains.", service.findAll(configPath),
        hasSize(names.size() - 1));
  }
}
