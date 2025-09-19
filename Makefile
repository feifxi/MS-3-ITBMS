dev: 
	docker compose -f docker-compose-dev.yml up -d && docker exec -it itbms-dev-ollama ollama pull llama3

dropdev: 
	docker compose down -v 

.PHONY: db rmdb ollama ollama-exec minio