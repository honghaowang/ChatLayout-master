package swinlab.chat.chatlayout;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Honghao on 7/13/2016.
 */
public class Send2swin03 {
    private String TAG = "Send2swin03";
    /*  json[deviceID, messageID, localTime, message, msgFrom, msgTo, isFromMe]
    **  type = "message" or "moves"
     */
    public Send2swin03(String type, JSONObject json){
        if(type.equals("message")){
            Log.d(TAG, "Set message JSON");
            try {
                String message = "type=" + type
                        + "device=" + json.getString("deviceID")
                        + "messageID=" + json.getString("messageID")
                        + "localTime=" + String.valueOf(json.getInt("localTime"))
                        + "message=" + json.getString("message")
                        + "msgFrom=" + json.getString("msgFrom")
                        + "msgTo=" + json.getString("msgTo")
                        + "isFromMe=" + json.getString("isFromMe");
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        if(type.equals("moves")){

        }
        Log.d(TAG, "Send json");
        new Send().execute(type);
    }

    public class Send extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String...params){
            Log.d(TAG, "send: doInBackground");
            try {
                JSONObject json = new JSONObject();
                json.put("message", params[0]);
                Log.d(TAG, json.toString());

                StringEntity se = new StringEntity(json.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "json"));

                HttpResponse response = null;
                HttpClient client = new DefaultHttpClient();
                String swin03URL = "http://swin03.cs.uml.edu:8080/?" + params[0];

                HttpGet post = new HttpGet(swin03URL);
                post.setHeader("Accept", "json");
                //post.set(se);
                response = client.execute(post);
                Log.e(TAG, String.valueOf(response.getStatusLine().getStatusCode()));
                Log.e(TAG, "---------------------------------------------");
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String webServiceInfo2 = "";

                while ((webServiceInfo2 = rd.readLine()) != null) {
                    System.out.print(webServiceInfo2);
                    Log.e(TAG, webServiceInfo2);
                }
                //Log.e(TAG,response.getEntity().getContent().toString());
                //Log.e(TAG, response.getEntity().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }
}
