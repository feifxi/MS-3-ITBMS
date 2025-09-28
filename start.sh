#!/bin/bash
set -e

docker compose up -d --build --remove-orphans

# wait for Ollama container to start
sleep 5

docker exec itbms-ollama ollama pull llama3
docker image prune -f