package newgrader;

import com.github.javaparser.ast.CompilationUnit;

import java.util.List;

public interface Processor {
    List<Result> process(CompilationUnit cu);

    double getTotalMaxScore();
}
