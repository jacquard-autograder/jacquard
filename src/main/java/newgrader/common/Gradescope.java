package newgrader.common;

import java.util.List;

import org.json.*;

public class Gradescope {
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
                    .put("output", result.message());
        } catch (JSONException e) {
            throw new InternalError(e);
        }
    }
}
