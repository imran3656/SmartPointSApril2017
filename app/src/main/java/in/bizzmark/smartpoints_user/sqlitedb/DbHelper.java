package in.bizzmark.smartpoints_user.sqlitedb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 28-Mar-17.
 */

public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "CUSTOMER_DATA";
    public static final int VERSION = 1;

    // CUSTOMER EARN-REDEEM TABLE
    public static final String TABLE_EARN_REDEEM = "CUSTOMER_EARN_REDEEM";

    public static final String STORE_NAME_COL_1 = "STORE_NAME";
    public static final String BILL_AMOUNT_COL_2 = "BILL_AMOUNT";
    public static final String TOTAL_POINTS_COL_3 = "TOTAL_POINTS";
    public static final String TYPE_COL_4 = "TYPE";
    public static final String DATE_TIME_COL_5 = "DATE_TIME";
    public static final String DEVICE_ID_COL_6 = "DEVICE_ID";
    public static final String BRANCH_ID_COL_7 = "BRANCH_ID";
    public static final String STORE_ID_COL_8 = "STORE_ID";
    public static final String NEW_BILL_AMOUNT_COL_9 = "NEW_BILL_AMOUNT";
    public static final String DISCOUNT_AMOUNT_COL_10 = "DISCOUNT_AMOUNT";
    public static final String EARN_POINTS_COL_11 = "EARN_POINTS";
    public static final String REDEEM_POINTS_COL_12 = "REDEEM_POINTS";
    public static final String EARN_TRANSACTION_ID_COL_13 = "EARN_TRANSACTION_ID";
    public static final String REDEEM_TRANSACTION_ID_COL_14 = "REDEEM_TRANSACTION_ID";

    public static final String STORE_ID="STORE_ID";
    public static final String BRANCH_NAME="BRANCH_NAME";
    public static final String STORE_NAME="STORE_NAME";
    public static final String TOTAL_POINTS="TOTAL_POINTS";


    public static final String TABLE_STORES="STORES_LIST";





    Context context;
    public DbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table " + TABLE_EARN_REDEEM + " ( "+STORE_NAME_COL_1+" TEXT, "+BILL_AMOUNT_COL_2+" TEXT, "+TOTAL_POINTS_COL_3+" TEXT, "+
                TYPE_COL_4+" TEXT, "+DATE_TIME_COL_5+" TEXT, "+DEVICE_ID_COL_6+" TEXT, "+BRANCH_ID_COL_7+" TEXT, "+STORE_ID_COL_8+" TEXT, "+NEW_BILL_AMOUNT_COL_9+ " TEXT, "+
                DISCOUNT_AMOUNT_COL_10+" TEXT, "+EARN_POINTS_COL_11+" TEXT, "+REDEEM_POINTS_COL_12+" TEXT, "+EARN_TRANSACTION_ID_COL_13+" TEXT, "+
                REDEEM_TRANSACTION_ID_COL_14+" TEXT)";

        //String STORES_LIST="create table " +TABLE_STORES+" ( "+


        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EARN_REDEEM);
        onCreate(db);
    }
}
