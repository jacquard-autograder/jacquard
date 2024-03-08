# Jacquard

Jacquard is a Java autograder developed at Northeastern University
with Gradescope integration.

## Features

* Syntax-based analysis using [JavaParser](https://github.com/javaparser/javaparser)
* Static analysis with [Checkstyle](https://checkstyle.org/) and
  [PMD](https://pmd.github.io/).
* Test coverage and cyclomatic complexity measurement with 
  [JaCoCo](https://www.jacoco.org/jacoco/).
* Unit testing with [JUnit 5](https://junit.org/junit5/), including:
  * running staff tests against student code
  * running student tests against
    * student code
    * intentionally buggy staff-written code
    * correct staff-written code

For more information, see [Jacquard Examples](https://github.com/espertus/jacquard-examples).

## User Information

* [Javadoc](https://jacquard.ellenspertus.com/)
* [FAQ](doc/FAQ.md)
* [User Guide](doc/Guide.md) (incomplete)
* Examples
  * [Jacquard Example 0](https://github.com/jacquard-autograder/jacquard-example0), which shows how to grade based on Checkstyle, PMD, and JUnit 5 tests.
  * [Jacquard Example 1](https://github.com/jacquard-autograder/jacquard-example1), which adds grading based on the parse tree, such as whether students
    use certain language features and methods.
* Groups
  * [jacquard-announce](https://groups.google.com/g/jacquard-announce)
  * [jacquard-discuss](https://groups.google.com/g/jacquard-discuss)

## Information for Contributors

I welcome contributors, especially ones interested in integrating Jacquard with learning
management systems (LMSs) and GitHub Class.

See [tips for contributors](CONTRIBUTING.md).

## Credits
Jacquard was influenced by Tim Kutcher's [JGrade](https://github.com/tkutcher/jgrade) and
includes some of its `Visibility` and `GradedTest` code.

[![Javadoc](https://img.shields.io/badge/JavaDoc-Online-green)](https://jacquard.ellenspertus.com/)
