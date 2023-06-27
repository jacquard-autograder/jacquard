package com.spertus.jacquard.crosstester;

record TestResult(String testName, boolean passed,
                  String methodUnderTestName, String message) {
    private static final String SUCCESS_MESSAGE = "PASSED";

    static TestResult makeFailure(String testName, String methodUnderTestName, String message) {
        return new TestResult(testName, false, methodUnderTestName, message);
    }

    static TestResult makeSuccess(String testName, String methodUnderTestName) {
        return new TestResult(testName, true, methodUnderTestName, SUCCESS_MESSAGE);
    }
}
