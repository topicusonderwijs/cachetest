docker compose up -d --build
mvn package
docker compose cp target/cachetest-1.0.war wildfly1:/opt/jboss/wildfly/standalone/deployments
docker compose cp target/cachetest-1.0.war wildfly2:/opt/jboss/wildfly/standalone/deployments