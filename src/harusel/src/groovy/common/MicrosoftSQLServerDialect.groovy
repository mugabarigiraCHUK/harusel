package common

import java.sql.Types
import org.hibernate.dialect.SQLServerDialect

public class MicrosoftSQLServerDialect extends SQLServerDialect {

    def MicrosoftSQLServerDialect() {
        super()
        registerColumnType(Types.VARCHAR, "NVARCHAR(MAX)")
        registerColumnType(Types.LONGVARCHAR, "NVARCHAR(MAX)")
        registerColumnType(Types.LONGNVARCHAR, "NVARCHAR(MAX)")
        registerColumnType(Types.NCLOB, "NVARCHAR(MAX)")
    }

}
