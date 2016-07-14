package swinlab.chat.chatlayout.LocalDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Honghao on 7/10/2016.
 */
public class MovesDBHelper extends SQLiteOpenHelper{
    private final String TAG = "MovesDBHelper";

    public MovesDBHelper(Context context) {
        super(context, DBconstant.DATABASE_FILE, null, DBconstant.DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBconstant.CREATE_MOVES_DATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}