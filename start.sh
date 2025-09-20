#!/bin/bash
docker compose up -d --build --remove-orphans \
&& docker exec itbms-ollama ollama pull llama3 \
&& docker image prune -f