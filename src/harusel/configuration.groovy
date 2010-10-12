////////////////////////////////////////////////////////////////////////////////
// HR-TOOL CONFIGURATION FILE.                                                //
//                                                                            //
// This is HR-Tool configuration file. Edit the properties below to configure //
// the application in compliance with your requirements.                      //
////////////////////////////////////////////////////////////////////////////////

println "Reading configuration file for harusel."

email {
    host = "10.0.0.25"
    port = 25
    from = "harusel@office.com"
}

log {
//    file = "${common.HRToolConfig.HRTOOL_CONF_DIR}/hr-tool.log"
    file = 'harusel.log'
}

// PRODUCTION CONFIG
//database {
//    url = 'jdbc:sqlserver://msdb2:1433;database=hrtool'
//    driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
//    database = 'hrtool'
//    username = 'hr'
//    password = 'djrw84dd'
//    dialect = 'common.MicrosoftSQLServerDialect'
//    longvarbinary = "VARBINARY(MAX)"
//}
ldap {
    url = "ldap://10.0.0.169"
    base = "cn=Users,dc=office,dc=local"
    userDn = "cn=testHR,cn=Users,dc=office,dc=local"
    password = "testhr1"
}

// TEST CONFIG

database {
    url = 'jdbc:mysql://localhost/harusel?useUnicode=true&characterEncoding=UTF-8'
    driver = 'com.mysql.jdbc.Driver'
    database = 'harusel'
    username = 'root'
    password = ''
    dialect = 'org.hibernate.dialect.MySQL5Dialect'
    longvarbinary = "BLOB"
}
//
//ldap {
//    url = "ldap://10.0.0.151"
//    // IT's case-sensitive property for Roles distinguishing
//    base = "CN=Users,DC=dctest"
//    userDn = "cn=testhr1,cn=Users,dc=dctest"
//    password = "testhr1"
//}



