.PHONY: test build start

test:
	lein test

build:
	lein uberjar

start:
	lein ring server-headless
