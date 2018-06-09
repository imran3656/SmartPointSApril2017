package in.bizzmark.smartpoints_user.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import in.bizzmark.smartpoints_user.R;
import in.bizzmark.smartpoints_user.bo.AcknowledgementBO;
import in.bizzmark.smartpoints_user.sqlitedb.DbHelper;

/**
 * Created by User on 26-Apr-17.
 */

public class RedeemAcknowledgement extends Activity implements View.OnClickListener {
    private TextView tvStoreName,tvNewAmount,tvPoints,tvDiscountAmount;
    private TextView tvcancelMessage;
    private Button btnOk,btnSaveData;

    private RelativeLayout rlStoreName,rlAmount,rlPoints,rlDate;
    private LinearLayout linearLayoutResponse;

    AcknowledgementBO ackBO;
    Gson gson = new Gson();
    String dateandtime;
    String imeistring;
    String result;

    String branchId;
    String storeId;
    String transId;
    String storeName ;
    String billAmount ;
    String type ;
    String discountAmount;
    String newBillAmount;
    String deviceId = imeistring;
    String time;
    String status;
    String redeemPoints;
    String response;

    DbHelper mydb;
    SQLiteDatabase db;

    String store_name_from_sqlite;
    String redeem_points_from_sqlite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.redeem_acknowledgement_new);

        // find All Id
        findViewByAllId();

        // from seller
        Intent i = getIntent();
        result = i.getStringExtra("result");


        ackBO = gson.fromJson(result,AcknowledgementBO.class);
        storeName = ackBO.getStoreName();
        billAmount = ackBO.getBillAmount();
        redeemPoints = ackBO.getPoints();
        type = ackBO.getType();
        time = ackBO.getTime();
        discountAmount = ackBO.getDisCountAmount();
        newBillAmount = ackBO.getNewBillAmount();
        deviceId = ackBO.getDeviceId();
        branchId = ackBO.getBranchId();
        storeId = ackBO.getStoreId();
        status = ackBO.getStatus();
        transId = ackBO.getTransId();
        response = ackBO.getResponse();

        if("success".equalsIgnoreCase(status)){
            // when seller accepting
            tvStoreName.setText(storeName);
            tvNewAmount.setText(newBillAmount);
            tvPoints.setText(redeemPoints);
            tvDiscountAmount.setText(discountAmount);
        }else {
            // Invalid-Response
            if (response != null){
                linearLayoutResponse.setVisibility(View.VISIBLE);
                btnOk.setVisibility(View.VISIBLE);
                tvStoreName.setText(storeName);

                tvcancelMessage.setVisibility(View.GONE);
                rlStoreName.setVisibility(View.GONE);
                rlAmount.setVisibility(View.GONE);
                rlPoints.setVisibility(View.GONE);
                rlDate.setVisibility(View.GONE);

                btnSaveData.setVisibility(View.GONE);
            }else {
                // when seller not accepting
                tvcancelMessage.setVisibility(View.VISIBLE);
                btnOk.setVisibility(View.VISIBLE);
                tvStoreName.setText(storeName);

                rlStoreName.setVisibility(View.GONE);
                rlAmount.setVisibility(View.GONE);
                rlPoints.setVisibility(View.GONE);
                rlDate.setVisibility(View.GONE);

                btnSaveData.setVisibility(View.GONE);
            }
        }

    }

    private void findViewByAllId() {
        tvStoreName = (TextView) findViewById(R.id.tv_store_name_redeem);
        tvNewAmount = (TextView) findViewById(R.id.tv_new_amount_from_seller_redeem);
        tvPoints = (TextView) findViewById(R.id.tv_redeem_points_from_seller);
        tvDiscountAmount = (TextView) findViewById(R.id.tv_discount_amount_from_seller_redeem);
        tvcancelMessage = (TextView) findViewById(R.id.tv_cancel_message);

        linearLayoutResponse = (LinearLayout) findViewById(R.id.ll_response);

        rlStoreName = (RelativeLayout) findViewById(R.id.rl_store_name);
        rlAmount = (RelativeLayout) findViewById(R.id.rl_amount);
        rlPoints = (RelativeLayout) findViewById(R.id.rl_points);
        rlDate = (RelativeLayout) findViewById(R.id.rl_date_time);

        btnOk = (Button) findViewById(R.id.btn_ok);
        btnSaveData = (Button) findViewById(R.id.btn_save_acknowledgement_data);

        btnOk.setOnClickListener(this);
        btnSaveData.setOnClickListener(this);

        // Retrieve Data From SQLite-Database
        retrieveDataFromSQLite();

    }

    // Retrieving data from SQLite-Database
    private void retrieveDataFromSQLite() {
        mydb = new DbHelper(this);
        db = mydb.getReadableDatabase();

/*
        if ( mydb != null) {
            String query = "SELECT STORE_NAME, TOTAL_POINTS FROM CUSTOMER_EARN_REDEEM GROUP BY STORE_NAME";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    store_name_from_sqlite = cursor.getString(0);
                    redeem_points_from_sqlite = cursor.getString(1);
                } while (cursor.moveToNext());
            }
        }
*/
    }

    @Override
    public void onClick(View v) {
        if (v == btnOk){
            finish();
        }else if (v == btnSaveData){
            saveDataIntoSQLiteDatabase();
            startActivity(new Intent(this,PointsActivity.class));
            finish();
        }
    }

    private void saveDataIntoSQLiteDatabase() {
      //  if (type.equalsIgnoreCase("redeem")) {
            // save data into sqlite-database
            DbHelper mydb = new DbHelper(this);
            SQLiteDatabase db = mydb.getWritableDatabase();

            ContentValues cv = new ContentValues();

           // if (store_name_from_sqlite != null && redeem_points_from_sqlite != null){
                if (storeName!=null){

                    int redeem_points = Integer.parseInt(redeemPoints);
                    //int sql_point = Integer.parseInt(redeem_points_from_sqlite);
                   // int total_points = sql_point - redeem_points;
                   // String avail_points = Integer.toString(total_points);

                    cv.put(DbHelper.STORE_NAME_COL_1, storeName);
                    cv.put(DbHelper.TOTAL_POINTS_COL_3, redeemPoints);
                    cv.put(DbHelper.TYPE_COL_4, type);
                    cv.put(DbHelper.DATE_TIME_COL_5, time);
                    cv.put(DbHelper.DEVICE_ID_COL_6, deviceId);
                    cv.put(DbHelper.BRANCH_ID_COL_7, branchId);
                    cv.put(DbHelper.STORE_ID_COL_8, storeId);
                    cv.put(DbHelper.NEW_BILL_AMOUNT_COL_9, newBillAmount);
                    cv.put(DbHelper.DISCOUNT_AMOUNT_COL_10,discountAmount);
                    cv.put(DbHelper.REDEEM_POINTS_COL_12, redeemPoints);
                    cv.put(DbHelper.REDEEM_TRANSACTION_ID_COL_14, transId);

                }
           /* }else {
                Toast.makeText(this, "You don't have enough points to redeem", Toast.LENGTH_LONG).show();
            }*/

            long result = db.insert(DbHelper.TABLE_EARN_REDEEM, null, cv);
            if (result != -1) {
               // Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
              //  Toast.makeText(this, "Data saving error : "+result, Toast.LENGTH_SHORT).show();
            }
       // }
    }
}
