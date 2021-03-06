package com.krio.kintone.jdbc;

public class Versions {

    //Kintone MetaData Driver Verison
    public static final int DRIVER_MAJOR = 2;
    public static final int DRIVER_MINOR = 1;
    public static final String DRIVER_VERSION = DRIVER_MAJOR + "." + DRIVER_MINOR;

    //Driver Verison KintoneのVerison
    public static final int DATABASE_PRODUCT_MAJOR = 1;
    public static final int DATABASE_PRODUCT_MINOR = 0;
    public static final String DATABASE_PRODUCT_VERSION = String.valueOf(DATABASE_PRODUCT_MAJOR);
    
    // Although Java 1.6 methods have been added, currently compiled to Java 1.5 which is JDBC 3.0
    // JDBCのVersion
    public static final int JDBC_MAJOR = 3;
    public static final int JDBC_MINOR = 0;
}
