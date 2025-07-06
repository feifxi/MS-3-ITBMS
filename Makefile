createdb: 
	docker compose -f itbms-database/docker-compose-devdb.yml up -d

dropdb:
	docker compose -f itbms-database/docker-compose-devdb.yml down -v

.PHONY: createdb dropdb