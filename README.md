# Foodcalc
Web application to simplify Food carrying plan composition, modification and printing different reports to use during your trekking.

##Environment
 * Java 8
 * Maven 3.5+
 * PostgreSQL 9+
 * Node JS 8.11.2
 * NPM 5.6.0
 
 PostgreSQL installation tips - https://help.statlook.com/knowledge-base/problem-running-post-install-step-postgresql-installation

##Deployment steps
 * create **foodcalc** DB in PostgreSQL manually (set owner user to postgres);
 * run _PostgreDB.sql_ on **foodcalc** DB to initialize DB structure;
 * run _testData.sql_ on **foodcalc** DB to insert initial data set;
 * go to project root directory and build in with maven - _mvn clean install_
 * go to web-tier directory and run project with maven - _mvn spring-boot:run_
 
 ##Development tips
 * run dev Node JS server with hot reload at localhost:8080 - _npm run dev_


