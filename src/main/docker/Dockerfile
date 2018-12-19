FROM docker.topicusonderwijs.nl/wildfly/wildfly:15.0.0.Final-jdk11

USER root
RUN apt update && apt install net-tools

USER jboss


RUN mkdir -p /opt/jboss/wildfly/modules/org/postgresql/main/
ADD module.xml /opt/jboss/wildfly/modules/org/postgresql/main/
ADD postgresql-42.2.5-jdbc42.jar /opt/jboss/wildfly/modules/org/postgresql/main/
ADD standalone.xml /opt/jboss/wildfly/standalone/configuration/
ADD maven/*.war /opt/jboss/wildfly/standalone/deployments/

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-Dfile.encoding=UTF-8", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]