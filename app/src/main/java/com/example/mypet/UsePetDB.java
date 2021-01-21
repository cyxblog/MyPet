package com.example.mypet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class UsePetDB {

    final String TAG = "XING";

    SQLiteOpenHelper dbHandler;
    SQLiteDatabase db;

    private  static final String[] columns = {
            PetDB.MODE,
            PetDB.NAME,
            PetDB.AGE
    };

    public UsePetDB(Context context) {
        dbHandler = new PetDB(context);
    }

    public void open() {
        db = dbHandler.getWritableDatabase();
    }

    public void close() {
        dbHandler.close();
    }

    public void addPet(String mode, String name, long age) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PetDB.MODE, mode);
        contentValues.put(PetDB.NAME, name);
        contentValues.put(PetDB.AGE, age);
        db.insert(PetDB.TABLE_NAME, null, contentValues);
    }

    public void updatePet( String name, long age) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PetDB.AGE, age);
        db.update(PetDB.TABLE_NAME, contentValues,
                PetDB.NAME + "=?", new String[] {name});
    }

    public long getAge(String name) {
        Cursor cursor = db.query(PetDB.TABLE_NAME, columns, "name=?", new String[]{name}, null, null, null);
        int age = 0;
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                age = cursor.getInt(cursor.getColumnIndex(PetDB.AGE));
            }
            cursor.close();
        }
        return age;
    }

    public List<Pet> getAllPet() {

        List<Pet> petList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + PetDB.TABLE_NAME, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Pet pet = new Pet();
                pet.setPetMode(cursor.getString(0));
                pet.setPetName(cursor.getString(1));
                pet.setPetAge(cursor.getLong(2));
                petList.add(pet);
            }
            cursor.close();
        }

        return petList;
    }

    public String getMode(String name) {
        Cursor cursor = db.query(PetDB.TABLE_NAME, columns, "name=?", new String[]{name}, null, null, null);
        String tmp_mode = "";
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                tmp_mode = cursor.getString(cursor.getColumnIndex(PetDB.MODE));
            }
            cursor.close();
        }
        return tmp_mode;
    }

    public void removePet(String name) {
        db.delete(PetDB.TABLE_NAME, "name=?", new String[]{name});
    }
}