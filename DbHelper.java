package com.example.ahmed.syncserver;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ahmed on 7/10/2017.
 */

public class DbHelper extends SQLiteOpenHelper {
    private static volatile DbHelper dbinstance;
    private final SQLiteDatabase db;


    private DbHelper(Context c)
    {
        super(c,DbContract.DATABASE_NAME, null, DbContract.DATABASE_VERSION);
        this.db = getWritableDatabase();
            }

    /**
     * We use a Singleton to prevent leaking the SQLiteDatabase or Context.
     * @return {@link DbHelper}
     */
    public static DbHelper getInstance(Context c){
        if(dbinstance==null)
        {
            synchronized (DbHelper.class){
                if(dbinstance==null)
                {
                    dbinstance = new DbHelper(c);
                }
            }
        }
        return dbinstance;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        CREATE_TABLE(db);
    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS [" + DbContract.TABLE_NAME + "];");
    }
    private void CREATE_TABLE(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }
    private  static final String CREATE_TABLE = "create table "+DbContract.TABLE_NAME + " ("
            + DbContract.Users.COL_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DbContract.Users.COL_NAME+ " TEXT,"
            + DbContract.Users.COL_LINK+ " TEXT,"
            + DbContract.Users.COL_EMAIL+ " TEXT,"
            + DbContract.Users.COL_PASSWORD+ " TEXT );";

    public SQLiteDatabase getDb()
    {
        return db;
    }
}
