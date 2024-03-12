
# Jacquard FAQ
* [How do I make sure I have the latest version of the Jacquard library?](https://github.com/jacquard-autograder/jacquard/blob/main/doc/FAQ.md#how-do-i-make-sure-i-have-the-latest-version-of-the-jacquard-library)
* [What configuration options are there?](https://github.com/jacquard-autograder/jacquard/blob/main/doc/FAQ.md#what-configuration-options-are-there)
* [How do I use Checkstyle?](https://github.com/jacquard-autograder/jacquard/blob/main/doc/FAQ.md#how-do-i-use-checkstyle)
* [What's PMD? How do I use it?](https://github.com/jacquard-autograder/jacquard/blob/main/doc/FAQ.md#whats-pmd-how-do-i-use-it)
* [How do I set test result visibility?](https://github.com/jacquard-autograder/jacquard/blob/main/doc/FAQ.md#how-do-i-set-test-result-visibility)
* [How is code coverage measured?](https://github.com/jacquard-autograder/jacquard/blob/main/doc/FAQ.md#how-is-code-coverage-measured)
* [What is cross-testing?](https://github.com/jacquard-autograder/jacquard/blob/main/doc/FAQ.md#what-is-cross-testing)
* [Why was the name "Jacquard" chosen?](https://github.com/jacquard-autograder/jacquard/blob/main/doc/FAQ.md#why-was-the-name-jacquard-chosen)
* [Where can I view the Javadoc?](https://github.com/jacquard-autograder/jacquard/blob/main/doc/FAQ.md#where-can-i-view-the-javadoc)
* [Where can I get support?](https://github.com/jacquard-autograder/jacquard/blob/main/doc/FAQ.md#where-can-i-get-support)


## How do I make sure I have the latest version of the Jacquard library?

Make the changes shown in commit [Use Jacquard 1.00 instead of snapshot](https://github.com/jacquard-autograder/jacquard-example2/commit/357aabef51b5d8fd541c789753af6801878c77fe).

## What configuration options are there?
There are currently 3 configurable values:
* `timeout` (default: `10_000L`), how many milliseconds to run a test before termination;
  a value of `0` means never to timeout
* `javaLevel` (default: 17), the Java language level used for [syntax-based graders](https://jacquard.ellenspertus.com/com/spertus/jacquard/syntaxgrader/package-summary.html)
* `visibility` (default: [`Visibility.VISIBLE`](https://jacquard.ellenspertus.com/com/spertus/jacquard/common/Visibility.html#VISIBLE)),
  the visibility of test results (except for `JUnitTester` results, which are specified differently)

To use the default values, call [`Autograder.init()`](https://jacquard.ellenspertus.com/com/spertus/jacquard/common/Autograder.html#init())
at the start of your program. Here's how to explicitly set other values:

```java
Autograder.Builder builder = Autograder.Builder.getInstance();

// By default, tests time out in 10,000 ms if they don't complete.
builder.timeout(5000); // set timeout to 5 s

// By default, Java level 17 is used.
builder.javaLevel(11); // use Java level 11

// By default, all tests results are visible.
builder.visibility(Visibility.HIDDEN); // hide test results
builder.build();
```
This can be written more concisely:
```
Autograder.Builder.getInstance()
    .timeout(5000)
    .javaLevel(11)
    .visibility(Visibility.HIDDEN)
    .build();
```

See also the [Autograder configuration chapter](https://northeastern.hosted.panopto.com/Panopto/Pages/Viewer.aspx?id=6b00de8f-4abe-49a6-a348-b12e0012f37b&start=15) (0:15-2:06) from [Example 2.1: Going through a more complicated AutograderMain](https://northeastern.hosted.panopto.com/Panopto/Pages/Viewer.aspx?id=6b00de8f-4abe-49a6-a348-b12e0012f37b).

## How do I use Checkstyle?

For general usage information, see [Checkstyle website](https://checkstyle.sourceforge.io/), 
especially [Checkstyle configuration](https://checkstyle.sourceforge.io/config.html). 

Here is how to create a `CheckstyleGrader` in Jacquard:

```java
CheckstyleGrader checkstyleGrader = new CheckstyleGrader(
        "config/checkstyle-rules.xml", // path to configuration file
        1.0, // penalty per violation
        5.0); // maximum penalty/points
```

See also the [`CheckstyleGrader` javadoc](https://jacquard.ellenspertus.com/com/spertus/jacquard/checkstylegrader/CheckstyleGrader.html).

We recommend putting your configuration file in your project's `config/`
directory so it is copied to Gradescope. We also recommend sharing it with
students so they can run checkstyle in their
IDE ([IntelliJ plugin](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea),
[Eclipse plugin](https://checkstyle.org/eclipse-cs/#!/))
before uploading. The IntelliJ plugin supports using a local configuration
file or accessing one via URL, so students don't need to download it
(but will need to configure the plugin to point to it).

For more detail, see
[Jacquard Example 0](https://github.com/jacquard-autograder/jacquard-example0).

## What's PMD? How do I use it?
[PMD](https://pmd.github.io/) (which is not an acronym) is a source code analyzer
capable of more complex checks than Checkstyle, such as whether the `@Override`
annotation is always used where permitted.

PMD rules are organized into rulesets, which, as the name suggests, are sets of rules.
You can [make your own rulesets](https://pmd.github.io/pmd/pmd_userdocs_making_rulesets.htm)
or use [Java rulesets](https://github.com/pmd/pmd/tree/master/pmd-java/src/main/resources)
built in to PMD, such as [`category/java/bestpractices.xml`](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/resources/category/java/bestpractices.xml).

Jacquard's [PMDGrader](https://jacquard.ellenspertus.com/com/spertus/jacquard/pmdgrader/PmdGrader.html)
has two static factory methods:
* [`createFromRuleSetPaths()`](https://jacquard.ellenspertus.com/com/spertus/jacquard/pmdgrader/PmdGrader.html#createFromRuleSetPaths(double,double,java.lang.String...)),
  which lets you specify one or more rulesets to be used in their entirety [used in [Jacquard Example 0](https://github.com/jacquard-autograder/jacquard-example0)]
* [`createFromRules()`](https://jacquard.ellenspertus.com/com/spertus/jacquard/pmdgrader/PmdGrader.html#createFromRules(double,double,java.lang.String,java.lang.String,java.lang.String...)),
  which lets you specify one ruleset and one or more rules from that ruleset [used in [Jacquard Example 2](https://github.com/jacquard-autograder/jacquard-example2)]

There are PMD plugins for [IntelliJ](https://plugins.jetbrains.com/plugin/1137-pmd) and [Eclipse](https://marketplace.eclipse.org/category/free-tagging/pmd).

## How do I set test result visibility?

Gradescope specifies four levels of visibility in [Autograder Specifications](https://gradescope-autograders.readthedocs.io/en/latest/specs/):

* `hidden`: test case will never be shown to students
* `after_due_date`: test case will be shown after the assignment's due date has passed. If late submission is allowed, then test will be shown only after the late due date.
* `after_published`: test case will be shown only when the assignment is explicitly published from the "Review Grades" page
* `visible` (default): test case will always be shown

These is a one-to-one correspondence between these visibility levels and the enumerated type [`Visibility`](http://jacquard.ellenspertus.com/com/spertus/jacquard/common/Visibility.html).

Unless otherwise specified, all test results are immediately `visible` to students.

### `JUnitTester` results
Unit tests run through [`JUnitTester`](https://jacquard.ellenspertus.com/com/spertus/jacquard/junittester/JUnitTester.html) (as
opposed to the cross-tester) must be annotated with [`@GradedTest`](https://jacquard.ellenspertus.com/com/spertus/jacquard/junittester/GradedTest.html). The
attribute `visibility` has the default value [`Visibility.VISIBLE`](https://jacquard.ellenspertus.com/com/spertus/jacquard/common/Visibility.html#VISIBLE) but
can be set to any other visibility. This code is from [Jacquard Example 0](https://github.com/jacquard-autograder/jacquard-example0):
```java
@Test
@GradedTest(name = "works for empty list", points = 5.0, visibility = Visibility.AFTER_PUBLISHED)
public void iteratorOverEmptyList() {
    FavoritesIterator<String> iterator = new FavoritesIterator<>(favoriteHotSauces0);

    // No items should be returned.
    assertFalse(iterator.hasNext());
    assertThrows(NoSuchElementException.class, () -> iterator.next());
}
```

### Other results
The visibility level can be set for all other types of autograder results through the
[initial configuration](https://github.com/jacquard-autograder/jacquard-examples/blob/main/doc/FAQ.md#what-configuration-options-are-there).

The visibility level of a generated [`Result`](https://jacquard.ellenspertus.com/com/spertus/jacquard/common/Result.html) can be mutated by calling the [`changeVisibility(Visibility visibility)` instance method](https://jacquard.ellenspertus.com/com/spertus/jacquard/common/Result.html#changeVisibility(com.spertus.jacquard.common.Visibility)) or [`Result.changeVisibility(List<Result> results, Visibility visibility)`](https://jacquard.ellenspertus.com/com/spertus/jacquard/common/Result.html#changeVisibility(java.util.List,com.spertus.jacquard.common.Visibility)), as shown:

```java
// Use the default configuration, which includes full visibility.
Autograder.init();
final Target target = Target.fromClass(FavoritesIterator.class);
List<Result> results = new ArrayList();

// PMD results should be visible only after the due date.
PmdGrader pmdGrader = PmdGrader.createFromRules(
        1.0,
        5.0,
        "category/java/bestpractices.xml",
        "MissingOverride");
List<Result> pmdResults = pmdGrader.grade(target);
// Change visibility before adding to results.
Result.changeVisibility(pmdResults, Visibility.AFTER_DUE_DATE);
results.addAll(pmdResults);
```

## How is code coverage measured?

Code coverage is measured using [JaCoCo](https://www.jacoco.org/jacoco/index.html). We recommend
having students run JaCoCo inside IntelliJ or Eclipse, because the plugins show which lines of
code are exercised by the tests.

When creating a [`CodeCoverageTester`](https://jacquard.ellenspertus.com/com/spertus/jacquard/coverage/CodeCoverageTester.html),
a [`Scorer`](https://jacquard.ellenspertus.com/com/spertus/jacquard/coverage/Scorer.html) must be
provided to convert the line and branch coverage percentages into points. The concrete scorers are
provided:
* [`LinearScorer`](https://jacquard.ellenspertus.com/com/spertus/jacquard/coverage/LinearScorer.html),
  which uses a linear function of the line and branch coverage percentages
* [`LinearBranchScorer`](https://jacquard.ellenspertus.com/com/spertus/jacquard/coverage/LinearBranchScorer.html),
  which uses a linear function of the branch coverage percentage (ignoring line coverage)
* [`LinearLineScorer`](https://jacquard.ellenspertus.com/com/spertus/jacquard/coverage/LinearLineScorer.html),
  which uses a linear function of the line coverage percentage (ignoring branch coverage)
  If you want to write your own scorer, we suggest viewing [`LinearScorer.java`](https://github.com/jacquard-autograder/jacquard/blob/main/src/main/java/com/spertus/jacquard/coverage/LinearScorer.java).

## What is cross-testing?
Cross-testing is my term for running multiple sets of tests against multiple implementations.
Most autograders only run instructor tests against student code. Jacquard also supports running
student tests against multiple versions of instructor code.

Cross-testing using submitted test code is specified by a CSV file, such as
[Example 2's `student-tests.csv`](https://github.com/jacquard-autograder/jacquard-example2/blob/main/src/main/resources/student-tests.csv):

|   | student  | correct   | buggy  |
|---: | :--: | :--: | :--: |
| size  | 10  | 5 | -5 |
| concat  | 20  | 10  | -10  |

The header and first row mean:
* If the tests do not report any errors on the implementation of the `size()` method in the `student` package, 10 points are earned.
* If the tests do not report any errors on the implementation of the `size()` method in the `correct` package, 5 points are earned.
* If the tests do report an errors on the implementation of the `size()` method in the `buggy` package, 5 points are earned.

The negative signs in the "buggy" column indicate that the tests are inverted (i.e., points are earned if they fail).

Test names must start with the name of the method under test, such as `sizeWorksForEmptyList()` for tests of `size()`.

This excerpt from [Example 2's `main()` method](https://github.com/jacquard-autograder/jacquard-example2/blob/main/src/main/java/student/AutograderMain.java#L79)
shows how the cross-tester is programmatically created and run:
```java
// Create CrossTester to run student tests on:
// * student code (20 points)
// * hidden correct implementation (15 points)
// * hidden buggy implementation (15 points)
// Grading detail is in student-tests.csv.
CrossTester crossTester = new CrossTester(
    student.ILOSTest.class, // the test to run
    "student-tests.csv" // the name of the CSV file
);
results.addAll(crossTester.run());
```

See also the [Example 2 documentation](https://github.com/jacquard-autograder/jacquard-example2?tab=readme-ov-file#configini) for needed changes to `config.ini` and the [Example 2 cross-tester video](https://northeastern.hosted.panopto.com/Panopto/Pages/Viewer.aspx?id=165ca9fa-98eb-4f0f-8841-b069013430c5).

## Why was the name "Jacquard" chosen?

The CSV files used for cross-testing made me think of looms, such as the [looms created by
Joseph Marie Jacquard](https://en.wikipedia.org/wiki/Jacquard_machine), which were
controlled by punched cards so play an important role in computing history. Also, the
starting letters correspond to Java or Java Autograder. Claude.ai suggested
this backronym:

* **J**ava
* **A**ssignment
* **C**hecking with
* **Q**uality
* **U**nit-testing,
* **A**nalysis,
* **R**eporting, and
* **D**iagnostics

## Where can I view the Javadoc?

The Javadoc is available at [https://jacquard.ellenspertus.com/](https://jacquard.ellenspertus.com/).
There are also linked badges at the bottom of Markdown pages, such as this one.

## Where can I get support?

There are low-volume Google
groups [jacquard-announce](https://groups.google.com/g/jacquard-announce)
and [jacquard-discuss](https://groups.google.com/g/jacquard-discuss).

You can also [create issues](https://github.com/jacquard-autograder/jacquard/issues)
(feature requests and bug reports).

[![Javadoc](https://img.shields.io/badge/JavaDoc-Online-green)](https://jacquard.ellenspertus.com/)
