package com.example.mainactivity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.lang.UCharacter;


import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1; //версия бд
    public static final String DATABASE_NAME = "contactDb";
    public static final String TABLE_CONTACTS = "contacts"; //имя таблицы
    public static final String TABLE_SUBJECT = "subject"; //имя таблицы
    public static final String TABLE_DATESTUDENT = "date"; //имя таблицы

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_CHIPID = "chipId";
    public static final String KEY_DATE = "date";
    public static final String KEY_SUBJECT = "subject";


    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_CONTACTS + "(" + KEY_ID + " integer primary key," + KEY_NAME + " text," + KEY_CHIPID + " text" + ")");
        db.execSQL("create table " + TABLE_SUBJECT + "(" + KEY_ID + " integer primary key," + KEY_NAME + " text" + ")");
        //db.execSQL("create table " + TABLE_DATESTUDENT + "(" + KEY_ID + " integer primary key," + KEY_DATE + " text"  ")");
        db.execSQL("create table " + TABLE_DATESTUDENT + "(" + KEY_ID + " integer primary key," + KEY_DATE + " text," + KEY_CHIPID + " text," + KEY_SUBJECT + " text" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_CONTACTS);
        db.execSQL("drop table if exists " + TABLE_SUBJECT);
        db.execSQL("drop table if exists " + TABLE_DATESTUDENT);
        onCreate(db);
    }
}
