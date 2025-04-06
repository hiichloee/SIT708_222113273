    package com.example.lostfoundmapapp;

    import android.content.*;
    import android.database.Cursor;
    import android.database.sqlite.*;

    public class DBHelper extends SQLiteOpenHelper {

        private static final String DB_NAME = "LostFoundDB";
        private static final int DB_VERSION = 1;
        private static final String TABLE_NAME = "items";

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "type TEXT, name TEXT, phone TEXT, description TEXT, date TEXT, location TEXT, " +
                    "latitude REAL, longitude REAL)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

        public void insertItem(String type, String name, String phone, String description,
                               String date, String location, double latitude, double longitude) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("type", type);
            cv.put("name", name);
            cv.put("phone", phone);
            cv.put("description", description);
            cv.put("date", date);
            cv.put("location", location);
            cv.put("latitude", latitude);
            cv.put("longitude", longitude);
            db.insert(TABLE_NAME, null, cv);
        }

        public Cursor getAllItems() {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        }

        public void deleteItem(int id) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
        }
    }