package kr.co.baek.wisethan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper {

    private static final String DATABASE_NAME = "InnerDatabase(SQLite).db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DataBases.CreateDB._CREATE0);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+DataBases.CreateDB._TABLENAME0);
            onCreate(db);
        }
    }

    public DbOpenHelper(Context context){
        this.mCtx = context;
    }

    public DbOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void create(){
        mDBHelper.onCreate(mDB);
    }

    public void close(){
        mDB.close();
    }

    // Insert DB
    public long insertColumn(String hour, String minute , String ampm){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.HOUR, hour);
        values.put(DataBases.CreateDB.MINUTE, minute);
        values.put(DataBases.CreateDB.AMPM, ampm);
        return mDB.insert(DataBases.CreateDB._TABLENAME0, null, values);
    }

    // Update DB
    public boolean updateColumn(long id, String userid, String hour, long minute , String ampm){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.HOUR, hour);
        values.put(DataBases.CreateDB.MINUTE, minute);
        values.put(DataBases.CreateDB.AMPM, ampm);
        return mDB.update(DataBases.CreateDB._TABLENAME0, values, "_id=" + id, null) > 0;
    }

    // Delete All
    public void deleteAllColumns() {
        mDB.delete(DataBases.CreateDB._TABLENAME0, null, null);
    }

    // Delete DB
    public boolean deleteColumn(long id){
        return mDB.delete(DataBases.CreateDB._TABLENAME0, "_id="+id, null) > 0;
    }
    // Select DB
    public Cursor selectColumns(){
        return mDB.query(DataBases.CreateDB._TABLENAME0, null, null, null, null, null, null);
    }

    // sort by column
    public Cursor sortColumn(String sort){
        Cursor c = mDB.rawQuery( "SELECT * FROM usertable ORDER BY " + sort + ";", null);
        return c;
    }

    // sort by column
    public Cursor sortByTimeColumn(){
        Cursor c = mDB.rawQuery( "SELECT * FROM usertable ORDER BY ampm asc, hour asc, minute asc;", null);
        return c;
    }
    // Cursor c = mDB.rawQuery( "SELECT * FROM usertable ORDER BY hour asc, minute asc;", null);

    public Cursor getLatestColumn(int mHour, int mMinute){
        Cursor c = mDB.rawQuery( "SELECT * FROM usertable WHERE "+mHour+"<hour ORDER BY ABS(hour-"+mHour+") asc, ABS(minute-"+mMinute+") asc LIMIT 1;", null);
        return c;
    }
    //"SELECT * FROM usertable WHERE "+mHour+"<hour ORDER BY ABS(hour-mHour) asc, ABS(minute-mMinute) asc LIMIT 1;"
}
