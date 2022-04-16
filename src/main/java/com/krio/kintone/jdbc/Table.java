package com.krio.kintone.jdbc;

import java.util.ArrayList;
import java.util.List;

public class Table {

    private String name;
    private String comments;
    private List<Column> columns;
    private List<Column> primaryKeys = new ArrayList<>();

    public List<Column> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(Column column ) {
        this.primaryKeys.add(column);
    }

    public Table(String name, String comments, List<Column> columns) {
        this.name = name;
        this.comments = comments;
        this.columns = columns; 
        for (Column c : columns) {
            c.setTable(this);
        }
    }
    
    public String getName() {
        return name;
    }

    public String getComments() {
        return comments;
    }

    public List<Column> getColumns() {
        return columns;
    }
}
