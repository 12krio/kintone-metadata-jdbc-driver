package com.krio.kintone.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class KintoneResultSetMetaData implements ResultSetMetaData {

    public String getCatalogName(int column) throws SQLException {
        return "";
    }
    
    //
    // Not implemented below here
    //
    
    public String getColumnClassName(int column) throws SQLException {

        return null;
    }

    public int getColumnCount() throws SQLException {

        return 0;
    }

    public int getColumnDisplaySize(int column) throws SQLException {

        return 0;
    }

    public String getColumnLabel(int column) throws SQLException {

        return null;
    }

    public String getColumnName(int column) throws SQLException {

        return null;
    }

    public int getColumnType(int column) throws SQLException {

        return 0;
    }

    public String getColumnTypeName(int column) throws SQLException {

        return null;
    }

    public int getPrecision(int column) throws SQLException {

        return 0;
    }

    public int getScale(int column) throws SQLException {

        return 0;
    }

    public String getSchemaName(int column) throws SQLException {

        return null;
    }

    public String getTableName(int column) throws SQLException {

        return null;
    }

    public boolean isAutoIncrement(int column) throws SQLException {

        return false;
    }

    public boolean isCaseSensitive(int column) throws SQLException {

        return false;
    }

    public boolean isCurrency(int column) throws SQLException {

        return false;
    }

    public boolean isDefinitelyWritable(int column) throws SQLException {

        return false;
    }

    public int isNullable(int column) throws SQLException {

        return 0;
    }

    public boolean isReadOnly(int column) throws SQLException {

        return false;
    }

    public boolean isSearchable(int column) throws SQLException {

        return false;
    }

    public boolean isSigned(int column) throws SQLException {

        return false;
    }

    public boolean isWritable(int column) throws SQLException {

        return false;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {

        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {

        return false;
    }
}
