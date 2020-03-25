## Assumptions

You have a running instance of Storm, Kafka, and Zookeeper.

## Setup

1. Copy the bundled artifact, parser-chains-storm-1.0-SNAPSHOT.tar.gz, to a node with a Storm client installed, and extract it:
    ```
    tar xvf parser-chains-storm-1.0-SNAPSHOT.tar.gz
    x config/
    x config/parserchains.properties
    x bin/
    x bin/start_parser_chains_topology.sh
    x flux/
    x flux/parserchains/
    x flux/parserchains/remote.yaml
    x lib/parser-chains-storm-1.0-SNAPSHOT-uber.jar
    ```

1. Copy your parser chain to the directory where you extracted the storm tarball. Create a jar file named "`chainconfig.jar`". Using a 
parser chain file name of "`1.json`", this would look like:
    ```
    jar -cf chainconfig.jar 1.json
    ```

1. Modify `config/parserchains.properties` and minimally set the following properties for your parser chain topology.
    ```
    parserchain.config.path=/SET ME
    ...
    kafka.zk=SET ME
    kafka.broker=SET ME
    ...
    parserchains.input.topic= INPUT TOPIC
    parserchains.output.topic= OUTPUT TOPIC
    parserchains.error.topic= OUTPUT ERROR TOPIC
    ...
    ```

    `parserchain.config.path` is the classpath path to the parser chain within the jar passed as an arg, i.e. "`--jars chainconfig.jar`", 
    to the storm jar command.The leading slash is required for the topology to read the resource from the root of the provided jar properly, 
    e.g. "`/1.json`". If our parser chain file name is "`1.json`" and we peek inside the jar file, you should see a layout similar to the
    following:
    ```
    jar -tf chainconfig.jar
    META-INF/
    META-INF/MANIFEST.MF
    1.json
    ```

1. Create topic
    ```
    kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
    ```

1. Create some data we can push to Kafka (use data that works for your parser chain)
    ```
    $ cat data.txt
    one
    two
    three
    four
   
    ```

1. Push data (set broker and kafka topic accordingly)
    ```
    cat data.txt | kafka-console-producer --broker-list localhost:9092 --topic test
    ```

1. Verify data in Kafka (set broker and kafka topic accordingly)
    ```
    kafka-console-consumer --bootstrap-server localhost:9092 --topic test --from-beginning
    ```

1. Start Storm topology
    ```
    bin/start_parser_chains_topology.sh
    ```

