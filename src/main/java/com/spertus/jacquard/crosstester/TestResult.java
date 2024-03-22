package com.spertus.jacquard.crosstester;

class TestResult {
    private static final String SUCCESS_MESSAGE = "PASSED";
    private final String testName;
    private final boolean passed;
    private final String packageUnderTestName;
    private final String methodUnderTestName;
    private final String message;
    private final String output;

    public String testName() {
        return testName;
    }

    public boolean passed() {
        return passed;
    }

    public String packageUnderTestName() {
        return packageUnderTestName;
    }

    public String methodUnderTestName() {
        return methodUnderTestName;
    }

    public String message() {
        return message;
    }

    public String output() {
        return output;
    }

    TestResult(String testName, boolean passed, String packageUnderTestName, String methodUnderTestName, String message, String output) {
        this.testName = testName;
        this.passed = passed;
        this.packageUnderTestName = packageUnderTestName;
        this.methodUnderTestName = methodUnderTestName;
        this.message = message;
        this.output = output;
    }

    static TestResult makeFailure(
            final String testName,
            final String packageUnderTestName,
            final String methodUnderTestName,
            final String message,
            final String output) {
        return new TestResult(testName, false, methodUnderTestName, packageUnderTestName, message, output);
    }

    static TestResult makeSuccess(
            final String testName,
            final String packageUnderTestName,
            final String methodUnderTestName,
            final String output) {
        return new TestResult(testName, true, methodUnderTestName, packageUnderTestName, SUCCESS_MESSAGE, output);
    }
}
