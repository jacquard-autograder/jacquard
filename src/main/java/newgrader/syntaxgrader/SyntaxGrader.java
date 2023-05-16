package newgrader.syntaxgrader;

import com.github.javaparser.ast.CompilationUnit;
import newgrader.Result;

import java.util.List;

public interface SyntaxGrader {
    List<Result> grade(CompilationUnit cu);

    double getTotalMaxScore();
}
