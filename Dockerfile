FROM openjdk:17-oracle

ADD build/libs/student-consulting-0.0.1-SNAPSHOT.jar student-consulting.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "student-consulting.jar"]