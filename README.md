# predict-v21-test

## Description

A test environment for the Predictv2.1 model written in clojurescript.

The clojurescript model is tested against the R model. The key idea is that we generate *n* (where *n* can
be large) random samples of valid parameters for the R model. We then feed these parameters into
both the reference R code, and the reimplementation in clojurescript that runs on the web. We then check
that the results tables are equal to within the tolerance of floating point representations.

Note that the clojurescript model is a library which is imported both by this test code, and by the live web
code - so we can be sure that the two environments are running exactly the same model.

The prerequisites section list the repositories for the R code and the clojurescript code under test.

We use test.check and clojure.spec to get good coverage of the parameter
space. Clojure.spec is used to make the parameters conform to their valid ranges. Test.check uses an 
algorithm that creates a tree of random parameter sets - the simplest (usually lowest) parameter values
appear more often at the root of the tree, and as *n* grows, so the more complex (larger) parameters 
are chosen. If an error occurs, the algorithm uses this tree to determine the simplest (lowest) set of
parameters which still exhibit the error. This can be useful in identifying the cause of the error by
isolating a single input parameter that triggers it.

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
