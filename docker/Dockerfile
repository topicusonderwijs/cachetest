FROM quay.io/wildfly/wildfly:35.0.1.Final-jdk21

RUN mkdir -p /opt/jboss/wildfly/modules/org/postgresql/main

COPY --chown=jboss module.xml /opt/jboss/wildfly/modules/org/postgresql/main/

ADD --chown=jboss https://jdbc.postgresql.org/download/postgresql-42.7.5.jar /opt/jboss/wildfly/modules/org/postgresql/main/

COPY --chown=jboss standalone-ha.xml /opt/jboss/wildfly/standalone/configuration/standalone-ha.xml

RUN $JBOSS_HOME/bin/add-user.sh -u admin -p admin

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-server-config=standalone-ha.xml", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]