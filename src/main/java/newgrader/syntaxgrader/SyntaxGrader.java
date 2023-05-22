package newgrader.syntaxgrader;

import com.github.javaparser.ast.CompilationUnit;
import newgrader.Result;

import java.util.List;

/**
 * Syntax-based graders that make use of the linked
 * <a href="https://javaparser.org/">Java parser</a>.
 */
public interface SyntaxGrader {
    /**
     * Grades based on whether the provided compilation unit satisfies the
     * required criterion.
     *
     * @param cu the compilation unit
     * @return the results
     */
    List<Result> grade(CompilationUnit cu);

    /**
     * The maximum possible score achievable with this grader.
     *
     * @return the maximum possible score
     */
    double getTotalMaxScore();
}
