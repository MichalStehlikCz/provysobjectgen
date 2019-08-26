FROM jboss/wildfly
ADD ./target/ProvysObjectGen.war /opt/jboss/wildfly/standalone/deployments/
ENV PROVYSDB_USER KER
ENV PROVYSDB_PWD ker
ENV PROVYSDB_MINPOOLSIZE 1
ENV PROVYSDB_MAXPOOLSIZE 3
