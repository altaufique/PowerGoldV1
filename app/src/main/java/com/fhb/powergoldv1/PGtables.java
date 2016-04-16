package com.fhb.powergoldv1;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FHB:Taufiq on 3/6/2016.
 */
public class PGtables {
    String memberTableName;
    Map<String, String> membersSchema;

    String packageTableName;
    Map<String, String> packageSchema;

    String weightTableName;
    Map<String, String> weightSchema;

    String rateTableName;
    Map<String, String> rateSchema;

    String authTableName;
    Map<String, String> authSchema;

    public PGtables () {
        memberTableName = getMemberTableName();
        membersSchema = getMemberSchema();

        packageTableName = getPackageTableName();
        packageSchema = getPkgSchema();

        weightTableName = getWeightTableName();
        weightSchema = getWeightSchema();

        rateTableName = getRateTableName();
        rateSchema = getRateSchema();

        authTableName = getAuthTableName();
        authSchema = getAuthSchema();
    }

    private Map<String,String> getAuthSchema () {
        Map<String, String> auth_table;
        {
            Map<String, String> aMap = new LinkedHashMap<>();
            //aMap.put("ID", "INTEGER");
            aMap.put("PG_USERNAME", "TEXT");
            aMap.put("PG_PASSWORD", "TEXT");
            aMap.put("PG_WEBPARAM", "TEXT"); //each time password changed, param need to be updated.
            aMap.put("NAME", "TEXT");
            aMap.put("DATE_REGISTERED", "TEXT");
            aMap.put("PACKAGE", "TEXT");
            auth_table = Collections.unmodifiableMap(aMap);
        }
        return auth_table;
    }

    private Map<String, String> getPkgSchema() {
        Map<String, String> pkg_tbl;
        {
            Map<String, String> aMap = new LinkedHashMap<>();
            aMap.put("ID", "INTEGER PRIMARY KEY AUTOINCREMENT");
            aMap.put("PKG_NAME", "TEXT");
            aMap.put("PKG_CODE", "TEXT");
            aMap.put("MEMBER_FEE", "TEXT");
            aMap.put("GOLD_TOKEN", "TEXT");
            aMap.put("BONUS_LV1_SPON", "TEXT");
            aMap.put("BONUS_LV2_SPON", "TEXT");
            aMap.put("BONUS_REPURCHASE", "TEXT");
            aMap.put("BONUS_KEYIN_MEMBER", "TEXT");
            aMap.put("BONUS_KEYIN_REP", "TEXT");
            aMap.put("BONUS_PAIR", "TEXT");
            aMap.put("BONUS_POINT_PERPAIR", "TEXT");
            aMap.put("MAX_PAIR_DAILY", "TEXT");
            pkg_tbl = Collections.unmodifiableMap(aMap);
        }
        return pkg_tbl;
    }

    private Map<String, String> getMemberSchema() {
        Map<String, String> member_tbl;
        {
            Map<String, String> aMap = new LinkedHashMap<>();
            aMap.put("ID", "INTEGER PRIMARY KEY AUTOINCREMENT");
            aMap.put("NAME", "TEXT NOT NULL");
            aMap.put("ADDRESS", "TEXT");
            aMap.put("MOBILE_NO", "TEXT");
            aMap.put("IC", "TEXT");
            aMap.put("EMAIL", "TEXT");
            aMap.put("PG_USERNAME", "TEXT UNIQUE");
            aMap.put("BANK", "TEXT");
            aMap.put("BANK_ACC_NO", "TEXT");
            aMap.put("PG_PKG", "TEXT");
            aMap.put("DATE_REGISTERED", "TEXT");
            member_tbl = Collections.unmodifiableMap(aMap);
        }
        return member_tbl;
    }

    private Map<String, String> getWeightSchema() {
        Map<String, String> weight_tbl;
        {
            Map<String, String> aMap = new LinkedHashMap<>();
            aMap.put("ID", "INTEGER PRIMARY KEY AUTOINCREMENT");
            aMap.put("ONE_GRAM", "TEXT");
            aMap.put("ONE_GRAM_SY", "TEXT");
            aMap.put("FIVE_GRAM", "TEXT");
            aMap.put("TEN_GRAM", "TEXT");
            aMap.put("TWENTY_GRAM", "TEXT");
            aMap.put("FIFTY_GRAM", "TEXT");
            aMap.put("HUNDRED_GRAM", "TEXT");
            aMap.put("FIVEHUNDRED_GRAM", "TEXT");
            aMap.put("TWO_DINAR", "TEXT");
            aMap.put("ONE_DINAR", "TEXT");
            aMap.put("QUARTER_DINAR", "TEXT");
            aMap.put("HALF_GRAM_SY", "TEXT");
            weight_tbl = Collections.unmodifiableMap(aMap);
        }
        return weight_tbl;
    }

    private Map<String, String> getRateSchema() {
        Map<String, String> rate_tbl;
        {
            Map<String, String> aMap = new LinkedHashMap<>();
            aMap.put("ID", "INTEGER PRIMARY KEY AUTOINCREMENT");
            aMap.put("DATE", "TEXT");
            aMap.put("PRICE_CAT", "TEXT");
            aMap.put("ONE_GRAM", "TEXT");
            aMap.put("ONE_GRAM_SY", "TEXT");
            aMap.put("FIVE_GRAM", "TEXT");
            aMap.put("TEN_GRAM", "TEXT");
            aMap.put("TWENTY_GRAM", "TEXT");
            aMap.put("FIFTY_GRAM", "TEXT");
            aMap.put("ONEHRD_GRAM", "TEXT");
            aMap.put("FIVEHRD_GRAM", "TEXT");
            aMap.put("TWO_DINAR", "TEXT");
            aMap.put("ONE_DINAR", "TEXT");
            aMap.put("QTR_DINAR", "TEXT");
            aMap.put("HALF_GRAM_SY", "TEXT");
            rate_tbl = Collections.unmodifiableMap(aMap);
        }
        return rate_tbl;
    }

    public List<String[]> setPackageData(Map<String, String> schema) {
        String[] str1 = {"Gold","G", "350", "0.5gSy", "40", "10", "1", "10", "0", "30", "1", "15"};
        String[] str2 = {"Solid Gold","SG", "450", "1/4 Dinnar", "50", "10", "1", "10", "0", "30", "1", "20"};
        String[] str3 = {"Super Power Gold","SP", "3800", "2 Dinnar", "400", "100", "2", "100", "2", "30", "10", "70"};

        List<String[]> lst_lstStr = new ArrayList<>();
        lst_lstStr.add(str1);
        lst_lstStr.add(str2);
        lst_lstStr.add(str3);

        return lst_lstStr;
    }

    public String[] setWeightData(Map<String, String> schema) {
        return new String[]{"1.00", "1.00", "5.00", "10.00", "20.00", "50.00", "100.00", "500.00", "8.50", "4.25", "1.06", "0.50"};
    }

    private String getAuthTableName () {
        return "AUTHENTICATION";
    }

    private String getMemberTableName () {
        return "MEMBERS";
    }

    private String getPackageTableName () {
        return "PACKAGES";
    }

    private String getWeightTableName () {
        return "WEIGHT";
    }

    private String getRateTableName () {
        return "RATE";
    }

    public void setContentValue (Map<String, String> schema, List<String> singlerow, List<String[]> multirow){

        ContentValues contentValues1 = new ContentValues();
        ContentValues contentValues2 = new ContentValues();
        ContentValues contentValues3 = new ContentValues();
        Integer i = 0; Long isInserted;

        for(Map.Entry<String, String> entry : schema.entrySet()){
            // Skip first column for  ID auto increment
            String[] str = multirow.get(i);
            if (entry.getKey().matches("ID")) continue;
            contentValues1.put(entry.getKey(), String.valueOf(str));
        }

    }
}
