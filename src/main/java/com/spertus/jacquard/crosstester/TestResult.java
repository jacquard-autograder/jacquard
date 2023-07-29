package com.spertus.jacquard.crosstester;

record TestResult(String testName, boolean passed,
                  String methodUnderTestName, String message) {
    private static final String SUCCESS_MESSAGE = "PASSED";

    static TestResult makeFailure(
            final String testName,
            final String methodUnderTestName,
            final String message) {
        return new TestResult(testName, false, methodUnderTestName, message);
    }

    static TestResult makeSuccess(
            final String testName,
            final String methodUnderTestName) {
        return new TestResult(testName, true, methodUnderTestName, SUCCESS_MESSAGE);
    }
}
