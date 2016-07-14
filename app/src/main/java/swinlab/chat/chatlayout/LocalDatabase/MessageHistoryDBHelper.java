package swinlab.chat.chatlayout.LocalDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by GY on 7/6/2016.
 */
public class MessageHistoryDBHelper extends SQLiteOpenHelper {
    private final String TAG = "MessageHistoryDBHelper";

    public MessageHistoryDBHelper(Context context) {
        super(context, DBconstant.DATABASE_FILE, null, DBconstant.DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBconstant.CREATE_MESSAGE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}

