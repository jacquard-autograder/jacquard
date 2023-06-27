package com.spertus.jacquard.common;

import java.util.List;

import org.json.*;

/**
 * The interface to Gradescope.
 *
 * @see <a href="https://gradescope-autograders.readthedocs.io/en/latest/specs/">
 *     Gradescope Autograder Specifications</a>
 */
public class Gradescope {
    /**
     * Converts the results into the JSON format expected by Gradescope.
     *
     * @param results the results
     * @return the string
     */
    public static String serialize(List<Result> results) {
        JSONArray testResults = new JSONArray();
        for (Result result : results) {
            testResults.put(assemble(result));
        }
        return new JSONObject().put("tests", testResults).toString();
    }

    private static JSONObject assemble(Result result) {
        try {
            return new JSONObject()
                    .put("name", result.name())
                    .put("score", result.score())
                    .put("max_score", result.maxScore())
                    .put("output", result.output());
        } catch (JSONException e) {
            throw new InternalError(e);
        }
    }
}
