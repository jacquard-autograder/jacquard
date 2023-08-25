#!/usr/bin/env python3

import configparser
import json
import os
import platform
import shutil
import subprocess
import sys

# Configuration file
CONFIG_FILE_NAME = "config.ini"
CONFIG_PATH_LOCAL_TEMPLATE = "../%s/" + CONFIG_FILE_NAME
CONFIG_SUBMISSION_SECTION_NAME = "submission"
CONFIG_CROSSTESTS_SECTION_NAME = "crosstests"
CONFIG_SECTIONS = [CONFIG_SUBMISSION_SECTION_NAME, CONFIG_CROSSTESTS_SECTION_NAME]
CONFIG_PACKAGE_KEY = "package"
CONFIG_FILES_KEY = "files"
CONFIG_SUBMISSION_KEYS = [CONFIG_PACKAGE_KEY, CONFIG_FILES_KEY]
CONFIG_TESTS_KEY = "tests"
CONFIG_PACKAGES_KEY = "packages"

# All directories are relative. On the server, they are relative to /autograder.
SUBMISSION_SUBDIR = "submission" + os.sep
GRADESCOPE_RESULTS_SUBDIR = "results" + os.sep
GRADESCOPE_RESULTS_PATH = GRADESCOPE_RESULTS_SUBDIR + "results.json"
SOURCE_SUBDIR = os.path.join("src", "main", "java") + os.sep
WORKING_SUBDIR = "working" + os.sep
WORKING_JAVA_SUBDIR = WORKING_SUBDIR + SOURCE_SUBDIR + os.sep
GRADLEW_WINDOWS_CMD = "gradlew.bat"
GRADLEW_UNIX_CMD = "./gradlew"
# This includes directories, which must end with a separator.
FILES_TO_COPY = ["build.gradle", "gradle" + os.sep, "gradlew", "gradlew.bat",
                 "src" + os.sep, "lib" + os.sep, "config" + os.sep]


def is_windows():
    """Checks whether the underlying OS is Windows."""
    return platform.system() == "Windows"


def is_local():
    """Check whether the file is running locally or on Gradescope."""
    return not os.getcwd().startswith("/autograder")


def init(config):
    """Initialize the environment before compilation can be done."""
    if not is_local():
        os.makedirs(GRADESCOPE_RESULTS_SUBDIR, exist_ok=True)

    create_working_dir()
    copy_source_files()
    copy_req_files(config)
    repackage(config)


def create_working_dir():
    """Create a fresh working directory for compilation/execution."""
    if os.path.isdir(WORKING_SUBDIR):
        shutil.rmtree(WORKING_SUBDIR, ignore_errors=False)
    os.mkdir(WORKING_SUBDIR)


def copy_source_files():
    """Copy all source code and gradle files to working directory."""
    for file in FILES_TO_COPY:
        if file.endswith(os.sep):
            if os.path.isdir(file):
                shutil.copytree(file, WORKING_SUBDIR + file)
        else:
            shutil.copy(file, WORKING_SUBDIR)


def package_to_path(package: str):
    """Convert a package name into a relative path."""
    return package.replace(".", os.sep) + os.sep


def ensure_file_in_package(file_path: str, package: str):
    """Ensures that a file contains the expected package statement.

    :raise Exception: if the package student is not found.
    """
    pkg_stmt = f"package {package};"
    with open(file_path, 'r') as f:
        for line in f:
            if line.lstrip().startswith(pkg_stmt):
                return
    raise Exception(
        f"File {file_path} does not contain the expected package declaration: {pkg_stmt}")


def copy_req_files(config):
    """Copy student-provided files into the appropriate server directory.

    :raise Exception: if a required file is not found

    """
    package, files = get_submission_info(config)
    path = package_to_path(package)
    dest_path = WORKING_JAVA_SUBDIR + path
    os.makedirs(dest_path, exist_ok=True)

    for file in files:
        file_path = SUBMISSION_SUBDIR + file
        if os.path.exists(file_path):
            if file_path.endswith(".java"):
                ensure_file_in_package(file_path, package)
            shutil.copy(file_path, dest_path)
        else:
            raise Exception(f"File {file_path} not found.")


def repackage(config):
    """Repackage test files for cross-testing"""

    # Only run if there is a crosstests section in config.
    if CONFIG_CROSSTESTS_SECTION_NAME not in config.sections():
        return

    # Get values from configuration files.
    old_package = config[CONFIG_SUBMISSION_SECTION_NAME][CONFIG_PACKAGE_KEY]
    section = config[CONFIG_CROSSTESTS_SECTION_NAME]
    tests = get_config_list(section, CONFIG_TESTS_KEY)
    packages = get_config_list(section, CONFIG_PACKAGES_KEY)

    # Repackage all the test files in all the packages.
    for filename in tests:
        source = os.path.join(WORKING_JAVA_SUBDIR, old_package, filename)
        for new_package in packages:
            target = os.path.join(WORKING_JAVA_SUBDIR, new_package, filename)

            with open(target, 'w') as target_file:
                target_file.write(f"package {new_package};\n\n")
                target_file.write(f"import {old_package}.*;\n")

                with open(source, 'r') as source_file:
                    for line in source_file:
                        if f"package {old_package}" not in line:
                            target_file.write(line)


def run():
    """Run the compiled project, outputting the results.

    Results are written to stdout if run locally, to a file if run on the server.

    :raise Exception: if the result of the run is not 0.
    """
    os.chdir(WORKING_SUBDIR)
    gradle_cmd = GRADLEW_WINDOWS_CMD if is_windows() else GRADLEW_UNIX_CMD
    result = subprocess.run(
        [gradle_cmd, "clean", "run", "--quiet"],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE)
    if result.returncode != 0:
        raise Exception("Runtime error: " + result.stderr.decode("UTF-8"))
    os.chdir("..")
    output(result.stdout.decode())


def output(s):
    """Output to stdout and to a file if run on the Gradescope server. """
    if is_local():
        print(s)
    else:
        print(s)
        with open(GRADESCOPE_RESULTS_PATH, "w") as text_file:
            text_file.write(s)


def output_error(e):
    """Output an error."""
    data = {"score": 0, "output": str(e)}
    output(json.dumps(data))


def read_config_file():
    """Read and validate the configuration file.

    :raise Exception: if the config file cannot be found or has invalid content
    """

    # Make sure config file has required section and keys.
    config = configparser.ConfigParser()
    path = CONFIG_PATH_LOCAL_TEMPLATE % sys.argv[1] if is_local() else CONFIG_FILE_NAME
    if not config.read(path):
        raise Exception(
            f"Unable to read configuration file {path}"
        )
    if CONFIG_SUBMISSION_SECTION_NAME not in config.sections():
        raise Exception(
            f"Did not find section '{CONFIG_SUBMISSION_SECTION_NAME}' in {path}"
        )
    if len(config.sections()) > len(CONFIG_SECTIONS):
        sections = config.sections()
        sections.remove(CONFIG_SUBMISSION_SECTION_NAME)
        raise Exception(
            f"Unexpected section(s) in {path}: {sections}"
        )
    section = config[CONFIG_SUBMISSION_SECTION_NAME]
    for key in CONFIG_SUBMISSION_KEYS:
        if key not in section:
            raise Exception(
                f"Did not find key '{key}' in section '{CONFIG_SUBMISSION_SECTION_NAME}' of {CONFIG_PATH}"
            )
    if len(section) > len(CONFIG_SUBMISSION_KEYS):
        keys = list(section.keys())
        for key in CONFIG_SUBMISSION_KEYS:
            keys.remove(key)
        raise Exception(
            f"Unexpected key(s) in {path}: {keys}"
        )

    return config


def get_config_list(section, key: str) -> list[str]:
    """Get a list from the configuration file.

    :raise Exception: if the key does not appear or its value is unparseable
    """
    if key not in section:
        raise Exception(
            f"Could not find key {key} in '{CONFIG_FILE_NAME}'")
    list = section[key]
    if len(list) < 2 or list[0] != '[' or list[-1] != ']':
        raise Exception(
            f"Could not parse {key} value '{list}' in  '{CONFIG_FILE_NAME}'")
    return [item.strip() for item in list[1:-1].split(',')]


def get_submission_info(config) -> (str, list[str]):
    """Get the name of the package and files of the submission.

    :raise Exception: if the file list cannot be parsed
    """
    section = config[CONFIG_SUBMISSION_SECTION_NAME]
    package = section[CONFIG_PACKAGE_KEY]
    files_list = get_config_list(section, CONFIG_FILES_KEY)
    return (package, files_list)


def main():
    if is_local() and len(sys.argv) != 2:
        print("Usage: run_autograder.py <projectdir>")
        sys.exit(1)
    try:
        config = read_config_file()
        init(config)
        run()
    except Exception as e:
        output_error(e)


if __name__ == "__main__":
    main()
