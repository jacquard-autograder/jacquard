For now, these are just some tips:

## Running Tests

The `test/java` sourceset consists mostly of tests that should be directly run; however, 
it also contains some source files that are essentially resources for other tests. (Perhaps
these should be moved to `resources/`. These classes, which include [SampleTest](src/test/java/com/spertus/jacquard/junittester/SampleTest.java),
are tagged with `"IndirectTest"`, which causes them to be ignored when running the Gradle `test` task.

![IntelliJ Gradle panel screenshot with the task "test" in the "verification" section 
selected](https://github.com/espertus/jacquard/assets/661056/5f33dcb0-76ab-4e4e-80e5-bf344c7a0ab9)


## build.gradle
IntelliJ sometimes grays out most of `build.gradle`. If this happens, select `Reload All Gradle Projects` (the circular icon in the top left
of the [Gradle panel screenshot](https://github.com/espertus/jacquard/assets/661056/5f33dcb0-76ab-4e4e-80e5-bf344c7a0ab9).
[[Stack Overflow](https://stackoverflow.com/a/60207549/631051)].
