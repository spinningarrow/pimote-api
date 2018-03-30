.PHONY: test build start deploy

test:
	lein test

build:
	lein uberjar

start:
	lein ring server-headless

deploy:
	./deploy.sh
