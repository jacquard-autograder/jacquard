package newgrader;

import com.github.javaparser.ast.CompilationUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * A meta-processor that returns the highest-scoring results of its constituent
 * processors.
 */
public class MaxProcessor implements Processor {
    private final List<Processor> processors = new ArrayList<>();
    private final double totalMaxScore;

    /**
     * Creates a meta-processor whose results will be those of its
     * highest-scoring constituent processors. If the result of any call
     * to {@link Processor#process(CompilationUnit)} is the highest possible
     * score, it does not run later processors.
     *
     * @param processors the constituent processors
     * @throws IllegalArgumentException if fewer than 2 processors are provided
     *                                  or any have different maximum total scores
     */
    public MaxProcessor(List<Processor> processors) {
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
    public List<Result> process(CompilationUnit cu) {
        List<Result> bestResults = null;
        double bestScore = 0;
        for (Processor processor : processors) {
            List<Result> results = processor.process(cu);
            double score = results.stream().mapToDouble(Result::score).sum();
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
