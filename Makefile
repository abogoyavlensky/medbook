# Styling for output
YELLOW := "\e[1;33m"
NC := "\e[0m"
INFO := @sh -c '\
    printf $(YELLOW); \
    echo "=> $$1"; \
    printf $(NC)' VALUE


DIRS?=src test
GOALS = $(filter-out $@,$(MAKECMDGOALS))

.SILENT:  # Ignore output of make `echo` command


.PHONY: help  # Show list of targets with descriptions
help:
	@$(INFO) "Commands:"
	@grep '^.PHONY: .* #' Makefile | sed 's/\.PHONY: \(.*\) # \(.*\)/\1 > \2/' | column -tx -s ">"


.PHONY: deps  # Install deps
deps:
	@$(INFO) "Install deps..."
	@clojure -P -X:dev:test:migrations

.PHONY: repl  # Run repl
repl:
	@$(INFO) "Run repl..."
	@clj -M:dev:test:migrations

.PHONY: test  # Run tests with coverage
test:
	@$(INFO) "Running tests..."
	@clojure -X:dev:migrations:test

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
	@docker-compose up -d db adminer test-db chromedriver-host


.PHONY: clean  # Clean target dir
clean:
	@$(INFO) "Cleaning target dir..."
	@rm -rf target/resources/*

.PHONY: migrations  # Manage migrations
migrations:
	@clojure -X:dev:migrations $(GOALS)
