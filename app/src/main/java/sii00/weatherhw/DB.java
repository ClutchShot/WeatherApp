package sii00.weatherhw;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DB extends SQLiteOpenHelper {

    private static String TableName = "cities";
    private static String DBName = "test.db";
    private static int DBVersion = 1;
    private Context context;
    private SQLiteDatabase database;
    public static DB db;
    private String createDBSql =
            "create table cities(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "city TEXT NOT NULL, lon REAL, lat REAL);";

    public DB( Context context) {
        super(context, TableName, null, DBVersion);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(createDBSql);
    }

    public static synchronized DB getInstance(Context context){
        if(db == null){
            db = new DB(context);
        }
        return db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public void insertData(String[] keys, String[] values){
        ContentValues contentValues = new ContentValues();
        for(int i = 0; i<keys.length; i++){
            contentValues.put(keys[i], values[i]);
        }
        database = getWritableDatabase();
        database.insert(TableName, null, contentValues);
    }

    public void deleteDataById(int id) {
        String[] args = {String.valueOf(id)};
        database = getWritableDatabase();
        database.delete(TableName, "id=?", args);
    }

    public Map<String, Double> getCityLocationById(int id){
        Map<String, Double> map = new HashMap<>();
        database = getReadableDatabase();
        Cursor cursor  = database.query(TableName, new String[]{"lon", "lat"},"id = ?",
                new String[]{String.valueOf(id)}, null,null, null, null  );
        if (cursor != null)
            cursor.moveToFirst();
        map.put("lon",cursor.getDouble(cursor.getColumnIndex("lon")));
        map.put("lat",cursor.getDouble(cursor.getColumnIndex("lat")));
        return  map;
    }

    public List<Map<String, Object>> queryAllCities(){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        database = getReadableDatabase();
        Cursor cursor = database.query(TableName, null, null, null, null, null, null, null);
        while(cursor.moveToNext()){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", cursor.getInt(cursor.getColumnIndex("id")));
            map.put("city", cursor.getString(cursor.getColumnIndex("city")));
            list.add(map);
        }
        return list;
    }
}
