# cachetest

Instructions to run this test
 - Run `mvn package`
 - Download and unpack WildFly 11.0.0.CR1, use standalone-ha.xml as base for the configuration
 - Add a datasource, like:
 ```
<datasource jta="true" jndi-name="java:/jdbc/cachetest" pool-name="cachetest" enabled="true" use-ccm="true">
   <connection-url>jdbc:postgresql://localhost:5432/cachetest</connection-url>
   <driver>postgresql</driver>
   <security>
      <user-name>username</user-name>
      <password>password</password>
   </security>
</datasource>
```
 - Change the entity-cache to:
```
<invalidation-cache name="entity">
   <transaction mode="NONE"/>
   <eviction strategy="LRU" max-entries="10000"/>
   <expiration max-idle="100000"/>
</invalidation-cache>
```
 - Start 2 instances of WildFly, one at port-offset 1
 - Deploy target/cachetest-1.0.war in both instances
 - Open `http://localhost:8080/cachetest-1.0` and `http://localhost:8081/cachetest-1.0`, make sure you use different browsers (one in Chrome, one in Firefox), otherwise they will keep overwritting the session-cookie.
