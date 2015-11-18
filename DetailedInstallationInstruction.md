# Detailed build and installation instruction #

### Prerequisites ###
  * Install mercurial DVCS client from [official mercurial site](http://mercurial.selenic.com/downloads/)
  * Install [Oracle Java Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
  * Set `JAVA_HOME` environment variable to JDK installation home. (e.g. `C:\Program Files\Java\jdk1.6.0_22`)
  * Install [Grails](http://grails.org/Download)
  * Set `GRAILS_HOME` environment variable, pointing to Grails installation directory.
  * Add `%GRAILS_HOME%/bin` to variable `PATH`
  * Install and configure database server.
    1. [Microsoft SQL Server Express 2005](http://www.microsoft.com/downloads/en/details.aspx?FamilyID=b448b0d0-ee79-48f6-b50a-7c4f028c2e3d)
    1. Either [Microsoft SQL Server Express 2008](http://www.microsoft.com/downloads/en/details.aspx?FamilyID=e08766ce-fc9d-448f-9e98-fe84ad61f135)
    1. Either [MySQL](http://www.mysql.com/downloads/mysql/)

Note: When installing SQL Server, mixed mode of authentication should be chosen. After SQL Server installation, authentication mode can be changed in Server  properties/Security/Management Studio. It’s required to switch TCP/IP protocol at Configuration Manager, restart service of SQL server. In TCP/IP properties port, connection with SQL server will go through, should be written for it not to be changed  during the next restart.

  * Install [Apache Tomcat](http://tomcat.apache.org/download-60.cgi).

### Getting sources ###
  * After Mercurial is installed get the source:
```
hg clone https://harusel.googlecode.com/hg/ harusel
```
Directory where sources are downloaded is reffered as `$HARUSEL_HOME` below in the document.

### Configuration ###
  * In Apache Tomcat Meneger select `Settings/Java/->Java Options` and add the following property: `-Dhrtool.conf.dir=$HARUSEL_HOME\src\harusel`.
  * At  `$HARUSEL_HOME\src\harusel` in `configuration.groovy`  file it’s required to set the following parameters:

| **Parameter name** | **Parameter description** | **Example** |
|:-------------------|:--------------------------|:------------|
| **mail.host**      | Mail server host name     | example.com, 10.0.0.128 |
| **email.port**     | Mail server port (for SMTP 25 is default) | 25          |
| **email.from**     | The address will be used as sender address for all application messages| hrtool@example.com|
| **database.url**   | JDBC url to database      |jdbc:sqlserver://msdb:1433;database=hrtool |
| **database.driver** |JDBC driver class name     |com.microsoft.sqlserver.jdbc.SQLServerDriver |
| **database.username** |Database user name         |             |
| **database.password** |Database password          |             |
| **database.dialect** |Hibernate dialect class name |common.MicrosoftSQLServerDialect|
| **database.longvarbinary** |The explicit SQL data type of column used for big values like Person documents (Resume, Questionnaire, ...)  | VARBINARY(MAX)|
| **ldap.url**       |URL to the LDAP server     | ldap://10.0.0.12 |
| **ldap.base**      |The folder with Person information in LDAP | cn=Users,dc=office,dc=local |
| **ldap.userDn**    |The LDAP system user       |             |
| **ldap.password**  | The LDAP system user's password |             |


### Building and running HaRusel ###

  * At `$HARUSEL_HOME\src\harusel` execute
```
  $ grails war
```

After compilation file `harusel.war` from the folder `$HARUSEL_HOME\src\harusel\target` should be copied to `$TOMCAT_HOME\webapps`.
On Linux:
```
  cp $HARUSEL_HOME\src\harusel\target\harusel.war $TOMCAT_HOME\webapps
```
On Windows:
```
  copy %HARUSEL_HOME%\src\harusel\target\harusel.war %TOMCAT_HOME%\webapps
```

  * Application will be available at `http://localhost:8080/harusel/`.