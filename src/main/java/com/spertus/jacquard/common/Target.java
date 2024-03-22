package com.spertus.jacquard.common;

import com.spertus.jacquard.exceptions.ClientException;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The target of a {@link Grader}, which must be a file.
 */
public final class Target {
    private static final String SUBMISSION_PATH_TEMPLATE =
            // arguments are package and class
            "src/main/java/%s/%s.java";

    // This works on Windows, even though it's not the normal separator.
    private static final char SEP = '/';

    private final Path path;

    private Target(final Path path) {
        if (path.toFile().isDirectory()) {
            throw new ClientException("The path argument must be to a file, not a directory.");
        }
        this.path = path;
    }

    /**
     * Creates a target from a path, which must be to a single file.
     *
     * @param path the path
     * @return the target
     * @throws ClientException if the path is to a directory rather than a file
     */
    public static Target fromPath(final Path path) {
        return new Target(path);
    }

    /**
     * Creates a target from a relative path string to a file. It does not matter whether
     * forward slashes or backslashes are used as separators.
     *
     * @param s a relative path string to a file
     * @return the target
     * @throws ClientException if the path is to a directory rather than a file
     */
    public static Target fromPathString(final String s) {
        // https://stackoverflow.com/a/40163941/631051
        final Path path = FileSystems.getDefault().getPath(s).normalize().toAbsolutePath();
        return fromPath(path);
    }

    /**
     * Creates a target from a class that the student is responsible for
     * submitting. The class's package must not be empty.
     *
     * @param targetClass the class
     * @return the target
     * @throws ClientException if the package of the class is invalid
     */
    public static Target fromClass(Class<?> targetClass) {
        String pkgName = targetClass.getPackageName();
        if (pkgName.isEmpty()) {
            throw new ClientException("Package name may not be empty.");
        }
        String pathString = String.format(SUBMISSION_PATH_TEMPLATE,
                pkgName.replace('.', SEP),
                targetClass.getSimpleName());
        return fromPathString(pathString);
    }

    /**
     * Creates targets from a directory string and list of files in the directory.
     *
     * @param dir   a relative directory string (ending with <code>/</code> or
     *              <code>\</code>)
     * @param files the names of the files in the directory
     * @return the targets
     * @throws ClientException if dir does not end with a path separator
     */
    public static List<Target> fromPathStrings(final String dir, final String... files) {
        if (!dir.endsWith("/") && !dir.endsWith("\\")) {
            throw new ClientException("dir must end with a path separator (/ or \\)");
        }
        return Arrays.stream(files)
                .map(file -> fromPathString(dir + file))
                .collect(Collectors.toList());
    }

    /**
     * Creates targets from all the files in the specified directory.
     *
     * @param dir a relative directory string
     * @return the targets
     * @throws ClientException if dir is not a directory or is not accessible
     */
    public static List<Target> fromDirectory(final String dir) {
        Path path = Paths.get(dir);
        if (!path.toFile().isDirectory()) {
            throw new ClientException("Argument to fromDirectory() must be a directory.");
        }
        try (Stream<Path> paths = Files.walk(path)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(file -> fromPath(file.toAbsolutePath()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new ClientException("Unable to access directory " + dir);
        }
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

    /**
     * Gets this target's directory.
     *
     * @return the directory
     */
    public Path toDirectory() {
        final File file = toFile();
        if (file.isDirectory()) {
            throw new RuntimeException("It should not be possible to have a Target that is a directory.");
        } else {
            return file.getParentFile().toPath();
        }
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof Target) {
            Target target = (Target) other;
            return this.path.equals(target.path);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
