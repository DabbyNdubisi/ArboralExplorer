# ArboralExplorer
Visualization program to play with Arborally Satisfied Sets

## Getting started

* Download everything
* Run `ant jar` in the main directory
* Run `java -jar dist/ArboralExplorer.jar`

## Using the integrated ILP solver

We use Coin-Or CMPL to solve an Integer Programming formulation of the problem. To get this running, do the following:

* If you're running linux64, set the environment variable `CMPLBINARY` to "lib/Cmpl/bin/cmpl"
* If you're running another OS, first download the correct version of CMPL from [Coliop](http://www.coliop.org/download.html), then set the environment variable `CMPLBINARY` to "<path to CMPL>/Cmpl/bin/cmpl"

## Contributing

* The repository is a [NetBeans](https://netbeans.org/) project, since we use that for development
* Pull requests are welcome, especially for features in [TODO.txt]