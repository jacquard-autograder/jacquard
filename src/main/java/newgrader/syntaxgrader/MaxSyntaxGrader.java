package newgrader.syntaxgrader;

import com.github.javaparser.ast.CompilationUnit;
import newgrader.Result;

import java.util.*;

/**
 * A meta-processor that returns the highest-scoring results of its constituent
 * processors.
 */
public class MaxSyntaxGrader implements SyntaxGrader {
    private final List<SyntaxGrader> processors = new ArrayList<>();
    private final double totalMaxScore;

    /**
     * Creates a meta-processor whose results will be those of its
     * highest-scoring constituent processors. If the result of any call
     * to {@link SyntaxGrader#grade(CompilationUnit)} is the highest possible
     * score, it does not run later processors.
     *
     * @param processors the constituent processors
     * @throws IllegalArgumentException if fewer than 2 processors are provided
     *                                  or any have different maximum total scores
     */
    public MaxSyntaxGrader(List<SyntaxGrader> processors) {
        if (processors.size() < 2) {
            throw new IllegalArgumentException("At least two processors must be provided.");
        }
        totalMaxScore = processors.get(0).getTotalMaxScore();
        for (int i = 1; i < processors.size(); i++) {
            if (processors.get(i).getTotalMaxScore() != totalMaxScore) {
                throw new IllegalArgumentException("Not all processors have same totalMaxScore");
            }
        }

        this.processors.addAll(processors);
    }

    @Override
    public List<Result> grade(CompilationUnit cu) {
        List<Result> bestResults = null;
        double bestScore = 0;
        for (final SyntaxGrader processor : processors) {
            final List<Result> results = processor.grade(cu);
            final double score = results.stream().mapToDouble(Result::score).sum();
            if (bestResults == null || score > bestScore) {
                bestResults = results;
                bestScore = score;
            }
        }
        return bestResults;
    }

    @Override
    public double getTotalMaxScore() {
        return totalMaxScore;
    }
}
