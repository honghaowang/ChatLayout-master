package swinlab.chat.chatlayout.LocalDatabase;

/**
 * Created by GY on 7/6/2016.
 */
public class DBconstant {
    // Database constants
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "chatLayout";
    public static final String DATABASE_FILE = "chatLayout.db";

    // Table names
    public static final String MESSAGE_HISTORY_TABLE = "MessageHistory";
    public static final String MOVES_DATA_TABLE = "MovesTable";

    // MESSAGE HISTORY TABLE Attributes
    public static final String MESSAGE_ID = "MessageID";
    public static final String TIME = "Time";
    public static final String MESSAGE = "Message";
    public static final String FROM = "msgFrom";
    public static final String IS_FROM_ME = "isFromMe";

    public static final String TOTAL_STEP = "TotalStep";

    // Constants for creating table
    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String COMMA_SEP = ",";

    // MESSAGE HISTORY TABLE
    public static final String CREATE_MESSAGE_HISTORY_TABLE =
            "CREATE TABLE IF NOT EXISTS " + MESSAGE_HISTORY_TABLE + " (" +
                    MESSAGE_ID + TEXT_TYPE + COMMA_SEP +
                    TIME + INTEGER_TYPE + COMMA_SEP +
                    MESSAGE + TEXT_TYPE + COMMA_SEP +
                    FROM + TEXT_TYPE + COMMA_SEP +
                    IS_FROM_ME + INTEGER_TYPE +
            " )";

    private static final String DELETE_MESSAGE_HISTORY_TABLE =
            "DROP TABLE IF EXISTS " + MESSAGE_HISTORY_TABLE;

    // MOVES TABLE
    public static final String CREATE_MOVES_DATA_TABLE =
            "CREATE TABLE IF NOT EXISTS " + MOVES_DATA_TABLE + " (" +
                    //DATA_ID + INTEGER_TYPE + COMMA_SEP +
                    TIME + TEXT_TYPE + COMMA_SEP +
                    TOTAL_STEP + INTEGER_TYPE + //COMMA_SEP +
                    //DURATION + INTEGER_TYPE + COMMA_SEP +
                    //STEP + INTEGER_TYPE + COMMA_SEP +
                    //ACTIVITY + TEXT_TYPE +
                    " )";

    private static final String DELETE_MOVES_DATA_TABLE =
            "DROP TABLE IF EXISTS " + MOVES_DATA_TABLE;

    // OTHER TABLES ...
}
