package swinlab.chat.chatlayout;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.midhunarmid.movesapi.MovesAPI;
import com.midhunarmid.movesapi.MovesHandler;
import com.midhunarmid.movesapi.auth.AuthData;
import com.midhunarmid.movesapi.util.MovesStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import swinlab.chat.chatlayout.LocalDatabase.DBconstant;
import swinlab.chat.chatlayout.LocalDatabase.MessageHistoryDBHelper;
import swinlab.chat.chatlayout.LocalDatabase.MovesDBHelper;

public class ChatActivity extends ActionBarActivity {

    private static final String TAG = "ChatActivity";
    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    private String deviceID;
    private String android_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        SetPermission mPermission = new SetPermission(this, this);
        mPermission.addPermissionList("android.permission.WRITE_EXTERNAL_STORAGE");
        mPermission.getPermission();

        setContentView(R.layout.activity_chat);
        deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        initControls();     // Message controller
        initMoves();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        TextView meLabel = (TextView) findViewById(R.id.meLbl);
        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        companionLabel.setText("Food Agent");

        loadHistory();
        loadDummyHistory();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                createMessage(deviceID, messageText, true);

                String Response;
                if(messageText.equals("step") || messageText.equals("Step")){
                    Response = getSteps();
                } else {
                    Response = getFoodResponse(messageText);
                }

                createMessage(deviceID, Response, false);

                messageET.setText("");
            }
        });

    }

    public void createMessage(String userID, String messageText, Boolean isMe) {
        Date currentDate = new Date();
        long time = currentDate.getTime();
        String ID;

        if(isMe) {
            ID = userID + "Me" + String.valueOf(time);
        } else {
            ID = userID + "Agent" + String.valueOf(time);
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(ID);
        chatMessage.setMessage(messageText);
        chatMessage.setDate(DateFormat.getDateTimeInstance().format(currentDate));
        chatMessage.setMe(isMe);

        // Save to Message History Database
        MessageHistoryDBHelper mDBHelper = new MessageHistoryDBHelper(getApplicationContext());
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DBconstant.MESSAGE_ID, ID);
        values.put(DBconstant.FROM, userID);
        values.put(DBconstant.TIME, time);
        values.put(DBconstant.MESSAGE, messageText);
        values.put(DBconstant.IS_FROM_ME, isMe);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                DBconstant.MESSAGE_HISTORY_TABLE,
                null,
                values);

        if (newRowId == -1) {
            Log.e(TAG, "Fail database insertion");
        }

        displayMessage(chatMessage);
        scroll();
    }



    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadHistory() {

        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        MessageHistoryDBHelper mDBHelper = new MessageHistoryDBHelper(getApplicationContext());
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                DBconstant.MESSAGE_ID,
                DBconstant.FROM,
                DBconstant.TIME,
                DBconstant.MESSAGE,
                DBconstant.IS_FROM_ME
        };

        String sortOrder = DBconstant.TIME + " ASC";

        Cursor mCursor = db.query(
                DBconstant.MESSAGE_HISTORY_TABLE,       // The table to query
                projection,                             // The columns to return
                null,                                   // The columns for the WHERE clause
                null,                                   // The values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row groups
                sortOrder,                              // The sort order
                "20"
        );

        Log.d(TAG, "number of history: " + mCursor.getCount());
        if(mCursor == null) {
            Log.e(TAG, "No history");
            return;
        }
        mCursor.moveToFirst();
        for(int i = 0; i < mCursor.getCount(); i++){
            String ID = mCursor.getString(mCursor.getColumnIndexOrThrow(DBconstant.MESSAGE_ID));
            long messageDate = mCursor.getLong(mCursor.getColumnIndexOrThrow(DBconstant.TIME));
            String messageText = mCursor.getString(mCursor.getColumnIndexOrThrow(DBconstant.MESSAGE));
            int isFromMe = mCursor.getInt(mCursor.getColumnIndexOrThrow(DBconstant.IS_FROM_ME));
            Boolean isMe = (isFromMe == 1);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setId(ID);
            chatMessage.setMessage(messageText);
            chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date(messageDate)));
            chatMessage.setMe(isMe);

            displayMessage(chatMessage);
            mCursor.moveToNext();
        }
        scroll();

    }

    private void loadDummyHistory() {

        chatHistory = new ArrayList<ChatMessage>();

        //Set message from agent
        ChatMessage msg = new ChatMessage();
        msg.setId("Agent1");
        msg.setMe(false);
        msg.setMessage("Hi");
        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg);
        ChatMessage msg1 = new ChatMessage();
        msg1.setId("Agent2");
        msg1.setMe(false);
        msg1.setMessage("What did you eat?");

        //send --> start
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deviceID", android_id);
            jsonObject.put("messageID", "Agent2");
            jsonObject.put("localTime", Calendar.getInstance().getTime());
            jsonObject.put("message", "What did you eat?");
            jsonObject.put("msgFrom", "agent");
            jsonObject.put("msgTo", android_id);
            jsonObject.put("isFromMe", 0);
        } catch (JSONException e){
            e.printStackTrace();
        }
        new Send2swin03("message", jsonObject);
        //send --> end
        
        msg1.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg1);

        for (int i = 0; i < chatHistory.size(); i++) {
            ChatMessage message = chatHistory.get(i);
            displayMessage(message);
        }

    }

    private void initMoves(){
        try {
            MovesAPI.init(getApplicationContext(), Constant.CLIENT_ID, Constant.clientSecret, Constant.MOVES_SCOPES, Constant.REDIRECT_URI);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MovesHandler<AuthData> authDialogHandler = new MovesHandler<AuthData>() {
            @Override
            public void onSuccess(AuthData result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChatActivity.this, "Authenticated Successfully", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(MovesStatus status, String message) {
                Log.e(TAG, "Request Failed! \n"
                        + "Status Code : " + status + "\n"
                        + "Status Message : " + message + "\n\n"
                        + "Specific Message : " + status.getStatusMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(ChatActivity.this, "Authentication Fail", Toast.LENGTH_LONG).show();
                    }
                });
            }
        };
        AuthData auth = MovesAPI.getAuthData();
        if(auth == null) {
            MovesAPI.authenticate(authDialogHandler, ChatActivity.this);
        }
        Intent intent = new Intent(this, MovesLogService.class);
        startService(intent);
    }

    private String getSteps(){
        MovesDBHelper mDBHelper = new MovesDBHelper(getApplicationContext());
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        db.execSQL(DBconstant.CREATE_MOVES_DATA_TABLE);

        Cursor mCursor = db.query(
                DBconstant.MOVES_DATA_TABLE,            // The table to query
                null,                                   // The columns to return
                null,                                   // The columns for the WHERE clause
                null,                                   // The values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row groups
                null                                    // The sort order
        );
        mCursor.moveToLast();
        String steps = null;
        try {
            steps = String.valueOf(mCursor.getInt(mCursor.getColumnIndexOrThrow(DBconstant.TOTAL_STEP)));
        } catch (NullPointerException e){
            e.printStackTrace();
            steps = "0";
        }
        return "You reached " + steps + "steps";
    }

    private String getFoodResponse(String messageText){
        String res = null;
        String brand = null, food = null;
        try {
            res = new Send2swin07().execute(messageText, deviceID).get();
            if(res == null){
                return "Server error. Please try again later.";
            }
            JSONObject jsonObject = new JSONObject(res);
            if(jsonObject.has("Brand")){
                brand = jsonObject.getString("Brand");
            }
            if(jsonObject.has("Food")){
                food = jsonObject.getString("Food");
            }
            res = "Food: " + food + "\n" + "Brand: " + brand;
        } catch (InterruptedException e){
            e.printStackTrace();
        } catch (ExecutionException e){
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        }

        return res;
    }

}
