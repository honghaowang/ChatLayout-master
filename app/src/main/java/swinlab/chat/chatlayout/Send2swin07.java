package swinlab.chat.chatlayout;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Created by GY on 7/10/2016.
 */
public class Send2swin07 extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
        String TAG = "Send2swin07";
        String jsonStr = null;
        try {
            Log.d(TAG, "Text--->" + params[0]);
            Log.d(TAG, "User--->" + params[1]);
            HttpResponse response = null;
            HttpClient client = new DefaultHttpClient();
            String swin07URL = "http://swin07.cs.uml.edu:8080/";
            HttpGet get = new HttpGet(swin07URL);
            get.setHeader("User", params[1]);
            get.setHeader("Text", params[0]);
            response = client.execute(get);
            jsonStr = EntityUtils.toString(response.getEntity());
            Log.e(TAG, String.valueOf(response.getStatusLine().getStatusCode()));
            Log.e(TAG, jsonStr);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
