#!/bin/bash

JAVA="java"
HOME="/home/minecraft/paper/"
JAR="/home/minecraft/paper/server.jar"
RAM="28000M"
FLAGS="-XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=40 -XX:G1MaxNewSizePercent=50 -XX:G1HeapRegionSize=16M -XX:G1ReservePercent=15 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=20 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -Daikars.new.flags=true -Dusing.aikars.flags=https://mcflags.emc.gs"

while true; do
    echo "Starting server..."
     # shellcheck disable=SC2086
    ${JAVA} -Xmx${RAM} -Xms${RAM} ${FLAGS} -jar ${JAR}  --nogui
    for i in 3 2 1; do
        printf 'Server restarting in %s... (press CTRL-C to exit)\n' "${i}"
        sleep 1
    done
    rm /home/minecraft/paper/plugins/DuckSMP-*.jar
    LATEST_DUCK_RELEASE=$(curl -X GET "https://api.github.com/repos/duckNation/SMPCore/commits/master" -H  "accept: application/json" | jq -r '.sha[:7]')
    wget -O "${HOME}plugins/DuckSMP-${LATEST_DUCK_RELEASE}.jar" "https://github.com/duckNation/SMPCore/releases/download/${LATEST_DUCK_RELEASE}/DuckSMP.jar"
done