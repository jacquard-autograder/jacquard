cd /autograder

# This moves files from /autograder/source to /autograder.
mv source/* .
chmod a+x run_autograder run_autograder.py gradlew

# This installs checkstyle, which is often used by Gradescope.
mkdir -p lib
wget -nc -P lib https://github.com/checkstyle/checkstyle/releases/download/checkstyle-10.12.1/checkstyle-10.12.1-all.jar

# This fixes carriage returns in scripts.
dos2unix run_autograder run_autograder.py gradlew

# This runs Gradle once, to download any needed files.
./gradlew clean
