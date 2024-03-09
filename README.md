# Jacquard

Jacquard is a Java autograder developed at Northeastern University
with Gradescope integration. It has been used at multiple universities.

## Features

* Static analysis with [Checkstyle](https://checkstyle.org/) and
  [PMD](https://pmd.github.io/).
* Syntactic analysis using [JavaParser](https://github.com/javaparser/javaparser)
* Test coverage and cyclomatic complexity measurement with
  [JaCoCo](https://www.jacoco.org/jacoco/).
* Unit testing with [JUnit 5](https://junit.org/junit5/), including:
    * running staff tests against student code
    * running student tests against
        * student code
        * intentionally buggy staff-written code
        * correct staff-written code

## Examples

The best way to learn Jacquard is to review the examples, which include video explanations.

You should start with [Jacquard Example 0](https://github.com/jacquard-autograder/jacquard-example0), 
which shows how to grade based on Checkstyle, PMD, and JUnit 5 tests.

[Jacquard Example 1](https://github.com/jacquard-autograder/jacquard-example1) adds syntactic analysis based on 
the parse tree, such as whether students use certain language features and methods.

[Jacquard Example 2](https://github.com/jacquard-autograder/jacquard-example2) demonstrates:
* Changing the default configuration values:
  * visibility level of results
  * timeouts
  * language level
* Running Checkstyle and PMD on multiple student files
* Measuring code coverage of student tests
* Running unit tests on student code
* Fine-grained control of visibility
* Cross-testing, i.e., running student tests against
    * student code
    * correct code
    * buggy code

Example 2 does not depend on Example 1, which may be skipped if you are not interested in
syntactic analysis.

## Further Information

* [Javadoc](https://jacquard.ellenspertus.com/)
* [FAQ](doc/FAQ.md)

There are low-volume Google groups [jacquard-announce](https://groups.google.com/g/jacquard-announce)
and [jacquard-discuss](https://groups.google.com/g/jacquard-discuss).

## Information for Contributors

I welcome contributors, especially ones interested in integrating Jacquard with learning
management systems (LMSs) and GitHub Classroom. See [tips for contributors](CONTRIBUTING.md).

I also welcome [reports](https://github.com/jacquard-autograder/jacquard/issues)
suggestions of how to improve Jacquard, the documentation, or examples.

## Credits

Jacquard was influenced by Tim Kutcher's [JGrade](https://github.com/tkutcher/jgrade) and
includes some of its `Visibility` and `GradedTest` code.

[![Javadoc](https://img.shields.io/badge/JavaDoc-Online-green)](https://jacquard.ellenspertus.com/)
