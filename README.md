# predict-v21-test

## Description

A test environment for the Predictv2.1 model written in clojurescript.
This is tested against the R model.

We use test.check and clojure.spec to get good coverage of the parameter
space.

In order to shell out to R it's easiest to target NodeJS.
Unfortunately, test.check can only test synchronous functions at the moment.
Fortunately, the node shelljs package gives us synchronous exec calls.

## Prerequisites

First clone these repositories, and run `lein install` in each
```
https://github.com/wintoncentre/predict-v21-cljs.git
https://github.com/wintoncentre/predict-v21-r.git
https://github.com/wintoncentre/winton-utils.git

```
Note that `predict-v21-cljs` has a transitive dependency on winton-utils.

Make sure NodeJS is installed, and then run
```
npm install
```

## REPL setup
Follow https://github.com/bhauman/lein-figwheel/wiki/Running-figwheel-in-a-Cursive-Clojure-REPL
to create a figwheel run configuration.

## Running the tests
Start figwheel, and then run the node app. Figwheel should then connect.

The number of tests can be adjusted by changing 'models.cljs-r-compare/gen-size

```sh
node target/js/runner/runner.js
```
This will run the tests and then connect to figwheel.


## Testing from the REPL
Check project.clj for the usual options.


## License

Copyright © 2018 University of Cambridge

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
