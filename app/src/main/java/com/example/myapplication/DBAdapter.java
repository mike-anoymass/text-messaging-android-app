package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
    static final String M_ID = "_id";

    static final String M_CONTACT = "contact";
    static final String M_NAME = "name";
    static final String TAG = "DBAdapter";
    static final String DB_NAME = "letschill";
    static final String TABLE_NAME = "messages";
    static final int DB_VERSION = 1;

    static final String CREATE_MEMBER_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            M_ID + " integer primary key autoincrement, " + M_NAME +
            " text not null ," + M_CONTACT + " text not null);";

    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context c) {
        this.context = c;
        //DBHelper = new DatabaseHelper(context);
    }

    public DBAdapter open() throws SQLException {
        DBHelper = new DatabaseHelper(context);
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DBHelper.close();
    }

    public long insertMessage(String name, String contact) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(M_NAME, name);
        initialValues.put(M_CONTACT, contact);
        return db.insert(TABLE_NAME, null, initialValues);
    }

    public boolean deleteMessage(long id) {
        return db.delete(TABLE_NAME, M_ID + "=" + id, null) > 0;
    }

    public Cursor getAllMessages() {
        return db.query(TABLE_NAME, new String[]{M_ID, M_NAME, M_CONTACT}, null,
                null, null, null, null);
    }

    public Cursor getMessage(long id) {
        Cursor mCursor = db.query(true, TABLE_NAME, new String[]{M_ID, M_NAME, M_CONTACT},
                M_ID + "=" + id, null, null, null, M_NAME, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    public boolean updateMessage(long id, String name, String contact) {
        ContentValues args = new ContentValues();
        args.put(M_NAME, name);
        args.put(M_CONTACT, contact);
        return db.update(TABLE_NAME, args, M_ID + "=" + id, null) > 0;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_MEMBER_TABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
                    newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
