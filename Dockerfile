FROM sonarqube:5.6.6-alpine

COPY sonar-puppet-plugin/build/libs/* /opt/sonarqube/extensions/plugins/
