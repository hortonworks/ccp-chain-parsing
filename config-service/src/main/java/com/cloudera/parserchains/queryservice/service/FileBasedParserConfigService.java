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

import com.cloudera.parserchains.queryservice.common.utils.IDGenerator;
import com.cloudera.parserchains.queryservice.common.utils.JSONUtils;
import com.cloudera.parserchains.queryservice.model.ParserChain;
import com.cloudera.parserchains.queryservice.model.ParserChainSummary;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileBasedParserConfigService implements ParserConfigService {

  @Autowired
  private IDGenerator<Long> idGenerator;

  @Autowired
  public FileBasedParserConfigService(IDGenerator<Long> idGenerator) {
    this.idGenerator = idGenerator;
  }

  @Override
  public List<ParserChainSummary> findAll(Path path) throws IOException {
    List<ParserChainSummary> summaries = new ArrayList<>();
    try (DirectoryStream<Path> files = Files.newDirectoryStream(path)) {
      for (Path file : files) {
        ParserChain chain = JSONUtils.INSTANCE.load(file.toFile(), ParserChain.class);
        summaries.add(new ParserChainSummary(chain));
      }
    }
    return summaries;
  }

  @Override
  public ParserChain create(ParserChain chain, Path path) throws IOException {
    chain.setId(Long.toString(idGenerator.incrementAndGet()));
    writeChain(chain, path);
    return chain;
  }

  private void writeChain(ParserChain chain, Path outPath) throws IOException {
    Path out = Paths.get(getFileName(chain.getId()));
    out = outPath.resolve(out);
    byte[] bytes = JSONUtils.INSTANCE.toJSONPretty(chain);
    Files.write(out, bytes);
  }

  private String getFileName(String id) {
    return id + ".json";
  }

  @Override
  public ParserChain read(String id, Path path) throws IOException {
    Path inPath = findFile(id, path);
    if (null == inPath) {
      return null;
    }
    return JSONUtils.INSTANCE.load(inPath.toFile(), ParserChain.class);
  }

  private Path findFile(String id, Path root) throws IOException {
    try (DirectoryStream<Path> files = Files.newDirectoryStream(root)) {
      for (Path file : files) {
        if (file.getFileName().toString().equals(getFileName(id))) {
          return file;
        }
      }
    }
    return null;
  }

  @Override
  public ParserChain update(String id, ParserChain chain, Path path) throws IOException {
    ParserChain readChain = read(id, path);
    if (null == readChain) {
      return null;
    }
    // enforce that the client cannot overwrite the chain ID
    chain.setId(id);
    writeChain(chain, path);
    return read(id, path);
  }

  @Override
  public boolean delete(String id, Path path) throws IOException {
    Path deletePath = findFile(id, path);
    if (null == deletePath) {
      return false;
    }
    Files.delete(deletePath);
    return true;
  }
}
