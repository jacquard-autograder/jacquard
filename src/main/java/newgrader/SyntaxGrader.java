package newgrader;

import com.github.javaparser.ast.CompilationUnit;

import java.util.List;

public interface SyntaxGrader {
    List<Result> grade(CompilationUnit cu);

    double getTotalMaxScore();
}
