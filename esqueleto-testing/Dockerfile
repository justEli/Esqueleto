FROM        container-registry.oracle.com/graalvm/jdk:25

WORKDIR     /app
COPY        target/esqueleto-testing.jar .

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-jar", "esqueleto-testing.jar"]
