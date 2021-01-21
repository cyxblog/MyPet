package com.example.mypet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class PetDB extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "pet";
    public static final String NAME = "name";
    public static final String AGE = "age";
    public static final String MODE = "mode";

    public PetDB(Context context) {
        super(context, "pet", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建数据库
        String sql = "create TABLE "
                + TABLE_NAME
                + "("
                + MODE + " varchar(20),"
                + NAME + " varchar(20) primary key not null,"
                + AGE + " integer not null)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
