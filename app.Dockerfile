FROM tomcat:8.5

RUN rm -rf $CATALINA_HOME/webapps/*

ADD arc-web/target/*.war $CATALINA_HOME/webapps/ROOT.war