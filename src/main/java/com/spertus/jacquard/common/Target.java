package com.spertus.jacquard.common;

import java.io.File;
import java.nio.file.*;
import java.util.Objects;

/**
 * The target of a {@link Grader}, such as a file or a directory.
 */
public final class Target {
    private final Path path;
    private String packageName; // lazy initialization
    private String className; // lazy initialization

    private Target(Path path) {
        this.path = path;
    }

    /**
     * Creates a target from a path, which could be to a file or directory.
     *
     * @param path the path
     * @return the target
     */
    public static Target fromPath(Path path) {
        return new Target(path);
    }

    /**
     * Creates a target from a relative path string. It does not matter whether
     * forward slashes or backslashes are used as separators.
     *
     * @param s a relative path string
     * @return the target
     */
    public static Target fromPathString(String s) {
        // https://stackoverflow.com/a/40163941/631051
        Path absPath = FileSystems.getDefault().getPath(s).normalize().toAbsolutePath();
        return new Target(absPath);
    }

    /**
     * Gets the string representation of this target's absolute path.
     *
     * @return the string representation of this target's absolute path
     */
    public String toPathString() {
        return path.toString();
    }

    /**
     * Gets this target's path
     *
     * @return this target's path
     */
    public Path toPath() {
        return path;
    }

    /**
     * Gets this target's file (which may be a directory).
     *
     * @return this target's file
     */
    public File toFile() {
        return new File(toPathString());
    }

//    /**
//     * Gets the name of the package of the target Java file.
//     *
//     * @return the package name (empty string for the default package)
//     * @throws ClientException if the target is not a Java file
//     *                         or cannot be parsed
//     */
//    public String toPackageName() {
//        // While we could infer the package name from the directory hierarchy,
//        // that might not work for files in resources directories, etc.
//        if (packageName == null) {
//            if (!toPathString().endsWith(".java")) {
//                throw new ClientException("Filename does not end with '.java'.");
//            }
//            File file = toFile();
//            if (file.isDirectory()) {
//                throw new ClientException("Found directory where file name was expected: " + toPathString());
//            }
//            CompilationUnit cu = Parser.parse(toFile()); // throws ClientException if parse fails
//            Optional<PackageDeclaration> pd = cu.getPackageDeclaration();
//            packageName = pd.isPresent() ? pd.get().getName().toString() : ""; // default package
//        }
//        return packageName;
//    }
//
//    /**
//     * Gets the name of the top-level class (or interface) of the target Java file
//     * from the file name.
//     *
//     * @return the class name
//     * @throws ClientException if the target is not a Java file
//     */
//    public String toClassName() {
//        if (className == null) {
//            File file = toFile();
//            if (file.isDirectory()) {
//                throw new ClientException("Found directory where file name was expected: " + toPathString());
//            }
//            String fileName = file.getName();
//            if (fileName.endsWith(".java")) {
//                className = fileName.substring(0, fileName.indexOf(".java"));
//            } else {
//                throw new ClientException("Specified file is not java source code: " + fileName);
//            }
//        }
//        return className;
//    }
//
//    public String toQualifiedName() {
//        return toPackageName() + "." + toClassName();
//    }

    /**
     * Gets this target's directory. This is the target itself if it is a
     * directory; otherwise, it is the parent directory of the file.
     *
     * @return the directory
     */
    public Path toDirectory() {
        File file = toFile();
        if (file.isDirectory()) {
            return file.toPath();
        } else {
            return file.getParentFile().toPath();
        }
    }

//    /**
//     * Parses this target, if it is a file.
//     *
//     * @param autograder autograder (for language level)
//     * @return the parsed file
//     * @throws ClientException if this target is a directory
//     */
//    public CompilationUnit toCompilationUnit(Autograder autograder) {
//        if (toFile().isDirectory()) {
//            throw new ClientException("Cannot parse directory " + toPathString());
//        }
//        return Parser.parse(autograder.getJavaLevel(), toFile());
//    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Target other) {
            return this.toPath().equals(other.toPath()) &&
                    this.toFile().equals(other.toFile()) &&
                    this.toDirectory().equals(other.toDirectory());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

}
