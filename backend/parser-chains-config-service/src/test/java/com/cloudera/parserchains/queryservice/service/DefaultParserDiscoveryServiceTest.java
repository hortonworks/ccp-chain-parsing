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

import static com.cloudera.parserchains.queryservice.service.DefaultParserDiscoveryService.infoToType;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import com.cloudera.parserchains.core.Parser;
import com.cloudera.parserchains.core.catalog.ParserCatalog;
import com.cloudera.parserchains.core.catalog.ParserInfo;
import com.cloudera.parserchains.queryservice.model.ParserType;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DefaultParserDiscoveryServiceTest {

  @Mock
  private ParserCatalog catalog;
  @Mock
  private Parser parserType1;
  @Mock
  private Parser parserType2;
  @Mock
  private Parser parserType3;
  private ParserDiscoveryService service;
  private List<ParserInfo> parserInfos;

  @BeforeEach
  public void beforeEach() throws IOException {
    parserInfos = Arrays.asList(
        ParserInfo.builder().withName("type1").withDescription("type 1 description")
            .withParserClass((Class<Parser>) parserType1.getClass()).build(),
        ParserInfo.builder().withName("type2").withDescription("type 2 description")
            .withParserClass((Class<Parser>) parserType2.getClass()).build(),
        ParserInfo.builder().withName("type3").withDescription("type 3 description")
            .withParserClass((Class<Parser>) parserType3.getClass()).build()
    );
    when(catalog.getParsers()).thenReturn(parserInfos);
    service = new DefaultParserDiscoveryService(catalog);
  }

  @Test
  public void returns_list_of_parsers() throws IOException {
    List<ParserType> expected = infoToType(parserInfos);
    List<ParserType> actual = service.findAll();
    assertThat(actual, equalTo(expected));
  }

  /*
  add this back once the config piece is figured out
  @Test
  public void returns_map_of_configs() throws IOException {
    Map<String, ParserFormConfig> expected = infoToConfig(parserInfos);
    Map<String, ParserFormConfig> actual = service.findAllConfig();
    assertThat(actual, equalTo(expected));
  }
   */
}
