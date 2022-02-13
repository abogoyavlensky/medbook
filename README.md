# medbook

[![CI](https://github.com/abogoyavlensky/medbook/actions/workflows/deploy.yaml/badge.svg?branch=master)](https://github.com/abogoyavlensky/medbook/actions/workflows/deploy.yaml)

Medical records for patients.

## Production

Project is live at [https://medbook.bogoyavlensky.com/](https://medbook.bogoyavlensky.com/).

*The app does not have an authorization for demonstration purposes.*

## Development

### Prerequisites

Following tools should be installed for convenient local development:

- docker & docker-compose (latest)
- Java 11
- clojure.tools.deps (>= 1.10.3.1040)
- chromedriver (>= 98.0.4758.80)

Optional tools in case you would like to run linting and formatting locally without docker on a git-hook:

- lefthook (latest)
- cljstyle (>= 0.15.0)
- clj-kondo (>= 2021.12.19)

### Manage project

First you need to run external dependencies:

```shell
make up
```

Now you can run the project inside repl:

```bash
make repl
(reset)  # run and reset the system;
(stop)  # stop the system;
(cljs-repl)  # run cljs repl
:quit  # quit from cljs repl
(run-all-tests)  # run all test
```

Also, there are some useful make commands: 

```bash
make test  # run tests for api and integration tests ui locally
make lint  # lint project files
make fmt  # format project files
make check-deps  # check outdated deps
make clean  # clean all temp files
make migrations <cmd>  # run migrations commands
```

Full list of make commands you could get by executing:

```shell
make help
```


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
