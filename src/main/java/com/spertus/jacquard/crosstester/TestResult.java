package com.spertus.jacquard.crosstester;

record TestResult(
        String testName,
        boolean passed,
        String packageUnderTestName,
        String methodUnderTestName,
        String message,
        String output) {
    private static final String SUCCESS_MESSAGE = "PASSED";

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
