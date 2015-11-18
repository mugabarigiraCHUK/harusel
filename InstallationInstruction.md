# Configuration #

Before running application you should set up the following configuration parameters:
| **Parameter name** | **Parameter description** | **Example** |
|:-------------------|:--------------------------|:------------|
| **mail.host**      | Mail server host name     | example.com, 10.0.0.128 |
| **email.port**     | Mail server port (for SMTP 25 is default) | 25          |
| **email.from**     | The address will be used as sender address for all application messages| hrtool@example.com|
|                    |                           |             |
| **database.url**   | JDBC url to database      |jdbc:sqlserver://msdb:1433;database=hrtool |
| **database.driver** |JDBC driver class name     |com.microsoft.sqlserver.jdbc.SQLServerDriver |
| **database.username** |Database user name         |             |
| **database.password** |Database password          |             |
| **database.dialect** |Hibernate dialect class name |common.MicrosoftSQLServerDialect|
| **database.longvarbinary** |The explicit SQL data type of column used for big values like Person documents (Resume, Questionnaire, ...)  | VARBINARY(MAX)|
|                    |                           |             |
| **ldap.url**       |URL to the LDAP server     | ldap://10.0.0.12 |
| **ldap.base**      |The folder with Person information in LDAP | cn=Users,dc=office,dc=local |
| **ldap.userDn**    |The LDAP system user       |             |
| **ldap.password**  | The LDAP system user's password |             |

These parameters should be placed in groovy file `configuration.groovy`. The example file could be find in `src/HRTool ` directory. The application looks for this file in the path specified in environment variable ` hrtool.conf.dir `. For tomcat you can specify this path in file `setenv.sh` (in Linux) or `setenv.bat` (in Windows). For example setenv.sh file: <br>
<pre><code>#!/bin/sh<br>
export JAVA_OPTS="-Dhrtool.conf.dir=/opt/hrtool/etc"<br>
</code></pre>

<h1>Compilation</h1>

The application is based on Grails framework. To build the program the Grails 1.2.2 should be installed (see <a href='http://www.grails.org/Installation'>http://www.grails.org/Installation</a> for instructions). <br />
Run the following command in the projects home (<code>src/HRTool</code> directory) to create WAR archive:<br>
<pre><code> $ grails war<br>
</code></pre>

After the script finished the WAR file is created:<br>
<pre><code>./target/HRTool-0.1.war<br>
</code></pre>


<h1>Installation</h1>

The application requires installed JDK version 1.5+. It can be downloaded from:<br>
<blockquote><a href='http://java.sun.com/javase/downloads/widget/jdk6.jsp'>http://java.sun.com/javase/downloads/widget/jdk6.jsp</a>
The WAR file can be deployed on any Java Servlet container (e.g. Apache Tomcat <a href='http://tomcat.apache.org'>http://tomcat.apache.org</a>). See documentation of your Java Servlet container for detailed instruction.<br>
For Apache Tomcat you should copy WAR file in <code>$TOMCAT_HOME/webapps</code> directory. Here <code>$TOMCAT_HOME</code> is the Tomcat installation directory.</blockquote>

After running Tomcat the application can be seen on the page:<br>
<blockquote><a href='http://localhost:8080/HRTool-0.1/'>http://localhost:8080/HRTool-0.1/</a>