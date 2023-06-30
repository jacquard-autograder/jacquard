package com.spertus.jacquard.publisher;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import com.spertus.jacquard.common.Result;
import org.json.*;

/**
 * The interface to Gradescope.
 *
 * @see <a href="https://gradescope-autograders.readthedocs.io/en/latest/specs/">
 * Gradescope Autograder Specifications</a>
 */
public class GradescopePublisher extends Publisher {
    private static final Path RESULTS_PATH = Path.of("results");
    private static final String RESULTS_FILE_NAME = "results.json";

    private JSONObject convertToJson(List<Result> results) {
        JSONArray testResults = new JSONArray();
        for (Result result : results) {
            testResults.put(assemble(result));
        }
        return new JSONObject().put("tests", testResults);
    }

    @Override
    public String serializeResults(List<Result> results) {
        return convertToJson(results).toString();
    }

    @Override
    public boolean publishResults(List<Result> results) {
        try {
            if (Files.exists(RESULTS_PATH)) {
                Files.write(RESULTS_PATH.resolve(RESULTS_FILE_NAME), serializeResults(results).getBytes());
                return true;
            }
        } catch (IOException e) {
            // fall through
        }
        return false;
    }

    @Override
    public void displayResults(List<Result> results) {
        System.out.println(convertToJson(results)
                .toString(4));
    }

    private JSONObject assemble(Result result) {
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
