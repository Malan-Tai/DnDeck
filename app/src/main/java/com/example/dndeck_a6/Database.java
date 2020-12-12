package com.example.dndeck_a6;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.dndeck_a6.activities.MainActivity;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "saves";
    public static final String PKEY = "pkey";
    public static final String COL1 = "col1";

    public Database(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String DATABASE_TABLE_CREATE = "CREATE TABLE " + DATABASE_NAME + " (" + PKEY + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL1 + " TEXT);";
        db.execSQL(DATABASE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DATABASE_NAME;

        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void insertData(long id, String s)
    {
        Log.i("Malan", "inserting : " + id + " ; " + s);
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(COL1, s);

        if (id != -1) {
            values.put(PKEY, id);
            deleteSave(id);
        }
        MainActivity.currentSave.id = db.insertOrThrow(DATABASE_NAME,null, values);

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public ArrayList<String>[] readData()
    {
        ArrayList<String> res = new ArrayList<String>();
        ArrayList<String> ids = new ArrayList<>();

        String select = new String("SELECT * from " + DATABASE_NAME);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(select, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                res.add(cursor.getString(cursor.getColumnIndex(COL1)));
                ids.add(cursor.getString(cursor.getColumnIndex(PKEY)));
                Log.i("Malan", "Reading: " + cursor.getString(cursor.getColumnIndex(PKEY)) + " ; "+ cursor.getString(cursor.getColumnIndex(COL1)));
            } while (cursor.moveToNext());
        }

        return new ArrayList[] { res, ids };
    }

    public void deleteSave(long id){
        Log.i("Malan", "deleting : " + id);
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        db.delete(DATABASE_NAME, PKEY + "=" + id, null);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

}
