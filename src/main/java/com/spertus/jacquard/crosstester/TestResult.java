package com.spertus.jacquard.crosstester;

record TestResult(String testName, boolean passed,
                  String packageUnderTestName,
                  String methodUnderTestName, String message) {
    private static final String SUCCESS_MESSAGE = "PASSED";

    static TestResult makeFailure(
            final String testName,
            final String packageUnderTestName,
            final String methodUnderTestName,
            final String message) {
        return new TestResult(testName, false, methodUnderTestName, packageUnderTestName, message);
    }

    static TestResult makeSuccess(
            final String testName,
            final String packageUnderTestName,
            final String methodUnderTestName) {
        return new TestResult(testName, true, methodUnderTestName, packageUnderTestName, SUCCESS_MESSAGE);
    }
}
