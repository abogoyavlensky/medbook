# Styling for output
YELLOW := "\e[1;33m"
NC := "\e[0m"
INFO := @sh -c '\
    printf $(YELLOW); \
    echo "=> $$1"; \
    printf $(NC)' VALUE


DIRS?=src test build
GOALS = $(filter-out $@,$(MAKECMDGOALS))

.SILENT:  # Ignore output of make `echo` command


.PHONY: help  # Show list of targets with descriptions
help:
	@$(INFO) "Commands:"
	@grep '^.PHONY: .* #' Makefile | sed 's/\.PHONY: \(.*\) # \(.*\)/\1 > \2/' | column -tx -s ">"


.PHONY: deps  # Install deps
deps:
	@$(INFO) "Install deps..."
	@clojure -P -X:dev:test:ui:migrations


.PHONY: target  # Make target/resources dirs for figwheel
target:
	@$(INFO) "Creating target/resources dirs..."
	@mkdir -p target/resources


.PHONY: repl  # Run repl
repl:
	@$(INFO) "Run repl..."
	@clojure -M:dev:test:ui:migrations

.PHONY: test  # Run tests with coverage
test:
	@$(INFO) "Running tests..."
	@$(MAKE) target
	@clojure -X:dev:ui:migrations:test

.PHONY: fmt-check  # Checking code formatting
fmt-check:
	@$(INFO) "Checking code formatting..."
	@cljstyle check --report $(DIRS)


.PHONY: fmt  # Fixing code formatting
fmt:
	@$(INFO) "Fixing code formatting..."
	@cljstyle fix --report $(DIRS)


.PHONY: lint  # Linting code
lint:
	@$(INFO) "Linting project..."
	@clj-kondo --config .clj-kondo/config-ci.edn --lint $(DIRS)


.PHONY: lint-init  # Linting code with libraries
lint-init:
	@$(INFO) "Linting project's classpath..."
	@clj-kondo --config .clj-kondo/config-ci.edn --lint $(shell clj -Spath)


.PHONY: check-deps  # Check deps versions and force update them
check-deps:
	@$(INFO) "Checking deps versions..."
	@clojure -M:check-deps


.PHONY: check  # Check linting and apply formatting locally
check:
	@$(MAKE) fmt
	@$(MAKE) lint


.PHONY: up  # Run db, testing db and db admin web UI locally for development
up:
	@$(INFO) "Running db..."
	@docker-compose up -d db adminer test-db


.PHONY: clean  # Clean target dir
clean:
	@$(INFO) "Cleaning target and asset dirs..."
	@rm -rf target
	@rm -rf medbook.standalone.jar
	@rm -rf resources/public/js
	@rm -rf resources/public/prod.html

.PHONY: migrations  # Manage migrations
migrations:
	@clojure -X:migrations $(GOALS)


.PHONY: build  # Build an uberjar
build:
	@clojure -X:ui:build build
