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
@SuppressWarnings("PMD.EmptyCatchBlock")
public class GradescopePublisher extends Publisher {
    private static final Path RESULTS_PATH = Path.of("results");
    private static final String RESULTS_FILE_NAME = "results.json";

    private JSONObject convertToJson(final List<Result> results) {
        final JSONArray testResults = new JSONArray();
        for (final Result result : results) {
            testResults.put(assemble(result));
        }
        return new JSONObject().put("tests", testResults);
    }

    @Override
    public String serializeResults(final List<Result> results) {
        return convertToJson(results).toString();
    }

    @Override
    public boolean publishResults(final List<Result> results, Result.Order order) {
        try {
            if (Files.exists(RESULTS_PATH)) {
                List<Result> sortedResults = Result.reorderResults(results, order);
                Files.write(
                        RESULTS_PATH.resolve(RESULTS_FILE_NAME),
                        serializeResults(sortedResults).getBytes());
                return true;
            }
        } catch (IOException e) {
            // fall through
        }
        return false;
    }

    @Override
    public void displayResults(final List<Result> results) {
        System.out.println(convertToJson(results)   // NOPMD
                .toString(4));
    }

    private JSONObject assemble(final Result result) {
        try {
            return new JSONObject()
                    .put("name", result.getName())
                    .put("score", result.getScore())
                    .put("max_score", result.getMaxScore())
                    .put("output", result.getMessage())
                    .put("visibility", result.getVisibility().getGradescopeText());
        } catch (JSONException e) {
            throw new InternalError(e);
        }
    }
}
