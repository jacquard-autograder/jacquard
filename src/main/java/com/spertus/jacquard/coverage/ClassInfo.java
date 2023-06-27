package com.spertus.jacquard.coverage;

/* default */ record ClassInfo(
        int instructionMissed,
        int instructionCovered,
        int branchMissed,
        int branchCovered,
        int lineMissed,
        int lineCovered,
        int complexityMissed,
        int complexityCovered,
        int methodMissed,
        int methodCovered) {
    ClassInfo(int[] fields) {
        // TODO: Improve abstraction
        this(fields[3], fields[4],
                fields[5], fields[6],
                fields[7], fields[8],
                fields[9], fields[10],
                fields[11], fields[12]);
    }

    ClassInfo(String[] fields) {
        // TODO: Improve abstraction
        this(Integer.parseInt(fields[3]),
                Integer.parseInt(fields[4]),
                Integer.parseInt(fields[5]),
                Integer.parseInt(fields[6]),
                Integer.parseInt(fields[7]),
                Integer.parseInt(fields[8]),
                Integer.parseInt(fields[9]),
                Integer.parseInt(fields[10]),
                Integer.parseInt(fields[11]),
                Integer.parseInt(fields[12]));
    }

    public double lineCoverage() {
        return lineCovered / (double) (lineCovered + lineMissed);
    }

    public double branchCoverage() {
        return branchCovered / (double) (branchCovered + branchMissed);
    }
}
