import common.HRToolConfig

dataSource {
    pooled = true
    properties {
        maxActive = 50
        maxIdle = 50
        minIdle = 5
        initialSize = 50
        minEvictableIdleTimeMillis = 60000
        timeBetweenEvictionRunsMillis = 60000
        maxWait = 10000
    }
}
hibernate {
//    format_sql = true
//    show_sql = true
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.provider_class = 'org.hibernate.cache.EhCacheProvider'
}
// environment specific settings
environments {
    development {
//        dataSource {
//            dbCreate = "update"
//            url = "jdbc:sqlserver://msdb2:1433;database=hrtool"
//            driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
//            username = "hr"
//            password = "djrw84dd"
//            dialect = common.MicrosoftSQLServerDialect
//        }
//        datatypes.longvarbinary = "VARBINARY(MAX)"
        dataSource {
            dbCreate = "update" // one of 'create', 'create-drop','update'
            url = "jdbc:mysql://localhost/hrtool?useUnicode=true&characterEncoding=UTF-8"
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = "org.hibernate.dialect.MySQL5Dialect"
            username = "hrtool"
            password = "hrtool"
        }
        datatypes.longvarbinary = "BLOB"
    }

    // MySQL Environment.
    // start grails with -Dgrails.env=mysql
    mysql {
        dataSource {
            dbCreate = "update" // one of 'create', 'create-drop','update'
            url = "jdbc:mysql://127.0.0.1/hrtool?useUnicode=true&characterEncoding=UTF-8"
            driverClassName = "com.mysql.jdbc.Driver"
            username = "root"
            password = "root"
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect
        }
        datatypes.longvarbinary = "BLOB"
    }

    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:hsqldb:mem:test-db"
            driverClassName = "org.hsqldb.jdbcDriver"
            username = "sa"
            password = ""
            dialect = org.hibernate.dialect.HSQLDialect
        }
        datatypes.longvarbinary = "LONGVARBINARY"
    }

    production {
        dataSource {
            dbCreate = "update"
            url = HRToolConfig.config.database.url
            driverClassName = HRToolConfig.config.database.driver
            username = HRToolConfig.config.database.username
            password = HRToolConfig.config.database.password
            dialect = HRToolConfig.config.database.dialect
        }
        datatypes.longvarbinary = HRToolConfig.config.database.longvarbinary
    }
}
