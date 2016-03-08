package com.fhb.powergoldv1;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by VAIO on 3/6/2016.
 */
public class DatabaseController extends SQLiteOpenHelper {
    // Database details
    public static final String DATABASE_NAME = "PG.db";
    public static final Integer DB_VERSION = 1;

    // Members table details
    public static final String MEMBERS_TBL = "MEMBERS";

    //One way to intialize HashMap.
    public static final Map<String, String> member_tbl;
    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("ID", "INTEGER PRIMARY KEY AUTOINCREMENT");
        aMap.put("NAME", "TEXT NOT NULL");
        aMap.put("ADDRESS", "TEXT");
        aMap.put("MOBILE_NO", "TEXT");
        aMap.put("EMAIL", "TEXT");
        aMap.put("PG_USERNAME", "TEXT");
        aMap.put("BANK", "TEXT");
        aMap.put("BANK_ACC_NO", "TEXT");
        aMap.put("PG_PKG", "TEXT");
        aMap.put("DATE_REGISTERED", "TEXT");
        member_tbl = Collections.unmodifiableMap(aMap);
    };

    public DatabaseController(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Build create table sql string
        int i = 0; String sql_str = "";
        Integer length = member_tbl.size();
        for(Map.Entry<String, String> entry : member_tbl.entrySet()){
            if (i < length - 1) sql_str = sql_str + entry.getKey() + " " + entry.getValue() + ", ";
            else sql_str = sql_str + entry.getKey() + " " + entry.getValue();
            i++;
        }
        String string = "CREATE TABLE " + MEMBERS_TBL + "(" + sql_str + ");";
        sqLiteDatabase.execSQL("CREATE TABLE " + MEMBERS_TBL + "(" + sql_str + ");");
    }

    public boolean insert_member(String[] strings) {
        // Open database connection. Remember to close by calling close()
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        Integer i = 0;
        for(Map.Entry<String, String> entry : member_tbl.entrySet()){
            contentValues.put(entry.getKey(), strings[i++]);
        }

        this.getWritableDatabase().insertOrThrow(MEMBERS_TBL, "", contentValues);
       // long result = db.insert(MEMBERS_TBL, null, contentValues);

        // Close the database
        db.close();

/*        if (result == -1)
            return false; // failed
        else*/
            return true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(sqLiteDatabase);
    }
}