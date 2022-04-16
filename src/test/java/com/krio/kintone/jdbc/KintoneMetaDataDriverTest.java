package com.krio.kintone.jdbc;

import org.junit.jupiter.api.DisplayName;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@DisplayName("Driver Test")
class KintoneMetaDataDriverTest {

    @org.junit.jupiter.api.Test
    void acceptsURL() {
    }

    @org.junit.jupiter.api.Test
    void connect() throws SQLException {
        //TODO SET URL/ID/PASS
        String baseUrl = "jdbc:kr:kintone";
        String url = ""; //https://xxxxx.cybozu.com
        String username = "";
        String password = "";

        KintoneMetaDataDriver dr = new KintoneMetaDataDriver();
        Properties prop = new Properties();
        prop.setProperty("url",url);
        prop.setProperty("user",username);
        prop.setProperty("password",password);
        Connection conn = dr.connect(baseUrl,prop);
        conn.getMetaData();
    }

    @org.junit.jupiter.api.Test
    void getMajorVersion() {
    }

    @org.junit.jupiter.api.Test
    void getMinorVersion() {
    }

    @org.junit.jupiter.api.Test
    void getPropertyInfo() {
    }

    @org.junit.jupiter.api.Test
    void jdbcCompliant() {
    }

    @org.junit.jupiter.api.Test
    void getParentLogger() {
    }
}