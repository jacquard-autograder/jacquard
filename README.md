# Jacquard

This is a Java autograder released in
late summer 2023. While most of it is platform-independent, it will
include Gradescope integration. I would be happy to get collaborators
familiar with other systems this could hook into.

Features include:

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

Jacquard was influenced by Tim Kutcher's [JGrade](https://github.com/tkutcher/jgrade) and
includes some of its `Visibility` and `GradedTest` code.

[![Javadoc](https://img.shields.io/badge/JavaDoc-Online-green)](https://www.ellenspertus.com/jacquard)
