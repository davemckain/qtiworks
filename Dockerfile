FROM maven:3.8.4-openjdk-8 as builder
RUN apt-get update && apt-get install git -y && git clone https://github.com/davemckain/qtiworks.git && cd qtiworks && mvn install

FROM tomcat:9-jre8-openjdk
COPY --from=builder qtiworks/qtiworks-engine/target/qtiworks-engine-*.war ./webapps/qtiworks.war
COPY --from=builder qtiworks/qtiworks-engine/target/ ./lib
COPY --from=builder qtiworks/qtiworks-engine-manager/target/qtiworks-engine-manager-*.jar qtiworks-engine-manager.jar
RUN apt-get update && apt-get install wget -y && wget --progress=dot:giga https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java_8.0.27-1debian11_all.deb && dpkg -i mysql-connector-java_8.0.27-1debian11_all.deb && cp /usr/share/java/mysql-connector-java-8.0.27.jar lib/
WORKDIR /usr/local/tomcat
COPY qtiworks-deployment.properties .
COPY qtiworks.xml conf/Catalina/localhost/
#RUN java -jar qtiworks-engine-manager.jar bootstrap
