package com.hfad.expensemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "register.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    public void insertRegistration(String name, String password) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues userAccount = new ContentValues();
        userAccount.put("USER_NAME", name);
        userAccount.put("PASSWORD", password);
        db.insert("REGISTER_USER", null,
                userAccount);
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("REGISTER_USER",
                new String[] {"USER_NAME"},
                "USER_NAME = ? and PASSWORD = ?",
                new String[] {username, password},
                null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL("CREATE TABLE REGISTER_USER (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "USER_NAME TEXT, "
                    + "PASSWORD TEXT);");
        }
        if (oldVersion < 2) {

        }
    }
}
