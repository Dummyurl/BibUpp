package com.bibupp.androidvantage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    public DBHelper(Context context) {
        super(context, "MyDBName.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table quiz" +
                        "(id integer primary key, quiz_id text, quiz_title text, quiz_date text,chossed_ans text)"
        );
}


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insert(long QuizId, String QuizTitle, String QuizDate, String chooseAns) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quiz_id", QuizId);
        values.put("quiz_title", QuizTitle);
        values.put("quiz_date", QuizDate);
        values.put("chossed_ans", chooseAns);
        db.insert("quiz", null, values);
        return true;
    }

    public Cursor getallQuiz() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from quiz", null);
        return res;
    }

    public Cursor getQuizById(String Id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from quiz where quiz_id=" + Id, null);
        return res;
    }

    public void delete() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from quiz");
    }
}