package com.krio.kintone.jdbc;

import com.kintone.client.api.app.GetFormFieldsResponseBody;
import com.kintone.client.model.app.App;
import com.kintone.client.model.app.field.*;
import com.krio.kintone.jdbc.partner.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO record count https://developer.cybozu.io/hc/ja/articles/360029152012#step2
/**
 * Wraps the kintone describe calls web service outputting simple data objects.
 * <p>
 * Uses Kintone Clinet Jar.
 */
public class KintoneService {

    private final Filter filter;
    private final KintonePartnerConnection partnerConnection;

    public KintoneService(String un, String pw, String url, Filter filter) throws KintoneConnectionException {

        this.filter = filter;

        KintoneConnectorConfig partnerConfig = new KintoneConnectorConfig();
        //TODO Proxy
        if (System.getProperty("http.auth.ntlm.domain") != null) {
            partnerConfig.setNtlmDomain(System.getProperty("http.auth.ntlm.domain"));
        }
        if ((System.getProperty("http.proxyHost") != null) && (System.getProperty("http.proxyPort") != null)) {
            partnerConfig.setProxy(System.getProperty("http.proxyHost"), Integer.parseInt(System.getProperty("http.proxyPort")));
        }
        if (System.getProperty("http.proxyUser") != null) {
            partnerConfig.setProxyUsername(System.getProperty("http.proxyUser"));
        }
        if (System.getProperty("http.proxyPassword") != null) {
            partnerConfig.setProxyPassword(System.getProperty("http.proxyPassword"));
        }

        partnerConfig.setUsername(un);
        partnerConfig.setPassword(pw);
        if (url != null && url.length() > 0) {
            partnerConfig.setAuthEndpoint(url);
            log("kintone connection url " + url);
        }
        partnerConfig.setConnectionTimeout(60 * 1000);

        log(filter.toString());

        partnerConnection = Connector.newConnection(partnerConfig);
    }

    private void log(String message) {
        System.out.println("KintoneMetaDataDriver: " + message);
    }

    /**
     * Grab the describe data and return it wrapped in a factory.
     * <p>
     * partnerConnection.describeGlobal().getSobjects()　全オブジェクト種類（Table,View etc)の名前一覧
     * partnerConnection.describeSObjects(batch)
     */
    public ResultSetFactory createResultSetFactory() throws KintoneConnectionException {

        ResultSetFactory factory = new ResultSetFactory();
        //TODO childParentReference
//        Map<String, String> childParentReferenceNames = new HashMap<String, String>();
//        Map<String, Boolean> childCascadeDeletes = new HashMap<String, Boolean>();
//
//        List<String> typesList = getSObjectTypes();//全オブジェクト種類（Table,View etc)の名前一覧
//        Set<String> typesSet = new HashSet<String>(typesList);
//        List<String[]> batchedTypes = batch(typesList);
//
//        // Need all child references so run through the batches first
            //バッチタイプ
//        for (String[] batch : batchedTypes) {
                //SObject 配列
//            DescribeSObjectResult[] sobs = partnerConnection.describeSObjects(batch);
//            if (sobs != null) {
                    //SObject
//                for (DescribeSObjectResult sob : sobs) {
//                    ChildRelationship[] crs = sob.getChildRelationships();
//                    if (crs != null) {
                            //子関係
//                        for (ChildRelationship cr : crs) {
                                //子を持っている
//                            if (typesSet.contains(cr.getChildSObject())) {
//                                String qualified = cr.getChildSObject() + '.' + cr.getField();
                                    //子オブジェクト名.子の項目名 , 関係名
//                                childParentReferenceNames.put(qualified, cr.getRelationshipName());
                                    // 子オブジェクト名.子の項目名 , カスケード削除
//                                childCascadeDeletes.put(qualified, cr.isCascadeDelete());
//                            }
//                        }
//                    }
//                }
//            }
//        }

        // Run through the batches again now the child references are available
        HashMap<Long,String> keyMap = new HashMap();
        List<App> appList = partnerConnection.getApps();
        String tableName = "";
        for (App app : appList) {
            //RECORD_NUMBERのカラム名を探して
            int valLen = String.valueOf(appList.size()).length();
            String fomarted_app_id = String.format("%0" + valLen + "d", app.getAppId());
            if ("".equals(app.getCode()) || null == app.getCode()) {
                tableName = fomarted_app_id + "_" + app.getName();
                //tableName =  fomarted_app_id ;
            } else {
                tableName = fomarted_app_id + "_" + app.getCode() + "_" + app.getName();
                //tableName =  fomarted_app_id ;
            }
            //log("tableName"+tableName);
            keyMap.put(app.getAppId(),tableName);
            //log("tableName"+keyMap.toString());
        }

        for (App app : appList) {
            //TODO DEBUG エラーなしで処理可能か？
            //DBAnalyzer.java getTablesWithIncrementingColumnNames
            //Table comments
            List<String> comments = new ArrayList<String>();
            //結果あり
            comments.add("Labels: " + app.getAppId() + " | " + app.getName());
            comments.add("詳細:" + app.getDescription());//TODO OK?
            //String recordTypes = getRecordTypes(sob.getRecordTypeInfos());
            //if (recordTypes != null) {
            //    comments.add("Record Types: " + recordTypes);
            //}

            //log("App情報["+app+"]");
            GetFormFieldsResponseBody gffResp = partnerConnection.getAppColumns(app.getAppId());
            //final String tableName2 = tableName;
            //if (sobs != null) {
            if (gffResp != null) {
                Map<String, FieldProperty> map = gffResp.getProperties();

                List<Column> columns = new ArrayList<Column>(map.size());
                map.forEach((k, v) -> {
                    /** 項目設定処理 */
                    //columns.add(setColumns((String) k, (FieldProperty) v));
                    //columns.add(convertColumns(k, v,keyMap.get(app.getAppId()),keyMap));
                    //TODO DEBUG
                    //columns.add(convertColumns(k+"_", v,keyMap.get(app.getAppId()),keyMap));
                    columns.add(convertColumns(k, v,keyMap.get(app.getAppId()),keyMap));
                });

                String tableN = keyMap.get(app.getAppId());
                //log("tableN"+tableN);
                Table table = new Table(tableN, separate(comments, "\n"), columns);

                String PrimaryKeyColumns = "";
                for (Column column:columns ) {
                    if(column.isPrimaryKey()){
                        log("column.getName();"+column.getName());
                        table.setPrimaryKeys(column);
                    }
                }
                factory.addTable(table);
            }
        }
        //KintoneClient Close
        try {
            partnerConnection.closeKintoneClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return factory;
    }

    /**
     * @param k
     * @param fp
     * @return 項目説明：https://developer.cybozu.io/hc/ja/articles/201941834
     */
    private Column convertColumns(String k, FieldProperty fp,String tableName,HashMap<Long,String> keyMap) {
        //項目コメント設定
        Column column = new Column(k, fp.getType().toString());
        List<String> comments = new ArrayList<String>();
        StringBuilder label = new StringBuilder();
        String size = "0";
        boolean nillable = false;
        switch (fp.getType()) {
            //レコード情報に関するフィールド
            case RECORD_NUMBER: //レコードID RECORD_NUMBER
                RecordNumberFieldProperty rnfp = (RecordNumberFieldProperty) fp;
                column.setPrimaryKey(true);
                column.setCalculated(true); /** 計算 or 自動番号 */
                label.append("フィールド名:"+rnfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+rnfp.getCode()+"/");
                label.append("フィールド名の非表示:"+rnfp.getNoLabel()+"/");
                break;
            case CREATOR: //作成者
                //log("項目"+fp.toString());
                CreatorFieldProperty tfp = (CreatorFieldProperty) fp;
                label.append("フィールド名:"+tfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+tfp.getCode()+"/");
                label.append("フィールド名の非表示:"+tfp.getNoLabel()+"/");
                break;
            case CREATED_TIME: //作成日時
                //log("項目"+fp.toString());
                CreatedTimeFieldProperty ctfp = (CreatedTimeFieldProperty)fp;
                label.append("フィールド名:"+ctfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+ctfp.getCode()+"/");
                label.append("フィールド名の非表示:"+ctfp.getNoLabel()+"/");
                break;
            case MODIFIER: //更新者
                //log("項目"+fp.toString());
                ModifierFieldProperty mfp = (ModifierFieldProperty) fp;
                label.append("フィールド名:"+mfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+mfp.getCode()+"/");
                label.append("フィールド名の非表示:"+mfp.getNoLabel()+"/");
                break;
            case UPDATED_TIME: //更新日時 "2012-01-11T11:30:00Z"
                //log("項目"+fp.toString());
                UpdatedTimeFieldProperty utfp = (UpdatedTimeFieldProperty) fp;
                label.append("フィールド名:"+utfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+utfp.getCode()+"/");
                label.append("フィールド名の非表示:"+utfp.getNoLabel()+"/");
                break;
            // カスタムフィールド
            case SINGLE_LINE_TEXT:  // 文字列（1行）or Lookup
            case NUMBER:            // 数値 or Lookup
                if (fp instanceof LookupFieldProperty) {//Lookup
                    LookupFieldProperty lfp = (LookupFieldProperty) fp;
                    label.append("フィールド名:"+lfp.getLabel()+"/");//共通項目
                    label.append("フィールドコード:"+lfp.getCode()+"/");
                    label.append("フィールド名の非表示:"+lfp.getNoLabel()+"/");
                    LookupSetting ls = lfp.getLookup();

                    label.append("フィールド名の非表示:"+ls.getLookupPickerFields()+"/");
                    label.append("関連付けるアプリのアプリID:"+ls.getRelatedApp().getApp()+"/");
                    label.append("関連付けるアプリのアプリコード:"+ls.getRelatedApp().getCode()+"/");
                    label.append("ソート順:"+ls.getSort()+"/");
                    label.append("条件:"+ls.getFilterCond()+"/");
                    label.append("マッピング項目:"+ls.getFieldMappings()+"/");
                    label.append("Lookup選択項目:"+ls.getLookupPickerFields()+"/");
                    label.append("関連キー項目:"+ls.getRelatedKeyField()+"/");

                    //関連アプリ名、関連カラム名
                    column.setReferencedTable(keyMap.get(ls.getRelatedApp().getApp())+"");
                    //カラム名が数字で終わる場合にエラーが発生
                    //column.setReferencedColumn(ls.getRelatedKeyField()+"_");//関連カラム
                    column.setReferencedColumn(ls.getRelatedKeyField());//関連カラム

                } else if (fp instanceof SingleLineTextFieldProperty) {
                    SingleLineTextFieldProperty sltfp = (SingleLineTextFieldProperty) fp;//文字列（1行）
                    label.append("フィールド名:"+sltfp.getLabel()+"/");//共通項目
                    label.append("フィールドコード:"+sltfp.getCode()+"/");
                    label.append("フィールド名の非表示:"+sltfp.getNoLabel()+"/");

                    label.append("必須可否:"+sltfp.getRequired()+"/");
                    label.append("重複可否:"+sltfp.getUnique()+"/");
                    label.append("初期値:"+sltfp.getDefaultValue()+"/");
                    label.append("自動計算:"+sltfp.getExpression()+"/");
                    label.append("計算式の非表示:"+sltfp.getHideExpression()+"/");
                    label.append("フィールドタイプ:"+sltfp.getType()+"/");
                    label.append("文字数の(最大文字数):"+sltfp.getMaxLength()+"/");
                    label.append("文字数の(最小文字数):"+sltfp.getMinLength()+"/");
                    size = String.valueOf(sltfp.getMaxLength());//Size
                    nillable = !sltfp.getRequired();

                } else if (fp instanceof NumberFieldProperty) {
                    NumberFieldProperty nfp = (NumberFieldProperty)fp;
                    label.append("フィールド名:"+nfp.getLabel()+"/");//共通項目
                    label.append("フィールドコード:"+nfp.getCode()+"/");
                    label.append("フィールド名の非表示:"+nfp.getNoLabel()+"/");

                    label.append("必須可否:"+nfp.getRequired()+"/");
                    label.append("重複可否:"+nfp.getUnique()+"/");
                    label.append("値の制限(数値)最大:"+nfp.getMaxValue()+"/");
                    label.append("値の制限(数値)最小:"+nfp.getMinValue()+"/");
                    label.append("初期値:"+nfp.getDefaultValue()+"/");
                    label.append("数値の桁区切り:"+nfp.getDigit());
                    label.append("小数点以下の表示桁数:"+nfp.getDisplayScale()+"/");
                    label.append("単位記号:"+nfp.getUnit()+"/");
                    label.append("単位記号の位置:"+nfp.getUnitPosition()+"/");
                    size = String.valueOf(nfp.getMaxValue());
                    nillable = !nfp.getRequired();
                }
                break;
            case MULTI_LINE_TEXT:  //文字列（複数行）
                MultiLineTextFieldProperty mltfp = (MultiLineTextFieldProperty) fp;
                label.append("フィールド名:"+mltfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+mltfp.getCode()+"/");
                label.append("フィールド名の非表示:"+mltfp.getNoLabel()+"/");
                label.append("フィールドタイプ:"+mltfp.getType()+"/");
                label.append("必須可否:"+mltfp.getRequired()+"/");
                label.append("初期値:"+mltfp.getDefaultValue()+"/");
                nillable = !mltfp.getRequired();
                break;
            case RICH_TEXT: //リッチエディター
                RichTextFieldProperty rtfp = (RichTextFieldProperty) fp;
                label.append("フィールド名:"+rtfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+rtfp.getCode()+"/");
                label.append("フィールド名の非表示:"+rtfp.getNoLabel()+"/");
                label.append("フィールドタイプ:"+rtfp.getType()+"/");
                label.append("必須可否:"+rtfp.getRequired()+"/");
                label.append("初期値:"+rtfp.getDefaultValue()+"/");
                nillable = !rtfp.getRequired();
                break;
            case CALC: //計算 ※3
                CalcFieldProperty cfp = (CalcFieldProperty) fp;
                label.append("フィールド名:"+cfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+cfp.getCode()+"/");
                label.append("フィールド名の非表示:"+cfp.getNoLabel()+"/");

                label.append("必須可否:"+cfp.getRequired()+"/");
                label.append("自動計算:"+cfp.getExpression()+"/");
                label.append("計算式の非表示:"+cfp.getHideExpression()+"/");
                label.append("フィールドタイプ:"+cfp.getFormat()+"/");
                label.append("小数点以下の表示桁数:"+cfp.getDisplayScale()+"/");
                label.append("単位記号:"+cfp.getUnit()+"/");
                label.append("単位記号の位置:"+cfp.getUnitPosition()+"/");
                nillable = !cfp.getRequired();
                column.setCalculated(true); /** 計算 or 自動番号 */
                break;
            case CHECK_BOX: //チェックボックス
                CheckBoxFieldProperty cbfp = (CheckBoxFieldProperty) fp;
                label.append("フィールド名:"+cbfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+cbfp.getCode()+"/");
                label.append("フィールド名の非表示:"+cbfp.getNoLabel()+"/");

                label.append("必須可否:"+cbfp.getRequired()+"/");
                label.append("初期値:"+cbfp.getDefaultValue()+"/");
                label.append("Align:"+cbfp.getAlign()+"/");
                label.append("項目:"+cbfp.getOptions()+"/");
                nillable = !cbfp.getRequired();
                break;
            case RADIO_BUTTON: //ラジオボタン
                RadioButtonFieldProperty rbfp = (RadioButtonFieldProperty) fp;
                label.append("フィールド名:"+rbfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+rbfp.getCode()+"/");
                label.append("フィールド名の非表示:"+rbfp.getNoLabel()+"/");
                label.append("必須可否:"+rbfp.getRequired()+"/");
                label.append("初期値:"+rbfp.getDefaultValue()+"/");
                label.append("Align:"+rbfp.getAlign()+"/");
                label.append("項目:"+rbfp.getOptions()+"/");
                nillable = !rbfp.getRequired();
                break;
            case MULTI_SELECT: //複数選択 ※1
                MultiSelectFieldProperty msfp = (MultiSelectFieldProperty) fp;
                label.append("フィールド名:"+msfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+msfp.getCode()+"/");
                label.append("フィールド名の非表示:"+msfp.getNoLabel()+"/");
                label.append("必須可否:"+msfp.getRequired()+"/");
                label.append("初期値:"+msfp.getDefaultValue()+"/");
                label.append("項目:"+msfp.getOptions()+"/");
                nillable = !msfp.getRequired();
                break;
            case DROP_DOWN: //ドロップダウン ※1
                DropDownFieldProperty ddfp = (DropDownFieldProperty) fp;
                label.append("フィールド名:"+ddfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+ddfp.getCode()+"/");
                label.append("フィールド名の非表示:"+ddfp.getNoLabel()+"/");
                label.append("必須可否:"+ddfp.getRequired()+"/");
                label.append("初期値:"+ddfp.getDefaultValue()+"/");
                label.append("項目:"+ddfp.getOptions()+"/");
                nillable = !ddfp.getRequired();
                break;
            case USER_SELECT: //ユーザー選択
                //log("項目"+fp.toString());
                UserSelectFieldProperty usfp = (UserSelectFieldProperty) fp;
                label.append("フィールド名:"+usfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+usfp.getCode()+"/");
                label.append("フィールド名の非表示:"+usfp.getNoLabel()+"/");
                label.append("必須可否:"+usfp.getRequired()+"/");
                label.append("初期値:"+usfp.getDefaultValue()+"/");
                label.append("要素:"+usfp.getEntities()+"/");
                nillable = !usfp.getRequired();
                break;
            case ORGANIZATION_SELECT:  //組織選択
                //log("項目"+fp.toString());
                OrganizationSelectFieldProperty osfp = (OrganizationSelectFieldProperty) fp;
                label.append("フィールド名:"+osfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+osfp.getCode()+"/");
                label.append("フィールド名の非表示:"+osfp.getNoLabel()+"/");
                label.append("必須可否:"+osfp.getRequired()+"/");
                label.append("初期値:"+osfp.getDefaultValue()+"/");
                label.append("要素:"+osfp.getEntities()+"/");
                nillable = !osfp.getRequired();
                break;
            case GROUP_SELECT: //グループ選択
                //log("項目"+fp.toString());
                GroupSelectFieldProperty gsfp = (GroupSelectFieldProperty) fp;
                label.append("フィールド名:"+gsfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+gsfp.getCode()+"/");
                label.append("フィールド名の非表示:"+gsfp.getNoLabel()+"/");
                label.append("必須可否:"+gsfp.getRequired()+"/");
                label.append("初期値:"+gsfp.getDefaultValue()+"/");
                label.append("要素:"+gsfp.getEntities()+"/");
                nillable = !gsfp.getRequired();
                break;
            case DATE: //日付 ※2
                //log("項目"+fp.toString());
                DateFieldProperty dfp = (DateFieldProperty) fp;
                label.append("フィールド名:"+dfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+dfp.getCode()+"/");
                label.append("フィールド名の非表示:"+dfp.getNoLabel()+"/");
                label.append("必須可否:"+dfp.getRequired()+"/");
                label.append("初期値:"+dfp.getDefaultValue()+"/");
                label.append("初期値の式:"+dfp.getDefaultNowValue()+"/");
                label.append("重複可否:"+dfp.getUnique()+"/");
                nillable = !dfp.getRequired();
                break;
            case TIME: //時刻 ※2
                //log("項目"+fp.toString());
                TimeFieldProperty timefp = (TimeFieldProperty) fp;
                label.append("フィールド名:"+timefp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+timefp.getCode()+"/");
                label.append("フィールド名の非表示:"+timefp.getNoLabel()+"/");
                label.append("必須可否:"+timefp.getRequired()+"/");
                label.append("初期値:"+timefp.getDefaultValue()+"/");
                label.append("初期値の式:"+timefp.getDefaultNowValue()+"/");
                nillable = !timefp.getRequired();
                break;
            case DATETIME: //日時 ※2
                //log("項目"+fp.toString());
                DateTimeFieldProperty dtfp = (DateTimeFieldProperty) fp;
                label.append("フィールド名:"+dtfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+dtfp.getCode()+"/");
                label.append("フィールド名の非表示:"+dtfp.getNoLabel()+"/");
                label.append("必須可否:"+dtfp.getRequired()+"/");
                label.append("初期値:"+dtfp.getDefaultValue()+"/");
                label.append("初期値の式:"+dtfp.getDefaultNowValue()+"/");
                label.append("重複可否:"+dtfp.getUnique()+"/");
                nillable = !dtfp.getRequired();
                break;
            case LINK: //リンク
                //log("項目"+fp.toString());
                LinkFieldProperty lfp = (LinkFieldProperty) fp;
                label.append("フィールド名:"+lfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+lfp.getCode()+"/");
                label.append("フィールド名の非表示:"+lfp.getNoLabel()+"/");
                label.append("必須可否:"+lfp.getRequired()+"/");
                label.append("重複可否:"+lfp.getUnique()+"/");
                label.append("初期値:"+lfp.getDefaultValue()+"/");
                label.append("フィールドタイプ:"+lfp.getType()+"/");
                label.append("文字数の(最大文字数):"+lfp.getMaxLength()+"/");
                label.append("文字数の(最小文字数):"+lfp.getMinLength()+"/");
                label.append("種類:"+lfp.getProtocol()+"/");
                nillable = !lfp.getRequired();
                break;
            case FILE://添付ファイル
                //log("項目"+fp.toString());
                FileFieldProperty fffp = (FileFieldProperty) fp;
                label.append("フィールド名:"+fffp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+fffp.getCode()+"/");
                label.append("フィールド名の非表示:"+fffp.getNoLabel()+"/");
                label.append("必須可否:"+fffp.getRequired()+"/");
                label.append("サムネイルサイズ:"+fffp.getThumbnailSize()+"/");
                nillable = !fffp.getRequired();
                break;
            case SUBTABLE://テーブル
                //log("項目"+fp.toString());
                SubtableFieldProperty subfp = (SubtableFieldProperty) fp;
                label.append("フィールド名:"+subfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+subfp.getCode()+"/");
                label.append("フィールド名の非表示:"+subfp.getNoLabel()+"/");
                label.append("フィールド:"+subfp.getFields()+"/");

                break;
            case REFERENCE_TABLE://関連レコード一覧
                //log("項目"+fp.toString());
                ReferenceTableFieldProperty rftp = (ReferenceTableFieldProperty) fp;
                label.append("フィールド名:"+rftp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+rftp.getCode()+"/");
                label.append("フィールド名の非表示:"+rftp.getNoLabel()+"/");
                label.append("参照するアプリ情報:"+rftp.getReferenceTable()+"/");
                break;
            case CATEGORY://カテゴリー ※3
                //log("項目"+fp.toString());
                CategoryFieldProperty cafp = (CategoryFieldProperty) fp;
                label.append("フィールド名:"+cafp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+cafp.getCode()+"/");
                label.append("有効:"+cafp.getEnabled()+"/");
                break;
            case STATUS://ステータス ※3
                //log("項目"+fp.toString());
                StatusFieldProperty stfp = (StatusFieldProperty) fp;
                label.append("フィールド名:"+stfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+stfp.getCode()+"/");
                label.append("有効:"+stfp.getEnabled()+"/");
                break;
            case STATUS_ASSIGNEE://作業者 ※3
                //log("項目"+fp.toString());
                StatusAssigneeFieldProperty safp = (StatusAssigneeFieldProperty) fp;
                label.append("フィールド名:"+safp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+safp.getCode()+"/");
                label.append("有効:"+safp.getEnabled()+"/");
                break;
            case GROUP://グループ GROUP
                //log("項目"+fp.toString());
                GroupFieldProperty gfp = (GroupFieldProperty) fp;
                label.append("フィールド名:"+gfp.getLabel()+"/");//共通項目
                label.append("フィールドコード:"+gfp.getCode()+"/");
                label.append("フィールド名の非表示:"+gfp.getNoLabel()+"/");
                break;
            default:
                //リビジョン/ラベル LABEL/スペース SPACER/罫線 HR
                break;
        }
        //共通項目設定
        comments.add(label.toString());//ラベル
        //参照項目
//            if ("reference".equals(field.getType().toString())) {
//                // MasterDetail vs Reference apparently not
//                // in API; cascade delete is though
//                String qualified = sob.getName() + "." + field.getName();
//                String childParentReferenceName = childParentReferenceNames.get(qualified);
//                Boolean cascadeDelete = childCascadeDeletes.get(qualified);
//                if (childParentReferenceName != null && cascadeDelete != null) {
//                    comments.add("Referenced: " + childParentReferenceName + (cascadeDelete ? " (cascade delete)" : ""));
//                }
//            }
        column.setComments(separate(comments, "\n"));
        if( !"null".equals(size)) {
            column.setLength(new Integer(size));//レングス
        }
        column.setNillable(nillable);/** Null許可 */
        return column;
    }

    private String getType(Field field) {
        String s = field.getType();
        // WSC adds this prefix for some types
        if (s.startsWith("_")) {
            s = s.substring("_".length());
        }
        return s.equalsIgnoreCase("double") ? "decimal" : s;
    }

    private int getLength(Field field) {
        if (field.getLength() != 0) {
            return field.getLength();
        } else if (field.getPrecision() != 0) {
            return field.getPrecision();
        } else if (field.getDigits() != 0) {
            return field.getDigits();
        } else if (field.getByteLength() != 0) {
            return field.getByteLength();
        } else {
            // SchemaSpy expects a value
            return 0;
        }
    }

    private String getPicklistValues(PicklistEntry[] entries) {
        if (entries != null && entries.length > 0) {
            List<String> values = new ArrayList<String>(128);
            for (PicklistEntry entry : entries) {
                values.add(entry.getValue());
            }
            if (values.size() > 0) {
                return separate(values, " | ");
            }
        }
        return null;
    }

    private String getRecordTypes(RecordTypeInfo[] rts) {
        if (rts != null && rts.length > 0) {
            List<String> values = new ArrayList<String>(16);
            for (RecordTypeInfo rt : rts) {
                // Master always present
                if (!rt.getName().equalsIgnoreCase("Master")) {
                    values.add(rt.getName() + (rt.isDefaultRecordTypeMapping() ? " (default)" : ""));
                }
            }
            if (values.size() > 0) {
                return separate(values, " | ");
            }
        }
        return null;
    }

    // Avoid EXCEEDED_MAX_TYPES_LIMIT on call by breaking into batches
    private List<String[]> batch(List<String> types) {

        List<String[]> batchedTypes = new ArrayList<String[]>();

        final int batchSize = 100;
        for (int batch = 0; batch < (types.size() + batchSize - 1) / batchSize; batch++) {
            int from = batch * batchSize;
            int to = (batch + 1) * batchSize;
            if (to > types.size()) {
                to = types.size();
            }
            List<String> t = types.subList(from, to);
            String[] a = new String[t.size()];
            t.toArray(a);
            batchedTypes.add(a);
        }

        return batchedTypes;
    }

    private List<String> getSObjectTypes() throws KintoneConnectionException {

        ArrayList<DescribeGlobalSObjectResult> sobs = partnerConnection.describeGlobal().getSobjects();
//
        List<String> list = new ArrayList<String>();
        for (DescribeGlobalSObjectResult sob : sobs) {
            if (keep(sob)) {
                list.add(sob.getName());
            }
        }
        return list;
    }

    private boolean keep(DescribeGlobalSObjectResult sob) {
        // Filter tables.
        // Normally want the User table filtered as all objects are associated with that
        // so the graphs become a mess and very slow to generate.
        return filter.accept(sob);
    }

    private boolean keep(Field field) {
        // Keeping all fields
        return true;
    }

    private String separate(List<String> terms, String separator) {
        StringBuilder sb = new StringBuilder(2048);
        for (String term : terms) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append(term);
        }
        return sb.toString();
    }
}
