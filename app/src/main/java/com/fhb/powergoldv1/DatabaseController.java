package com.fhb.powergoldv1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by FHB:Taufiq on 3/6/2016.
 */
public class DatabaseController extends SQLiteOpenHelper {
    // Database details
    public static final String DATABASE_NAME = "PG.db";
    public static final Integer DB_VERSION = 8;
    public static final String MEMBERS_TBL = "MEMBERS";
    public static final String PACKAGES_TBL = "PACKAGES";

    public DatabaseController(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        PGtables pgtables = new PGtables();

        Map<String, String> members_schema = pgtables.getMemberSchema();
        Map<String, String> package_schema = pgtables.getPkgSchema();

        String member_table_schema = createTableSQLstring(members_schema);
        String package_table_schema = createTableSQLstring(package_schema);

        String create_member_table = "CREATE TABLE IF NOT EXISTS " + MEMBERS_TBL + " (" + member_table_schema + ");";
        String create_package_table = "CREATE TABLE IF NOT EXISTS " + PACKAGES_TBL + " (" + package_table_schema + ");";

        sqLiteDatabase.execSQL(create_member_table);
        sqLiteDatabase.execSQL(create_package_table);

        List<String[]> pkg_data = pgtables.setPackageData(package_schema);

        for (int i=0; i<pkg_data.size(); i++ ) {
            try {
                // insert_value(pgtables.getPackageTableName(), package_schema, pkg_data.get(i));
                // Do not called this.insert_value() from onCreate to avoid error of
                // "getDatabase called recursively". onCreate automatic run when database not found
                // and already have all the database functionality. Don't call getWritable() or
                // getReadable() again.
                // use sqLiteDatabase.insert() directly from here.

                // List<String[]> pkg_data to ContentValues to use saLiteDatabase.insert()
                ContentValues contentValues = (ContentValues) convertToContentvalues(pgtables.getPkgSchema(), pkg_data.get(i));


                sqLiteDatabase.insertOrThrow(pgtables.getPackageTableName(), null, contentValues);
            } catch (Exception e) {
                Log.d("FHB", "DB insert error!!!");
                Log.d("FHB", e.toString());
            }
        }
    }

    public String createTableSQLstring(Map<String, String> members_schema) {

        // Build create table sql string
        int i = 0; String sql_str = "";
        Integer length = members_schema.size();
        for(Map.Entry<String, String> entry : members_schema.entrySet()){
            if (i < length - 1) sql_str = sql_str + entry.getKey() + " " + entry.getValue() + ", ";
            else sql_str = sql_str + entry.getKey() + " " + entry.getValue();
            i++;
        }
        return sql_str;
    }

    public boolean insert_value(String table, Map<String, String> schema, String[] strings) {
        // Open database connection. Remember to close by calling close()
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        Integer i = 0;
        for(Map.Entry<String, String> entry : schema.entrySet()){
            // Skip first column for  ID auto increment
            if (entry.getKey().matches("ID")) continue;
            contentValues.put(entry.getKey(), strings[i++]);
        }

        Long isInserted;
        isInserted = db.insertOrThrow(table, "", contentValues);
        // long result = db.insert(MEMBERS_TBL, null, contentValues);

        // Close the database
        db.close();

        return isInserted != -1;
    }



/*
    public boolean insert_member(String[] strings) throws Exception {
        // Open database connection. Remember to close by calling close()
        SQLiteDatabase db = this.getWritableDatabase();
        PGtables pgtables = new PGtables();
        Map<String, String> members_schema = pgtables.getMemberSchema();

        ContentValues contentValues = new ContentValues();
        Integer i = 0;
        for(Map.Entry<String, String> entry : members_schema.entrySet()){
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
*/

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
        PGtables pgtables = new PGtables();
        Map<String, String> members_schema = pgtables.getMemberSchema();

        ContentValues contentValues = new ContentValues();
        Set<String> keys = members_schema.keySet(); // get all the keys
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

    public ContentValues convertToContentvalues (Map<String, String> schema, String[] strings) {
        ContentValues contentValues = new ContentValues();
        Integer i = 0;
        for (Map.Entry<String, String> entry : schema.entrySet()) {
            // Skip first column for  ID auto increment
            if (entry.getKey().matches("ID")) continue;
            contentValues.put(entry.getKey(), strings[i++]);
        }
        return contentValues;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        PGtables pgtables = new PGtables();
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + pgtables.getMemberTableName());
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + pgtables.getPackageTableName());
        onCreate(sqLiteDatabase);
    }
}