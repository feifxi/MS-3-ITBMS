createdb: 
	docker run \
	--name itbms-dev-db \
	-e MYSQL_ROOT_PASSWORD=rootpass \
	-v ./itbms-database/init.sql:/docker-entrypoint-initdb.d/init.sql \
	-p 3306:3306 \
	-d mysql:latest

# docker run --name itbms-dev-db -e MYSQL_ROOT_PASSWORD=rootpass -v ./itbms-database/init.sql:/docker-entrypoint-initdb.d/init.sql -p 3306:3306 -d mysql:latest

dropdb:
	docker rm -f itbms-dev-db

ollama: 
	docker run -d \
	-v ollama_data:/root/.ollama \
	-p 11434:11434 \
	--name itbms-ollama-container \
	ollama/ollama && \
	docker exec -it itbms-ollama-container ollama pull llama3

# docker run -d -v ollama_data:/root/.ollama -p 11434:11434 --name itbms-ollama-container ollama/ollama && docker exec -it itbms-ollama-container ollama pull llama3

ollama-exec:
	docker exec -it ollama bash

.PHONY: createdb dropdb ollama ollama-exec