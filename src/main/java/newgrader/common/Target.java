package newgrader.common;

import com.github.javaparser.ast.*;
import newgrader.Autograder;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

// could be a class, package, path to either, string representation
public abstract class Target {
    private String packageName;

    public static Target fromPathString(String s) {
        return new PathStringTarget(s);
    }

    public abstract CompilationUnit toCompilationUnit();

    public abstract String toPathString();

    public Path toPath() {
        String[] parts = toPathParts();
        if (parts.length == 1) {
            return Paths.get(parts[0]);
        } else {
            return Paths.get(parts[0], Arrays.copyOfRange(parts, 1, parts.length));
        }
    }
        // only works on classes https://stackoverflow.com/a/11747859/631051
        //
      //  final File f = new File(toClass().getProtectionDomain().getCodeSource().getLocation().getPath());

    public String[] toPathParts() {
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        return toPathString().split(pattern);
    }

    public File toFile() {
        return new File(toPathString());
    }

    public Class<?> toClass() {
        throw new UnsupportedOperationException();
    }

    public String toPackageName() {
        // While we could infer the package name from the directory hierarchy,
        // it is probably better to actually get the package statement.
        if (packageName == null) {
            // This may be overkill...
            File file = toFile();
            if (file.isDirectory()) {
                throw new IllegalArgumentException("Found directory where file name was expected: " + toPathString());
            }
            CompilationUnit cu = Autograder.parse(toFile());
            Optional<PackageDeclaration> pd = cu.getPackageDeclaration();
            packageName = pd.isPresent() ? pd.get().getName().toString() : ""; // default package
        }
        return packageName;
    }

}
