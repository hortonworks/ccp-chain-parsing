#!/bin/bash

PARSER_CHAINS_VERSION=${project.version}
TOPOLOGY_JAR=${project.artifactId}-$PARSER_CHAINS_VERSION-uber.jar
CHAIN_CONFIG_JAR=chainconfig.jar
DEFAULT_ARGS="--remote flux/parserchains/remote.yaml --filter config/parserchains.properties"

# by passing in different args, the user can execute an alternative parser topology
ARGS=${@:-$DEFAULT_ARGS}

if [ ! -f "$CHAIN_CONFIG_JAR" ]
then
  echo "Error - need '$CHAIN_CONFIG_JAR' with the parser chain json in the directory from whence you started the topology."
  exit;
fi

echo "Running storm command: " storm jar lib/$TOPOLOGY_JAR org.apache.storm.flux.Flux $ARGS --jars $CHAIN_CONFIG_JAR
storm jar lib/$TOPOLOGY_JAR org.apache.storm.flux.Flux $ARGS --jars $CHAIN_CONFIG_JAR
