package com.example.avkor.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by avkor on 25.11.2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "LoginDb";
    public static final String TABLE_LOGIN = "Login";
    public static final String TABLE_FI = "Fi";


    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_NM = "nm";
    public static final String KEY_FM = "fm";
    public static final String KEY_AVATAR = "avatar";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_LOGIN + "("
                + KEY_ID + " integer primary key,"
                + KEY_NAME + " text,"
                + KEY_PASSWORD + " text" + ")");

        db.execSQL("create table " + TABLE_FI + "("
                + KEY_ID + " integer primary key,"
                + KEY_NM + " text,"
                + KEY_FM + " text,"
                + KEY_AVATAR + " text,"
                + "FOREIGN KEY (" + KEY_ID + ") REFERENCES " + TABLE_LOGIN + "(" + KEY_ID + ")" + ")"
        );


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + TABLE_LOGIN);
        db.execSQL("drop table if exists " + TABLE_FI);

        onCreate(db);

    }
    public void insertOnTableFI(String name, String secondname, String avatar, int id) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_NM, name);
        contentValues.put(KEY_FM, secondname);
        contentValues.put(KEY_AVATAR, avatar);
        contentValues.put(KEY_ID, id);
        database.insertWithOnConflict(DBHelper.TABLE_FI, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        this.close();

    }

    public boolean registration(String name, String password) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_NAME, name);
        contentValues.put(KEY_PASSWORD, password);

        Cursor cursor = database.query(DBHelper.TABLE_LOGIN, null, KEY_NAME + "=?",
                new String[]{name}, null, null, null);
        if (cursor.getCount() == 0) {
            database.insert(DBHelper.TABLE_LOGIN, null, contentValues);
            this.close();
            return true;
        }
        else return false;

    }
}
