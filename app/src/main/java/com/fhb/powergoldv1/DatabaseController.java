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
    public static final Integer DB_VERSION = 19;

    // Get all the tables property
    public PGtables pgTables = new PGtables();
    public String pgAuthParam;
    public DatabaseController(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // build sql string for table schemas

        // Only create authentication tables during first login
        String auth_table_schema = createTableSQLstring(pgTables.authSchema);
        String member_table_schema = createTableSQLstring(pgTables.membersSchema);
        String package_table_schema = createTableSQLstring(pgTables.packageSchema);
        String weight_table_schema = createTableSQLstring(pgTables.weightSchema);
        String rate_table_schema = createTableSQLstring(pgTables.rateSchema);

        // create tables
        // Only create authentication tables during first login
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + pgTables.authTableName + " (" + auth_table_schema + ");");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + pgTables.memberTableName + " (" + member_table_schema + ");");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + pgTables.packageTableName + " (" + package_table_schema + ");");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + pgTables.weightTableName + " (" + weight_table_schema + ");");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + pgTables.rateTableName + " (" + rate_table_schema + ");");

        // insert data for user authentication into AUTHENTICATION table


        //  insert data for into package table
        List<String[]> pkg_data = pgTables.setPackageData(pgTables.packageSchema);
        for (int i=0; i<pkg_data.size(); i++ ) {
            try {
                // onCreate automatic run when database not found and already have all the database
                // functionality. Don't call getWritable() or getReadable() again to avoid error
                // "getDatabase called recursively". Use sqLiteDatabase.insert() directly from here.

                // List<String[]> pkg_data to ContentValues to use saLiteDatabase.insert()
                ContentValues contentValues = (ContentValues) convertToContentvalues(pgTables.packageSchema, pkg_data.get(i));

                sqLiteDatabase.insertOrThrow(pgTables.packageTableName, null, contentValues);
            } catch (Exception e) {
                Log.d("FHB", e.toString());
            }
        }

        // insert data for gold weight table
        String[] weight_data = pgTables.setWeightData(pgTables.weightSchema);
        ContentValues weightContentValues = (ContentValues) convertToContentvalues(pgTables.weightSchema, weight_data);
        sqLiteDatabase.insertOrThrow(pgTables.weightTableName, null, weightContentValues);
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

    /**
     * query member extract rows from database and return List of Arraylist of each element in row
     * @return List of String array
     */
    public List<List<String>> query_member() {

        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + pgTables.memberTableName, null);

        List<List<String>> arr_arr = new ArrayList<>();
        Integer i;
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
        Map<String, String> members_schema = pgTables.membersSchema;

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
        int rowUpdated = db.update(pgTables.memberTableName, contentValues, where_str, null);
        // long result = db.insert(MEMBERS_TBL, null, contentValues);

        // Close the database
        db.close();

        return rowUpdated != -1;
    }

    public boolean delete_member(Integer name_ID) {
        // Open database connection. Remember to close by calling close()
        SQLiteDatabase db = this.getWritableDatabase();

        String where_str = "ID=" + name_ID;
        int rowDeleted = db.delete(pgTables.memberTableName, where_str, null);

        // Close the database
        db.close();

        return rowDeleted != -1;
    }

    public String getAuthParam () {

        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT PG_WEBPARAM FROM " + pgTables.authTableName, null);
        // Since there is only one value querry interation is not required. Value in cursor.getString(0)
        pgAuthParam = getOneValue(cursor, 1);

        return pgAuthParam;
    }

    public boolean isTableExists(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst())
        {
            return false;
        }

        // count the record
        cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
        if (!cursor.moveToFirst())
        {
            return false;
        }

        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count > 0;
    }

    public void createAuthTable () {
        String auth_table_schema = createTableSQLstring(pgTables.authSchema);

        // create tables
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS " + pgTables.authTableName + " (" + auth_table_schema + ");");

        db.close();
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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + pgTables.authTableName);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + pgTables.memberTableName);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + pgTables.packageTableName);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + pgTables.weightTableName);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + pgTables.rateTableName);
        onCreate(sqLiteDatabase);
    }

    public String getOneValue (Cursor cursor, int col_index){
        Integer i;
        Integer cols = cursor.getColumnCount();
        List<String> arr = new ArrayList<>(); // this is the way to declare at the same time reset arr to clear content insted of arr.clear() to store new element or array.
        while (cursor.moveToNext()) {
            for (i = 0; i < cols; i++) {
                arr.add(cursor.getString(i));
            }
        }
        return arr.get(col_index - 1);
    }
}