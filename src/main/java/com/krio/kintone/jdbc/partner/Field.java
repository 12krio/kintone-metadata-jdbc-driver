package com.krio.kintone.jdbc.partner;

/**
 * 項目
 */
public class Field {

    String type;
    int length;
    int precision;
    int digits;
    int byteLength;
    String name;
    String Label;
    String defaultValueFormula;
    String calculatedFormula;
    String inlineHelpText;
    PicklistEntry[] picklistValue;
    boolean calculated;
    boolean autoNumber;
    String[] referenceTo;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getDigits() {
        return digits;
    }

    public void setDigits(int digits) {
        this.digits = digits;
    }

    public int getByteLength() {
        return byteLength;
    }

    public void setByteLength(int byteLength) {
        this.byteLength = byteLength;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return Label;
    }
    public void setLabel(String label) {
        Label = label;
    }

    public String getDefaultValueFormula() {
        return defaultValueFormula;
    }
    public void setDefaultValueFormula(String defaultValueFormula) {
        this.defaultValueFormula = defaultValueFormula;
    }

    public String getCalculatedFormula() {
        return calculatedFormula;
    }
    public void setCalculatedFormula(String calculatedFormula) {
        this.calculatedFormula = calculatedFormula;
    }

    public String getInlineHelpText() {
        return inlineHelpText;
    }
    public void setInlineHelpText(String inlineHelpText) {
        this.inlineHelpText = inlineHelpText;
    }
    public PicklistEntry[] getPicklistValues() {
        return picklistValue;
    }
    public void setPicklistValues(PicklistEntry[] picklistEntry ) {
        this.picklistValue = picklistEntry;
    }

    public boolean isCalculated() {
        return calculated;
    }
    public void setCalculated(boolean calculated) {
        this.calculated = calculated;
    }

    public boolean isAutoNumber() {
        return autoNumber;
    }
    public void setAutoNumber(boolean autoNumber) {
        this.autoNumber = autoNumber;
    }

    public String[] getReferenceTo() {
        return referenceTo;
    }
    public void setReferenceTo(String[] referenceTo) {
        this.referenceTo = referenceTo;
    }


}
