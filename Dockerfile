FROM openjdk

ARG JAR_FILE=/target/spring-kafka-0.0.1-SNAAPSHOT.jar

COPY ${JAR_FILE} kafka.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/kafka.jar"]