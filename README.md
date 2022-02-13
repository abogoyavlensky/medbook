# medbook

[![CI](https://github.com/abogoyavlensky/medbook/actions/workflows/deploy.yaml/badge.svg?branch=master)](https://github.com/abogoyavlensky/medbook/actions/workflows/deploy.yaml)

Medical records for patients.

## Production

Project is live at [https://medbook.bogoyavlensky.com/](https://medbook.bogoyavlensky.com/).

*The app does not have an authorization for demonstration purposes.*

## Development

### Prerequisites

Following tools should be installed for convenient local development:

- [docker](https://www.docker.com/) & [docker-compose](https://docs.docker.com/compose/install/) (latest)
- [Java 11](https://adoptopenjdk.net/)
- [clojure.tools.deps](https://github.com/clojure/tools.deps.alpha) (>= 1.10.3.1040)
- [chromedriver](https://sites.google.com/a/chromium.org/chromedriver/downloads) (>= 98.0.4758.80)

Optional tools in case you would like to run linting and formatting locally without docker on a git-hook:

- [lefthook](https://github.com/evilmartians/lefthook) (latest)
- [clj-kondo](https://github.com/clj-kondo/clj-kondo) (>= 2021.12.19)
- [cljstyle](https://github.com/greglook/cljstyle) (>= 0.15.0)

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
- [ ] Share api routes between backend and frontend using cljc-file.
- [ ] Validation form field on client side.
- [ ] TailwindCSS.
- [ ] Form library for re-frame.
- [ ] Cursor pagination.
- [ ] Collect logs and metrics.
- [ ] Serve static using CDN.

## License

Copyright © 2021 Andrey Bogoyavlensky

Distributed under the MIT License.
