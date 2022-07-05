package com.eqmonterozo.capture;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {


    private  static final String TABLE_NAME = "places";
    private static  final String ID = "id";
    private static  final String PLACE_NAME = "place_name";
    private static  final String DESCRIPTION = "description";
    private static  final String IMAGES = "images";
    private static  final String LATITUDE = "latitude";
    private static  final String LONGITUDE = "longitude";
    private static  final String ADDRESS = "address";
    private static  final String TIMESTAMP = "timestamp";
    private static  final String ADDED_BY = "added_by";

    public  DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PLACE_NAME + " TEXT," +
                DESCRIPTION + " TEXT," +
                IMAGES + " TEXT," +
                LATITUDE + " TEXT," +
                LONGITUDE + " TEXT," +
                ADDRESS + " TEXT," +
                TIMESTAMP + " DEFAULT CURRENT_TIMESTAMP," +
                ADDED_BY + " TEXT)";
        System.out.println("createtable" + createTable);
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean addPlace(String placeName, String description, ArrayList<String> images,
                            Double latitude, Double longitude, String address, Integer addedBy) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PLACE_NAME, placeName);
        contentValues.put(DESCRIPTION, description);
        contentValues.put(IMAGES, String.valueOf(images));
        contentValues.put(LATITUDE, latitude);
        contentValues.put(LONGITUDE, longitude);
        contentValues.put(ADDRESS, address);
        contentValues.put(ADDED_BY, addedBy);


        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Cursor getPlaces() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }

    public void delete() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_NAME);
    }
}
