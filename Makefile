createdb: 
	docker run \
	--name itbms-dev-db \
	-e MYSQL_ROOT_PASSWORD=rootpass \
	-v ./itbms-database/init.sql:/docker-entrypoint-initdb.d/init.sql \
	-p 3306:3306 \
	-d mysql:latest

rmdb:
	docker rm -f itbms-dev-db

.PHONY: createdb rmdb 