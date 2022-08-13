#!/bin/bash

JAVA="java"
HOME="/home/minecraft/test/"
JAR="/home/minecraft/test/server.jar"
RAM="7168M"
FLAGS="--add-modules=jdk.incubator.vector -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -Dusing.aikars.flags=https://mcflags.emc.gs -Daikars.new.flags=true -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20"

while true; do
  echo "Updating server..."
    rm -f /home/minecraft/paper/plugins/DuckSMP-*.jar
    LATEST_DUCK_RELEASE=$(curl -X GET "https://api.github.com/repos/duckNation/SMPCore/commits/master" -H  "accept: application/json" | jq -r '.sha[:7]')
    echo "Downloading latest DuckSMP release ${LATEST_DUCK_RELEASE}"
    wget -O "${HOME}plugins/DuckSMP-${LATEST_DUCK_RELEASE}.jar" "https://github.com/duckNation/SMPCore/releases/download/${LATEST_DUCK_RELEASE}/DuckSMP.jar"
    echo "Starting server..."
     # shellcheck disable=SC2086
    ${JAVA} -Xmx${RAM} -Xms${RAM} ${FLAGS} -jar ${JAR}  --nogui
    for i in 3 2 1; do
        printf 'Server restarting in %s... (press CTRL-C to exit)\n' "${i}"
        sleep 1
    done
done