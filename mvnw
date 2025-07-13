#!/bin/bash

# Maven Wrapper Script for Unix/Linux/macOS
# This script allows running Maven without having it installed locally

set -euo pipefail

MAVEN_WRAPPER_JAR=".mvn/wrapper/maven-wrapper.jar"
MAVEN_WRAPPER_PROPERTIES=".mvn/wrapper/maven-wrapper.properties"

# Check if wrapper jar exists
if [ ! -f "$MAVEN_WRAPPER_JAR" ]; then
    echo "Maven Wrapper JAR not found. Downloading..."
    mkdir -p .mvn/wrapper
    curl -L https://repo1.maven.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar -o "$MAVEN_WRAPPER_JAR"
fi

# Execute Maven with wrapper
exec java -cp "$MAVEN_WRAPPER_JAR" org.apache.maven.wrapper.MavenWrapperMain "$@"
