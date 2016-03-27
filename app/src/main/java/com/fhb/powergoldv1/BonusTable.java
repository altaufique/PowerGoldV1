package com.fhb.powergoldv1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by FHB:Taufiq on 3/6/2016.
 */
public class BonusTable {
    public static final String PACKAGE_TBL = "PACKAGE";

    public static final Map<String, String> pkg_tbl;
    static {
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

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        int i; Integer length; String sql_str; String string;
        // Build PACKAGE table string
        i=0 ; sql_str=""; length = pkg_tbl.size();
        for(Map.Entry<String, String> entry : pkg_tbl.entrySet()){
            if (i < length - 1) sql_str = sql_str + entry.getKey() + " " + entry.getValue() + ", ";
            else sql_str = sql_str + entry.getKey() + " " + entry.getValue();
            i++;
        }
        string = "CREATE TABLE " + PACKAGE_TBL + " (" + sql_str + ");";
        sqLiteDatabase.execSQL(string);
        // Set value for PACKAGE_TBL
        List<String[]> pkg_val = setPackageValue();
        // TODO populate table using data from setPackageValue method
    }

    private List<String[]> setPackageValue() {
        String[] str1 = {"Gold","G", "350", "0.5gSy", "40", "10", "1", "10", "0", "30", "1", "15"};
        String[] str2 = {"Solid Gold","SG", "450", "1/4 Dinnar", "50", "10", "1", "10", "0", "30", "1", "20"};
        String[] str3 = {"Super Power Gold","Sp", "3800", "2 Dinnar", "400", "100", "2", "100", "2", "30", "10", "70"};

        List<String[]> lst_lstStr = new ArrayList<>();
        lst_lstStr.add(str1);
        lst_lstStr.add(str2);
        lst_lstStr.add(str3);

        return lst_lstStr;
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PACKAGE_TBL);
        onCreate(sqLiteDatabase);
    }
}