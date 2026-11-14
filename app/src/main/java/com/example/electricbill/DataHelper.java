package com.example.electricbill;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "electricbill.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE = "bills";

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "month TEXT NOT NULL, " +
                "units INTEGER NOT NULL, " +
                "total REAL NOT NULL, " +
                "rebate INTEGER NOT NULL, " +
                "final_cost REAL NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    private ContentValues values(String month, int units, double total, int rebate, double finalCost) {
        ContentValues v = new ContentValues();
        v.put("month", month);
        v.put("units", units);
        v.put("total", total);
        v.put("rebate", rebate);
        v.put("final_cost", finalCost);
        return v;
    }

    public long insertBill(String month, int units, double total, int rebate, double finalCost) {
        return getWritableDatabase().insert(TABLE, null, values(month, units, total, rebate, finalCost));
    }

    public int updateBill(int id, String month, int units, double total, int rebate, double finalCost) {
        return getWritableDatabase().update(TABLE, values(month, units, total, rebate, finalCost),
                "id=?", new String[]{String.valueOf(id)});
    }

    public int deleteBill(int id) {
        return getWritableDatabase().delete(TABLE, "id=?", new String[]{String.valueOf(id)});
    }

    public Cursor getAllBills() {
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE + " ORDER BY id DESC", null);
    }

    public Cursor getBill(int id) {
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE + " WHERE id=?",
                new String[]{String.valueOf(id)});
    }
}

