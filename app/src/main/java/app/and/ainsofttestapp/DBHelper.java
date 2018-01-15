package app.and.ainsofttestapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Egorman on 15.01.2018.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static String dbName = "database";
    private static String tableName = "products";

    public DBHelper(Context context){
        super(context,dbName,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+tableName+" (" +
                "id integer primary key autoincrement," +
                "name varchar(20)," +
                "price decimal);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static String getDbName() {
        return dbName;
    }

    public static String getTableName() {
        return tableName;
    }
}
