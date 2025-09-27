#!/bin/bash
docker compose build --no-cache --pull \
&& docker compose up -d --remove-orphans

# wait a few seconds for container to start
sleep 5

docker exec itbms-ollama ollama pull llama3 \
&& docker image prune -f