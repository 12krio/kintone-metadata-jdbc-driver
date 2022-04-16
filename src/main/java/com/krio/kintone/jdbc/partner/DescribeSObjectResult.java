package com.krio.kintone.jdbc.partner;

/**
 * 詳細結果取得
 */
public class DescribeSObjectResult {
    Field[] fields = null;
    String name = null;
    String label = null;
    String labelPlural = null;
    RecordTypeInfo[] recordTypeInfo = null;
    ChildRelationship[] childRelationship = null;

    public Field[] getFields() {
        return fields;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabelPlural() {
        return labelPlural;
    }

    public void setLabelPlural(String labelPlural) {
        this.labelPlural = labelPlural;
    }

    public RecordTypeInfo[] getRecordTypeInfos() {
        return recordTypeInfo;
    }

    public void setRecordTypeInfo(RecordTypeInfo[] recordTypeInfo) {
        this.recordTypeInfo = recordTypeInfo;
    }

    public ChildRelationship[] getChildRelationships() {
        return childRelationship;
    }

    public void setChildRelationship(ChildRelationship[] childRelationship) {
        this.childRelationship = childRelationship;
    }

}
