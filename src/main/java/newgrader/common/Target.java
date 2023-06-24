package newgrader.common;

import com.github.javaparser.ast.*;
import newgrader.syntaxgrader.Parser;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

// could be a class, package, path to either, string representation
public abstract class Target {
    private String packageName; // lazy initialization
    private String className; // lazy initialization

    public static Target fromPath(Path path) {
        return fromAbsolutePathString(path.toAbsolutePath().toString());
    }

    public static Target fromRelativePathString(String s) {
        String absPath = FileSystems.getDefault().getPath(s).normalize().toAbsolutePath().toString();
        return fromAbsolutePathString(absPath);
    }

    public static Target fromAbsolutePathString(String s) {
        return new PathStringTarget(s);
    }

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
            CompilationUnit cu = Parser.parse(toFile());
            Optional<PackageDeclaration> pd = cu.getPackageDeclaration();
            packageName = pd.isPresent() ? pd.get().getName().toString() : ""; // default package
        }
        return packageName;
    }

    public String toClassName() {
        if (className == null) {
            File file = toFile();
            if (file.isDirectory()) {
                throw new IllegalArgumentException("Found directory where file name was expected: " + toPathString());
            }
            String fileName = file.getName();
            if (fileName.endsWith(".java")) {
                className = fileName.substring(0, fileName.indexOf(".java"));
            } else {
                throw new IllegalArgumentException("Specified file is not java source code: " + fileName);
            }
        }
        return className;
    }

    public Path toDirectory() {
        File file = toFile();
        if (file.isDirectory()) {
            return file.toPath();
        } else {
            return file.getParentFile().toPath();
        }

    }
    public CompilationUnit toCompilationUnit() {
        return Parser.parse(toFile());
    }

}
