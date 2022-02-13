# medbook

Medical records for patients.

*TODO: update readme!*

## Installation

Download from https://github.com/medbook/medbook

## Usage

Run the project's tests (they'll fail until you edit them):

    $ clojure -T:build test

Run the project's CI pipeline and build an uberjar (this will fail until you edit the tests to pass):

    $ clojure -T:build ci

This will produce an updated `pom.xml` file with synchronized dependencies inside the `META-INF`
directory inside `target/classes` and the uberjar in `target`. You can update the version (and SCM tag)
information in generated `pom.xml` by updating `build.clj`.

If you don't want the `pom.xml` file in your project, you can remove it. The `ci` task will
still generate a minimal `pom.xml` as part of the `uber` task, unless you remove `version`
from `build.clj`.

Run that uberjar:

    $ java -jar target/medbook-0.1.0-SNAPSHOT.jar

If you remove `version` from `build.clj`, the uberjar will become `target/medbook-standalone.jar`.

## Development

### Prerequisites

TODO: add links for all tools!

Following tools should be installed for convenient local development:

- docker & docker-compose (latest)
- Java 11
- clojure.tools.deps (>= 1.10.3.1040)
- chromedriver (>= 98.0.4758.80)

Optional tools in case you would like to run linting and formatting locally without docker on a git-hook:

- lefthook (latest)
- cljstyle (>= 0.15.0)
- clj-kondo (>= 2021.12.19)

## TODO
- [ ] Test cljs with [re-frame-test](https://github.com/day8/re-frame-test)
- [ ] TailwindCSS.
- [ ] Validation form field on client side.
- [ ] Form library for re-frame.
- [ ] Cursor pagination.
- [ ] Collect logs and metrics.
- [ ] Serve static using CDN.

## License

Copyright © 2021 Andrey Bogoyavlensky

Distributed under the MIT License.
