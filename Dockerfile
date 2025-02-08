FROM openjdk:21
RUN mkdir -p /opt/target
ADD build/libs/percentage-service-0.0.1-SNAPSHOT.jar /opt/target/
WORKDIR /opt/target
CMD ["java", "-jar", "percentage-service-0.0.1-SNAPSHOT.jar"]