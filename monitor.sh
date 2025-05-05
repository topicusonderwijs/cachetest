#!/bin/bash

instance=${1:-wildfly1}

watch "docker compose exec $instance wildfly/bin/jboss-cli.sh -c --command='/deployment=cachetest-1.0.war/subsystem=jpa/hibernate-persistence-unit=cachetest-1.0.war#primary/:read-resource(include-runtime=true)'"
