#!/bin/sh
# to build
mvn clean install assembly:single

# to display the list of options
java -cp target/ranker2-1.0-SNAPSHOT-jar-with-dependencies.jar com.hyperiongray.ranker2.Ranker

# to actually run
java -cp target/ranker2-1.0-SNAPSHOT-jar-with-dependencies.jar com.hyperiongray.ranker2.Ranker --keyphrases data/keyphrases.txt
