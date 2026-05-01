#!/bin/bash
if ! command -v java &> /dev/null; then
    echo "Java non trouvé, installation en cours..."
    sudo apt install -y openjdk-21-jdk
fi
DIR="$(dirname "$0")"
java -jar "$DIR/sokoban-1.0-SNAPSHOT.jar"
