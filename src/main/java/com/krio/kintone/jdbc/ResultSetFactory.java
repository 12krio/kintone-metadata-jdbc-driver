package com.krio.kintone.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
//TODO fix for kintone
/**
 * Holds a force.com org's objects (tables) and fields (columns)
 * and translates them into ResultSet objects that match the patterns
 * specified in the DatabaseMetaData Javadoc.
 */
public class ResultSetFactory {
    
    private static class TypeInfo {
        public TypeInfo(
                String typeName,
                int sqlDataType,
                int precision,
                int minScale,
                int maxScale,
                int radix) {
            this.typeName = typeName;
            this.sqlDataType = sqlDataType;
            this.precision = precision;
            this.minScale = minScale;
            this.maxScale = maxScale;
            this.radix = radix;         
        }
        String typeName;
        int sqlDataType;
        int precision;
        int minScale;
        int maxScale;
        int radix;
    }

    //TODO kintoneの項目設定変更
    /**
     *
     * https://jp.cybozu.help/k/ja/user/app_settings/form/form_parts/field_restriction.html
     * https://developer.cybozu.io/hc/ja/articles/202166330
     */
    private static TypeInfo TYPE_INFO_DATA[] = {
        new TypeInfo("RECORD_NUMBER", Types.INTEGER, 0x7fffffff, 0,0,0),
        new TypeInfo(" CREATOR", Types.OTHER, 0, 0,0, 0),       //作成者
        new TypeInfo(" CREATED_TIME", Types.TIME, 0, 0,0, 0),  //作成日時
        new TypeInfo(" MODIFIER", Types.OTHER, 0, 0,0, 0),      //更新者
        new TypeInfo(" UPDATED_TIME", Types.TIME, 0, 0,0, 0), //更新日時 "2012-01-11T11:30:00Z"
        new TypeInfo(" SINGLE_LINE_TEXT", Types.VARCHAR, 0, 0,0, 0),  // 文字列（1行）or Lookup
        new TypeInfo(" NUMBER", Types.BIGINT, 0, 0,0, 0),            // 数値 or Lookup
        new TypeInfo(" MULTI_LINE_TEXT", Types.LONGVARCHAR, 0, 0,0, 0),  //文字列（複数行）
        new TypeInfo(" RICH_TEXT", Types.LONGVARCHAR, 0, 0,0, 0), //リッチエディター
        new TypeInfo(" CALC", Types.OTHER, 0, 0,0, 0), //計算 ※3
        new TypeInfo(" CHECK_BOX", Types.ARRAY, 0, 0,0, 0), //チェックボックス
        new TypeInfo(" RADIO_BUTTON", Types.ARRAY, 0, 0,0, 0), //ラジオボタン
        new TypeInfo(" MULTI_SELECT", Types.ARRAY, 0, 0,0, 0), //複数選択 ※1
        new TypeInfo(" DROP_DOWN", Types.ARRAY, 0, 0,0, 0), //ドロップダウン ※1
        new TypeInfo(" USER_SELECT", Types.ARRAY, 0, 0,0, 0), //ユーザー選択
        new TypeInfo(" ORGANIZATION_SELECT", Types.ARRAY, 0, 0,0, 0),  //組織選択
        new TypeInfo(" GROUP_SELECT", Types.ARRAY, 0, 0,0, 0), //グループ選択
        new TypeInfo(" DATE", Types.OTHER, 0, 0,0, 0), //日付 ※2
        new TypeInfo(" TIME", Types.TIME, 0, 0,0, 0), //時刻 ※2
        new TypeInfo(" DATETIME", Types.TIMESTAMP, 0, 0,0, 0), //日時 ※2
        new TypeInfo(" LINK", Types.VARCHAR, 0, 0,0, 0), //リンク
        new TypeInfo(" FILE", Types.BLOB, 0, 0,0, 0),//添付ファイル
        new TypeInfo(" SUBTABLE", Types.ARRAY, 0, 0,0, 0),//テーブル
        new TypeInfo(" REFERENCE_TABLE", Types.DATALINK, 0, 0,0, 0),//関連レコード一覧
        new TypeInfo(" CATEGORY", Types.ARRAY, 0, 0,0, 0),//カテゴリー ※3
        new TypeInfo(" STATUS", Types.VARCHAR, 0, 0,0, 0),//ステータス ※3
        new TypeInfo(" STATUS_ASSIGNEE", Types.VARCHAR, 0, 0,0, 0),//作業者 ※3
//            new TypeInfo("id", Types.VARCHAR, 0x7fffffff, 0,0,0),
//            new TypeInfo("masterrecord", Types.VARCHAR, 0x7fffffff, 0,0,0),
//            new TypeInfo("reference", Types.VARCHAR, 0x7fffffff, 0,0,0),
//            new TypeInfo("string", Types.VARCHAR, 0x7fffffff, 0,0,0),
//            new TypeInfo("encryptedstring", Types.VARCHAR, 0x7fffffff, 0,0,0),
//            new TypeInfo("email", Types.VARCHAR, 0x7fffffff, 0,0,0),
//            new TypeInfo("phone", Types.VARCHAR, 0x7fffffff, 0,0,0),
//            new TypeInfo("url", Types.VARCHAR, 0x7fffffff, 0,0,0),
//            new TypeInfo("textarea", Types.LONGVARCHAR, 0x7fffffff, 0,0,0),
//            new TypeInfo("base64", Types.LONGVARCHAR, 0x7fffffff, 0,0,0),
//            new TypeInfo("boolean", Types.BOOLEAN, 1, 0,0, 0),
//            new TypeInfo("_boolean", Types.BOOLEAN, 1, 0,0, 0),
//            new TypeInfo("byte", Types.VARBINARY, 10, 0,0, 10),
//            new TypeInfo("_byte", Types.VARBINARY, 10, 0,0, 10),
//            new TypeInfo("int", Types.INTEGER, 10, 0,0, 10),
//            new TypeInfo("_int", Types.INTEGER, 10, 0,0, 10),
//            new TypeInfo("decimal", Types.DECIMAL, 17, -324,306, 10),
//            new TypeInfo("double", Types.DOUBLE, 17, -324,306, 10),
//            new TypeInfo("double", Types.DOUBLE, 17, -324,306, 10),
//            new TypeInfo("percent", Types.DOUBLE, 17, -324,306, 10),
//            new TypeInfo("currency", Types.DOUBLE, 17, -324,306, 10),
//            new TypeInfo("date", Types.DATE, 10, 0,0, 0),
//            new TypeInfo("time", Types.TIME, 10, 0,0, 0),
//            new TypeInfo("datetime", Types.TIMESTAMP, 10, 0,0, 0),
//            new TypeInfo("picklist", Types.ARRAY, 0, 0,0, 0),
//            new TypeInfo("multipicklist", Types.ARRAY, 0, 0,0, 0),
//            new TypeInfo("combobox", Types.ARRAY, 0, 0,0, 0),
//            new TypeInfo("anyType", Types.OTHER, 0, 0,0, 0),
    };
    
    private List<Table> tables = new ArrayList<Table>();
    private int counter;

    public void addTable(Table table) {
        tables.add(table);
    }
    
    /**
     * Provide table (object) detail.
     */
    public ResultSet getTables() {
        List<ColumnMap<String, Object>> maps = new ArrayList<ColumnMap<String, Object>>();
        for (Table table : tables) {
        	ColumnMap<String, Object> map = new ColumnMap<String, Object>();
        	map.put("TABLE_CAT", null);
        	map.put("TABLE_SCHEM", null);
        	map.put("TABLE_NAME", table.getName());
        	map.put("TABLE_TYPE", "TABLE");        	                    
            map.put("REMARKS", table.getComments());
            map.put("TYPE_CAT", null);
            map.put("TYPE_SCHEM", null);
            map.put("TYPE_NAME", null);
            map.put("SELF_REFERENCING_COL_NAME", null);
            map.put("REF_GENERATION", null);
            maps.add(map);
        }
        return new KintoneResultSet(maps);
    }
    //TODO 項目の定義を動的に設定できるように変更するべき？一旦コメントに出力でOKか？
    public ResultSet getTypeInfo() {
        List<ColumnMap<String, Object>> maps = new ArrayList<ColumnMap<String, Object>>();
        for (TypeInfo typeInfo : TYPE_INFO_DATA) {
        	ColumnMap<String, Object> map = new ColumnMap<String, Object>();
            map.put("TYPE_NAME", typeInfo.typeName);
            map.put("DATA_TYPE", typeInfo.sqlDataType);
            map.put("PRECISION", typeInfo.precision);
            map.put("LITERAL_PREFIX", null);
            map.put("LITERAL_SUFFIX", null);
            map.put("CREATE_PARAMS", null);
            map.put("NULLABLE", 1);
            map.put("CASE_SENSITIVE", 0);
            map.put("SEARCHABLE", 3);
            map.put("UNSIGNED_ATTRIBUTE", false);
            map.put("FIXED_PREC_SCALE", false);
            map.put("AUTO_INCREMENT", false);
            map.put("LOCAL_TYPE_NAME", typeInfo.typeName);
            map.put("MINIMUM_SCALE", typeInfo.minScale);
            map.put("MAXIMUM_SCALE", typeInfo.maxScale);
            map.put("SQL_DATA_TYPE", typeInfo.sqlDataType);
            map.put("SQL_DATETIME_SUB", null);
            map.put("NUM_PREC_RADIX", typeInfo.radix);
            map.put("TYPE_SUB", 1);
            
            maps.add(map);
        }
        return new KintoneResultSet(maps);
    }
  
    public ResultSet getCatalogs() {
    	List<ColumnMap<String, Object>> maps = new ArrayList<ColumnMap<String, Object>>();    	
    	return new KintoneResultSet(maps);
    }
    
    public ResultSet getSchemas() {
    	List<ColumnMap<String, Object>> maps = new ArrayList<ColumnMap<String, Object>>();
    	ColumnMap<String, Object> row = new ColumnMap<String, Object>();
        //TABLE_SCHEM KINTONE
    	row.put("TABLE_SCHEM", "KINTONE");
    	row.put("TABLE_CATALOG", null);
    	row.put("IS_DEFAULT", true);
    	maps.add(row);
    	return new KintoneResultSet(maps);
    }
    
    /**
     * Provide column (field) detail.
     */
    public ResultSet getColumns(String tableName) {
        List<ColumnMap<String, Object>> maps = new ArrayList<ColumnMap<String, Object>>();
        for (Table table : tables) {
            if (table.getName().equals(tableName)) {
                int ordinal = 1;
                for (Column column : table.getColumns()) {
                	ColumnMap<String, Object> map = new ColumnMap<String, Object>();
                	TypeInfo typeInfo = lookupTypeInfo(column.getType());
                	map.put("TABLE_CAT", null);
                	map.put("TABLE_SCHEM", null);
                	map.put("TABLE_NAME", table.getName());
                    map.put("COLUMN_NAME", column.getName());
                    map.put("DATA_TYPE", typeInfo != null ? typeInfo.sqlDataType : Types.OTHER);
                    map.put("TYPE_NAME", column.getType());
                    map.put("COLUMN_SIZE", column.getLength());
                    map.put("BUFFER_LENGTH", 0);
                    map.put("DECIMAL_DIGITS", 0);
                    map.put("NUM_PREC_RADIX", typeInfo != null ? typeInfo.radix : 10);
                    map.put("NULLABLE", 0);
                    map.put("REMARKS", column.getComments());
                    map.put("COLUMN_DEF", null);
                    map.put("SQL_DATA_TYPE", null);
                    map.put("SQL_DATETIME_SUB", null);
                    map.put("CHAR_OCTET_LENGTH", 0);
                    map.put("ORDINAL_POSITION", ordinal++);
                    map.put("IS_NULLABLE", "");
                    map.put("SCOPE_CATLOG", null);
                    map.put("SCOPE_SCHEMA", null);
                    map.put("SCOPE_TABLE", null);
                    map.put("SOURCE_DATA_TYPE", column.getType());
                    
                    map.put("NULLABLE", column.isNillable() ? DatabaseMetaData.columnNullable : DatabaseMetaData.columnNoNulls);
                    
                    // The Auto column is obtained by SchemaSpy via ResultSetMetaData so awkward to support
                    
                    maps.add(map);
                }
            }
        }
        return new KintoneResultSet(maps);
    }
    
    private TypeInfo lookupTypeInfo(String forceTypeName) {
        for (TypeInfo entry : TYPE_INFO_DATA) {
            if (forceTypeName.equals(entry.typeName)) {
                return entry;
            }
        }
        return null;
    }
    
    /**
     * Provide table (object) relationship information.
     */
    public ResultSet getImportedKeys(String tableName) {
        
        List<ColumnMap<String, Object>> maps = new ArrayList<ColumnMap<String, Object>>();
        for (Table table : tables) {
            if (table.getName().equals(tableName)) {
                for (Column column : table.getColumns()) {
                    if (column.getReferencedTable() != null && column.getReferencedColumn() != null) {
                    	ColumnMap<String, Object> map = new ColumnMap<String, Object>();
                    	map.put("PKTABLE_CAT", null);
                    	map.put("PKTABLE_SCHEM", null);
                    	map.put("PKTABLE_NAME", column.getReferencedTable());
                        map.put("PKCOLUMN_NAME", column.getReferencedColumn());
                        map.put("FKTABLE_CAT", null);
                        map.put("FKTABLE_SCHEM", null);
                        map.put("FKTABLE_NAME", tableName);
                        map.put("FKCOLUMN_NAME", column.getName());
                        map.put("KEY_SEQ", counter);
                        map.put("UPDATE_RULE", 0);
                        map.put("DELETE_RULE", 0);
                        map.put("FK_NAME", "FakeFK" + counter);
                        map.put("PK_NAME", "FakePK" + counter);
                        map.put("DEFERRABILITY", 0);
                        counter++;
                        maps.add(map);
                    }
                }
            }
        }
        return new KintoneResultSet(maps);
    }
    
    /**
     * May not be needed.
     */
    public ResultSet getPrimaryKeys(String tableName) throws SQLException {
        
        List<ColumnMap<String, Object>> maps = new ArrayList<ColumnMap<String, Object>>();
        for (Table table : tables) {
            if (table.getName().equals(tableName)) {
                //TODO テーブルによってRECORD_NUBERのカラムを設定する。
                List<Column> list = table.getPrimaryKeys();
                String pkey_name =list.get(0).getName();
                System.out.println("pkey_name="+pkey_name);

                for (Column column : table.getColumns()) {
                    //if (column.getName().equalsIgnoreCase("Id")) {
                    //TODO テーブルによってRECORD_NUBERのカラムを設定する。
                    //if (column.getName().equalsIgnoreCase("レコード番号")) {
                    if (column.getName().equalsIgnoreCase(pkey_name)) {
                    	ColumnMap<String, Object> map = new ColumnMap<String, Object>();
                    	map.put("TABLE_CAT", null);
                    	map.put("TABLE_SCHEM", null);
                    	map.put("TABLE_NAME", table.getName());
                        map.put("COLUMN_NAME", "" + column.getName());
                        map.put("KEY_SEQ", 0);
                        map.put("PK_NAME", "FakePK" + counter);
                        maps.add(map);
                    }
                }
            }
        }
        return new KintoneResultSet(maps);
    }
    
    /**
     * Avoid the tables (objects) appearing in the "tables without indexes" anomalies list.
     */
    public ResultSet getIndexInfo(String tableName) throws SQLException {
        
        List<ColumnMap<String, Object>> maps = new ArrayList<ColumnMap<String, Object>>();
        for (Table table : tables) {
            if (table.getName().equals(tableName)) {
                //TODO テーブルによってRECORD_NUBERのカラムを設定する。
                List<Column> list = table.getPrimaryKeys();
                String pkey_name =list.get(0).getName();
                System.out.println("["+tableName+"]pkey_name="+pkey_name);
                for (Column column : table.getColumns()) {
                    //if (column.getName().equalsIgnoreCase("Id")) {
                    if (column.getName().equalsIgnoreCase(pkey_name)) {
                    //TODO テーブルによってRECORD_NUBERのカラムを設定する。
                    //if (column.getName().equalsIgnoreCase("レコード番号")) {
                            ColumnMap<String, Object> map = new ColumnMap<String, Object>();
                            map.put("TABLE_CAT", null);
                            map.put("TABLE_SCHEM", null);
                            map.put("TABLE_NAME", table.getName());
                            map.put("NON_UNIQUE", true);
                            map.put("INDEX_QUALIFIER", null);
                            map.put("INDEX_NAME", "FakeIndex" + counter++);
                            map.put("TYPE", DatabaseMetaData.tableIndexOther);
                            map.put("ORDINAL_POSITION", counter);
                            map.put("COLUMN_NAME", "Id");
                            map.put("ASC_OR_DESC", "A");
                            map.put("CARDINALITY", 1);
                            map.put("PAGES", 1);
                            map.put("FILTER_CONDITION", null);

                            maps.add(map);
                    }
                }
            }
        }
        return new KintoneResultSet(maps);
    }
}
