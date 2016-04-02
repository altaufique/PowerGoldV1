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

    public PGtables () {
        this.getMemberTableName();
        this.getMemberSchema();
        this.getPackageTableName();
        this.getPkgSchema();
    }

    public Map<String, String> getPkgSchema() {
        Map<String, String> pkg_tbl;
        {
            Map<String, String> aMap = new LinkedHashMap<>();
            aMap.put("ID", "INTEGER PRIMARY KEY AUTOINCREMENT");
            aMap.put("PKG_NAME", "TEXT");
            aMap.put("PKG_CODE", "TEXT");
            aMap.put("MEMBER_FEE", "TEXT");
            aMap.put("GOLD_TOKEN", "TEXT");
            aMap.put("BONUS_LV1_SPON", "TEXT");
            aMap.put("BONUS_LV1_SPON", "TEXT");
            aMap.put("BONUS_REP", "TEXT");
            aMap.put("BONUS_KEYIN_MEMBER", "TEXT");
            aMap.put("BONUS_KEYIN_REP", "TEXT");
            aMap.put("BONUS_PAIR", "TEXT");
            aMap.put("BONUS_POINT_PERPAIR", "TEXT");
            aMap.put("MAX_PAIR_DAILY", "TEXT");
            pkg_tbl = Collections.unmodifiableMap(aMap);
        }
        return pkg_tbl;
    }

    public Map<String, String> getMemberSchema() {
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

    public String getMemberTableName () {
        String tablename = "MEMBERS";
        return tablename;
    }

    public String getPackageTableName () {
        String tablename = "PACKAGES";
        return tablename;
    }

    public List<String[]> setPackageData(Map<String, String> schema) {
        String[] str1 = {"Gold","G", "350", "0.5gSy", "40", "10", "1", "10", "0", "30", "1", "15"};
        String[] str2 = {"Solid Gold","SG", "450", "1/4 Dinnar", "50", "10", "1", "10", "0", "30", "1", "20"};
        String[] str3 = {"Super Power Gold","Sp", "3800", "2 Dinnar", "400", "100", "2", "100", "2", "30", "10", "70"};

        List<String[]> lst_lstStr = new ArrayList<>();
        lst_lstStr.add(str1);
        lst_lstStr.add(str2);
        lst_lstStr.add(str3);

        return lst_lstStr;
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
