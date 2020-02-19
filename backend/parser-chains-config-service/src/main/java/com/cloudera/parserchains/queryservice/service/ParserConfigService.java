package com.cloudera.parserchains.queryservice.service;

import com.cloudera.parserchains.queryservice.model.ParserChain;
import com.cloudera.parserchains.queryservice.model.ParserChainSummary;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ParserConfigService {

  List<ParserChainSummary> findAll(Path path) throws IOException;

  ParserChain create(ParserChain chain, Path path) throws IOException;

  ParserChain read(String id, Path path) throws IOException;

  ParserChain update(String id, ParserChain chain, Path path) throws IOException;

  boolean delete(String id, Path path) throws IOException;
}
