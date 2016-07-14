package swinlab.chat.chatlayout.LocalDatabase;

import android.os.AsyncTask;

import java.net.URL;

/**
 * Created by GY on 7/6/2016.
 */
public class MessageHistoryDBOperation {
    private class MessageHistoryInsert extends AsyncTask<MessageHistoryDBHelper, Integer, Boolean> {
        protected Boolean doInBackground(MessageHistoryDBHelper... mDBHelper) {
            return true;
        }
    }
}
