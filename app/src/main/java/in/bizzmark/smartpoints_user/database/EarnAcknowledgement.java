package in.bizzmark.smartpoints_user.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import in.bizzmark.smartpoints_user.R;
import in.bizzmark.smartpoints_user.bo.AcknowledgementBO;
import in.bizzmark.smartpoints_user.login.LoginActivity;
import in.bizzmark.smartpoints_user.sqlitedb.DbHelper;

import static in.bizzmark.smartpoints_user.NavigationActivity.ACCESS_TOKEN;
import static in.bizzmark.smartpoints_user.NavigationActivity.device_Id;

/**
 * Created by User on 26-Mar-17.
 */

public class EarnAcknowledgement extends Activity implements View.OnClickListener{

    ImageView ivCross;
    Button btnSaveData,btnOk,btnCancel;
    TextView tvstoreName,tvcancelMessage,tvamount,tvpoints, tvP,tvdate;
    RelativeLayout rlStoreName,rlAmount,rlPoints,rlDate;

    AcknowledgementBO ackBO;
    Gson gson = new Gson();
    SimpleDateFormat simpleDateFormat;
    String dateandtime;
    String imeistring;
    String result;

    String branchId;
    String storeId;
    String transId;
    String storeName ;
    String billAmount ;
    String type ;
    String deviceId = device_Id;
    String time;
    String status;
    String earnPoints;

    DbHelper mydb;
    SQLiteDatabase db;

    String store_name_from_sqlite;
    String total_points_from_sqlite;
    String earn_points_sqlite;
    String total_points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earn_acknowledgement_new);

        // find All Id
        findViewByAllId();

        // retrieve access-token from sharedPreferences
        retrievingAccessTokenFromSP();

        // from seller
        Intent i = getIntent();
        result = i.getStringExtra("result");

        ackBO = gson.fromJson(result,AcknowledgementBO.class);
        storeName = ackBO.getStoreName();
        billAmount = ackBO.getBillAmount();
        earnPoints = ackBO.getPoints();
        type = ackBO.getType();
        time = ackBO.getTime();
        deviceId = ackBO.getDeviceId();
        branchId = ackBO.getBranchId();
        storeId = ackBO.getStoreId();
        status = ackBO.getStatus();
        transId = ackBO.getTransId();

        if("success".equalsIgnoreCase(status)){
            // when seller accepting
            tvstoreName.setText(storeName);
            tvamount.setText(billAmount);
            tvpoints.setText(earnPoints);
            tvP.setText(earnPoints+" SmartpointS");
            tvdate.setText(time);
        }else {
            // when seller not accepting
            tvstoreName.setText(storeName);
            tvcancelMessage.setVisibility(View.VISIBLE);
            btnOk.setVisibility(View.VISIBLE);

            rlStoreName.setVisibility(View.GONE);
            rlAmount.setVisibility(View.GONE);
            rlPoints.setVisibility(View.GONE);
            rlDate.setVisibility(View.GONE);

            btnSaveData.setVisibility(View.GONE);
        }

        // getTimeAndDate();
        Calendar calander = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        dateandtime = simpleDateFormat.format(calander.getTime());

        // Retrieve Data From SQLite-Database
        retrieveDataFromSQLite();
    }

    // Retrieving data from SQLite-Database
    private void retrieveDataFromSQLite() {
        mydb = new DbHelper(this);
        db = mydb.getWritableDatabase();

        if ( mydb != null ) {
            String query = "SELECT STORE_NAME, TOTAL_POINTS, EARN_POINTS FROM CUSTOMER_EARN_REDEEM GROUP BY STORE_NAME";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    store_name_from_sqlite = cursor.getString(0);
                    total_points_from_sqlite = cursor.getString(1);
                    earn_points_sqlite = cursor.getString(2);
                } while (cursor.moveToNext());
            }
        }
    }

    // Find All Ids
    private void findViewByAllId() {
        ivCross = (ImageView) findViewById(R.id.iv_cross);
        tvstoreName = (TextView) findViewById(R.id.tv_store_name);
        tvcancelMessage = (TextView) findViewById(R.id.tv_cancel_message);
        tvamount = (TextView) findViewById(R.id.tv_amount_from_seller);
        tvpoints = (TextView) findViewById(R.id.tv_points_from_seller);
        tvP = (TextView) findViewById(R.id.tv_p);
        tvdate = (TextView) findViewById(R.id.tv_date_from_seller);
        btnOk = (Button) findViewById(R.id.btn_ok);
        btnSaveData = (Button) findViewById(R.id.btn_save_acknowledgement_data);

        rlStoreName = (RelativeLayout) findViewById(R.id.rl_store_name);
        rlAmount = (RelativeLayout) findViewById(R.id.rl_amount);
        rlPoints = (RelativeLayout) findViewById(R.id.rl_points);
        rlDate = (RelativeLayout) findViewById(R.id.rl_date_time);

        ivCross.setOnClickListener(this);
        btnSaveData.setOnClickListener(this);
        btnOk.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_cross){
            finish();
        } else if (id == R.id.btn_ok) {
            finish();
        } else if (id == R.id.btn_save_acknowledgement_data) {
            saveDataIntoSQLite();
            if (ACCESS_TOKEN.isEmpty()){
                startActivity(new Intent(this, LoginActivity.class));
            }else {
                startActivity(new Intent(this, PointsActivity.class));
                finish();
            }
        }
    }

    // SAVE DATA INTO SQLite-DB
    private void saveDataIntoSQLite() {
       // if (type.equalsIgnoreCase("earn")) {
            // save data into sqlite-database
            mydb = new DbHelper(this);
            db = mydb.getWritableDatabase();

            ContentValues cv = new ContentValues();

            if (store_name_from_sqlite != null && total_points_from_sqlite != null) {

                int earn_point = Integer.parseInt(earnPoints);
                int sql_point = Integer.parseInt(total_points_from_sqlite);
                int totl_points = earn_point + sql_point;
                total_points = Integer.toString(totl_points);

                if (store_name_from_sqlite.equalsIgnoreCase(storeName)) {

                    cv.put(DbHelper.STORE_NAME_COL_1, store_name_from_sqlite);
                    cv.put(DbHelper.BILL_AMOUNT_COL_2, billAmount);
                    cv.put(DbHelper.TOTAL_POINTS_COL_3, total_points);
                    cv.put(DbHelper.TYPE_COL_4, type);
                    cv.put(DbHelper.DATE_TIME_COL_5, dateandtime);
                    cv.put(DbHelper.DEVICE_ID_COL_6, deviceId);
                    cv.put(DbHelper.BRANCH_ID_COL_7, branchId);
                    cv.put(DbHelper.STORE_ID_COL_8, storeId);
                    cv.put(DbHelper.EARN_POINTS_COL_11, earnPoints);
                    cv.put(DbHelper.EARN_TRANSACTION_ID_COL_13, transId);

                }else {
                    cv.put(DbHelper.STORE_NAME_COL_1, storeName);
                    cv.put(DbHelper.BILL_AMOUNT_COL_2, billAmount);
                    cv.put(DbHelper.EARN_POINTS_COL_11, earnPoints);
                    cv.put(DbHelper.TYPE_COL_4, type);
                    cv.put(DbHelper.DATE_TIME_COL_5, dateandtime);
                    cv.put(DbHelper.DEVICE_ID_COL_6, deviceId);
                    cv.put(DbHelper.BRANCH_ID_COL_7, branchId);
                    cv.put(DbHelper.STORE_ID_COL_8, storeId);
                    //cv.put(DbHelper.TOTAL_POINTS_COL_3, total_points);
                    cv.put(DbHelper.TOTAL_POINTS_COL_3, earnPoints);
                    cv.put(DbHelper.EARN_TRANSACTION_ID_COL_13, transId);
                }
            }else {
                cv.put(DbHelper.STORE_NAME_COL_1, storeName);
                cv.put(DbHelper.BILL_AMOUNT_COL_2, billAmount);
                cv.put(DbHelper.TOTAL_POINTS_COL_3, earnPoints);
                cv.put(DbHelper.TYPE_COL_4, type);
                cv.put(DbHelper.DATE_TIME_COL_5, dateandtime);
                cv.put(DbHelper.DEVICE_ID_COL_6, deviceId);
                cv.put(DbHelper.BRANCH_ID_COL_7, branchId);
                cv.put(DbHelper.STORE_ID_COL_8, storeId);
                cv.put(DbHelper.EARN_POINTS_COL_11, earnPoints);
                cv.put(DbHelper.EARN_TRANSACTION_ID_COL_13, transId);
            }

            long result = db.insert(DbHelper.TABLE_EARN_REDEEM, null, cv);
            if (result != -1) {
               // Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show();
               finish();
            } else {
             //   Toast.makeText(this, "Saving error : "+result, Toast.LENGTH_SHORT).show();
            }

       // }
    }

    private String getTimeAndDate() {
        Calendar calander = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        dateandtime = simpleDateFormat.format(calander.getTime());
        return dateandtime;
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrievingAccessTokenFromSP();
    }

    @Override
    protected void onPause() {
        super.onPause();
        retrievingAccessTokenFromSP();
    }

    // retrieve access-token from sharedPreferences
    private void retrievingAccessTokenFromSP() {
        SharedPreferences sp = this.getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE);
        ACCESS_TOKEN = sp.getString("access_token", "");
    }
}
