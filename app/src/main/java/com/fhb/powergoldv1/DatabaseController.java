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
public class DatabaseController extends SQLiteOpenHelper {
    // Database details
    public static final String DATABASE_NAME = "PG.db";
    public static final Integer DB_VERSION = 5;

    // Members table details
    public static final String MEMBERS_TBL = "MEMBERS";

    //One way to intialize HashMap.
    public static final Map<String, String> member_tbl;
    static {
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

        String string = "CREATE TABLE " + MEMBERS_TBL + " (" + sql_str + ");";
        sqLiteDatabase.execSQL(string);
    }

    public boolean insert_member(String[] strings) throws Exception {
        // Open database connection. Remember to close by calling close()
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        Integer i = 0;
        for(Map.Entry<String, String> entry : member_tbl.entrySet()){
            // Skip first column for  ID auto increment
            if (entry.getKey().matches("ID")) continue;
            contentValues.put(entry.getKey(), strings[i++]);
        }

        Long isInserted;
        isInserted = db.insertOrThrow(MEMBERS_TBL, "", contentValues);
       // long result = db.insert(MEMBERS_TBL, null, contentValues);

        // Close the database
        db.close();

        return isInserted != -1;
    }

    /**
     * query member extract rows from database and return List of Arraylist of each element in row
     * @return List of String array
     */
    public List<List<String>> query_member() {

        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + MEMBERS_TBL, null);

        List<List<String>> arr_arr = new ArrayList<>();
        Integer i;
        Integer count=cursor.getCount();
        Integer cols = cursor.getColumnCount();
        while (cursor.moveToNext()) {
            List<String> arr = new ArrayList<>(); // this is the way to declare at the same time reset arr to clear content insted of arr.clear() to store new element or array.
            for (i = 0; i < cols; i++) {
                arr.add(cursor.getString(i));
            }
            arr_arr.add(arr);
        }
        return arr_arr;
    }

    public boolean update_member(Integer name_ID, String[] strings) {
        // Open database connection. Remember to close by calling close()
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        Set<String> keys = member_tbl.keySet(); // get all the keys
        Integer i = 0;
        String where_str = null; // store ID key name to add in db.update argument.
        for(String s : keys) {
            //Skip the ID key and continue
            if(s.equals("ID")) {
                where_str = s + "=" + name_ID;
                continue;
            }
            /*if (s.equals("PG_USERNAME")) {
                //continue;
            }*/
            contentValues.put(s, strings[i++]);
        }
        //String id = strings[0];
        int rowUpdated = db.update(MEMBERS_TBL, contentValues, where_str, null);
        // long result = db.insert(MEMBERS_TBL, null, contentValues);

        // Close the database
        db.close();

        return rowUpdated != -1;
    }

    public boolean delete_member(Integer name_ID) {
        // Open database connection. Remember to close by calling close()
        SQLiteDatabase db = this.getWritableDatabase();

        String where_str = "ID=" + name_ID;
        int rowDeleted = db.delete(MEMBERS_TBL, where_str, null);

        // Close the database
        db.close();

        return rowDeleted != -1;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MEMBERS_TBL);
        onCreate(sqLiteDatabase);
    }
}