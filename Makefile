db: 
	docker run \
	--name itbms-dev-db \
	-e MYSQL_ROOT_PASSWORD=rootpass \
	-v ./itbms-database/init.sql:/docker-entrypoint-initdb.d/init.sql \
	-p 3306:3306 \
	-d mysql:latest

# docker run --name itbms-dev-db -e MYSQL_ROOT_PASSWORD=rootpass -v ./itbms-database/init.sql:/docker-entrypoint-initdb.d/init.sql -p 3306:3306 -d mysql:latest

ollama: 
	docker run -d \
	-v itbms_ollama_data:/root/.ollama \
	-p 11434:11434 \
	--name itbms-ollama-container \
	ollama/ollama && \
	docker exec -it itbms-ollama-container ollama pull llama3

# docker run -d -v itbms_ollama_data:/root/.ollama -p 11434:11434 --name itbms-ollama-container ollama/ollama && docker exec -it itbms-ollama-container ollama pull llama3

ollama-exec:
	docker exec -it itbms-ollama-container bash

minio:
	docker run -d \
	--name itbms-minio \
	-p 9000:9000 \
	-p 9001:9001 \
	-e MINIO_ROOT_USER=root \
	-e MINIO_ROOT_PASSWORD=rootpass \
	-v itbms_minio_data:/data \
	minio/minio:latest server /data --console-address ":9001"

.PHONY: db rmdb ollama ollama-exec minio