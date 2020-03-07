package com.example.mygrocerylist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GLDBHelper extends SQLiteOpenHelper {

    // Logcat Tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // database Name
    private static final String DATABASE_NAME = "myGroceryList";

    // Table Names
    private static final String TABLE_Store = "store";
    private static final String TABLE_Item = "item";
    private static final String TABLE_List = "list";

    //Table Create Statements

        // Item table create Statement
    private static final String CREATE_TABLE_ITEM = "CREATE TABLE item ("
                + "id INTEGER PRIMARY KEY, item_name text not null, item_code text, "
                + "item_quantity text, item_price text, item_photo blob);";



        // List table create Statement
    private static final String CREATE_TABLE_LIST = "CREATE TABLE list ("
                + "id INTEGER PRIMARY KEY autoincrement, list_name text not null," +
                "created_at datetime);";

        //Store table create statement

    private static final String CREATE_TABLE_STORE =
            "create table store (_id integer primary key, "
    + "storename text not null);";


    public GLDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_STORE);
        db.execSQL(CREATE_TABLE_ITEM);
        db.execSQL(CREATE_TABLE_LIST);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS item");
        db.execSQL("DROP TABLE IF EXISTS list");
        db.execSQL("DROP TABLE IF EXISTS store");

        //create new tables
        onCreate(db);


    }






}
