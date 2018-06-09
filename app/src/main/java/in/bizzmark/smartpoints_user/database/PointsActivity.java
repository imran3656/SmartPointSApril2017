package in.bizzmark.smartpoints_user.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.bizzmark.smartpoints_user.NavigationActivity;
import in.bizzmark.smartpoints_user.R;
import in.bizzmark.smartpoints_user.adapter.PointsAdapter;
import in.bizzmark.smartpoints_user.bo.EarnPointsBO;
import in.bizzmark.smartpoints_user.login.CheckInternet;
import in.bizzmark.smartpoints_user.sqlitedb.DbHelper;
import in.bizzmark.smartpoints_user.store.StoreHomeActivity;

import static in.bizzmark.smartpoints_user.NavigationActivity.device_Id;
import static in.bizzmark.smartpoints_user.store.StoreHomeActivity.branch_Id;
import static in.bizzmark.smartpoints_user.utility.UrlUtility.POINTS_URL;

public class PointsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView iv_Arrow_Back,ivSyncData;
    TextView tvOffline;
    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    ProgressBar pb;
    LinearLayout llErrorMessage, linearLayout;
    Button btnRetry;

    ArrayList<EarnPointsBO> storeList;
    String storeName,points,storeId, type;
    Context context = PointsActivity.this;

    PointsAdapter adapter ;
    Snackbar snackbar;

    DbHelper helper;
    SQLiteDatabase sqLiteDatabase;

    CheckInternet checkInternet = new CheckInternet();

    public static String branchId = null;

    String EARN_TRANSACTION_URL = "http://bizzmark.in/smartpoints/customer-api/get-single-customer-all-branch-transactions?customerDeviceId=" ;
    String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

        storeList = new ArrayList();
        deviceID= NavigationActivity.device_Id;

        // find all ids
        findViewByAllId();
    }

    // Retrieve Data From Server Using Volley-Library
    private void retrieveDataFromServer() {
      pb.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        storeList.clear();



            StringRequest request = new StringRequest(Request.Method.GET, POINTS_URL+deviceID,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String result) {
                            pb.setVisibility(View.GONE);
                            linearLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            try {
                                JSONObject jo = new JSONObject(result);

                                //Check device transacted or not
                                type = jo.getString("status_type");
                                if (type.equalsIgnoreCase("error")) {
                                    recyclerView.setVisibility(View.GONE);
                                    linearLayout.setVisibility(View.GONE);
                                    llErrorMessage.setVisibility(View.VISIBLE);
                                }

                                JSONArray ja = jo.getJSONArray("response");
                                for (int j = 0; j < ja.length(); j++) {
                                    JSONObject jo2 = ja.getJSONObject(j);

                                    storeName = jo2.getString("store_name");
                                    points = jo2.getString("total_points");
                                    storeId = jo2.getString("storeId");

                                    EarnPointsBO earnPointsBO = new EarnPointsBO();
                                    earnPointsBO.setStorename(storeName);
                                    earnPointsBO.setPoints(points);
                                    earnPointsBO.setStoreId(storeId);

                                    storeList.add(earnPointsBO);
                                }

                                adapter = new PointsAdapter(storeList, context, new PointsAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position) {
                                        branchId = storeList.get(position).getStoreId();
                                        Intent i = new Intent(context, StoreHomeActivity.class);
                                        i.putExtra("branchId", branchId);
                                        startActivity(i);
                                    }
                                });

                                recyclerView.setAdapter(adapter);
                                // stopping swipe refresh
                                refreshLayout.setRefreshing(false);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pb.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "something went wrong please try again", Toast.LENGTH_SHORT).show();
                    // stopping swipe refresh
                    refreshLayout.setRefreshing(false);
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    300000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(request);

    }

    // Retrieve Data From SQLite-Database
    private void retrieveDataFromSQLite() {
        storeList.clear();
        helper = new DbHelper(this);
        sqLiteDatabase = helper.getWritableDatabase();

        if (sqLiteDatabase != null && storeList != null) {
            //String query = "SELECT STORE_NAME, POINTS, BRANCH_ID, STORE_ID FROM CUSTOMER_EARN GROUP BY STORE_NAME";
            //String query = "SELECT STORE_NAME, POINTS, BRANCH_ID, STORE_ID FROM CUSTOMER_EARN_REDEEM WHERE TYPE= 'earn' GROUP BY STORE_NAME";
           // db.rawQuery("select sum(amount) from transaction_table where category = Salary ;", null)




            String query = "SELECT STORE_NAME,Sum(earn_points) - Sum(redeem_points), BRANCH_ID, STORE_ID FROM CUSTOMER_EARN_REDEEM GROUP BY STORE_NAME";
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
           // Log.e("ERROR ==>", query);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        storeName = cursor.getString(cursor.getColumnIndex("STORE_NAME"));
                        points = cursor.getString(cursor.getColumnIndex("Sum(earn_points) - Sum(redeem_points)"));
                        branchId = cursor.getString(cursor.getColumnIndex("BRANCH_ID"));
                        storeId = cursor.getString(cursor.getColumnIndex("STORE_ID"));

                        EarnPointsBO earnPointsBO = new EarnPointsBO();
                        earnPointsBO.setStorename(storeName);
                        earnPointsBO.setPoints(points);
                        earnPointsBO.setStoreId(branchId);
                        //earnPointsBO.setDeviceId(deviceId);

                        storeList.add(earnPointsBO);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                sqLiteDatabase.close();

                adapter = new PointsAdapter(storeList, context, new PointsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        storeName = storeList.get(position).getStorename();
                        branchId = storeList.get(position).getStoreId();
                        Intent i = new Intent(context, StoreHomeActivity.class);
                        //  i.putExtra("storeName", storeName);
                        i.putExtra("branchId", branchId);
                        startActivity(i);
                    }
                });
                recyclerView.setAdapter(adapter);
                // stopping swipe refresh
                refreshLayout.setRefreshing(false);
            }
        }
    }

    private void findViewByAllId() {
        linearLayout = (LinearLayout) findViewById(R.id.ll_retry);
        llErrorMessage = (LinearLayout) findViewById(R.id.ll_points_error_message);
        btnRetry = (Button) findViewById(R.id.btn_retry_points);

        iv_Arrow_Back = (ImageView) findViewById(R.id.your_points_back_arrow);
        ivSyncData = (ImageView) findViewById(R.id.iv_sync_data);
        tvOffline = (TextView) findViewById(R.id.tv_online_offline);
        pb = (ProgressBar) findViewById(R.id.pb_points);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.points_swipe_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                if (checkInternet.isInternetConnected(getApplicationContext())){
                    retrieveDataFromServer();
                    getDataFromServer();
                    tvOffline.setText("Online");
                    return;
                }else {
                    retrieveDataFromSQLite();
                    tvOffline.setText("Offline");
                }
            }
        });

        recyclerView  = (RecyclerView) findViewById(R.id.recylerview_your_points);

        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        iv_Arrow_Back.setOnClickListener(this);
        ivSyncData.setOnClickListener(this);
        btnRetry.setOnClickListener(this);

        if (checkInternet.isInternetConnected(context)){
            retrieveDataFromServer();
            getDataFromServer();
            tvOffline.setText("Online");
            return;
        }else {
            retrieveDataFromSQLite();
            tvOffline.setText("Offline");
            //return;
        }
        getDataFromServer();

    }

    private void getDataFromServer() {
        StringRequest request = new StringRequest(Request.Method.GET, EARN_TRANSACTION_URL+deviceID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {

                        try {
                            JSONObject jo = new JSONObject(result);

                           // progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            String status = jo.getString("status_type");
                            if (status.equalsIgnoreCase("success")) {
                                JSONArray ja = jo.getJSONArray("response");

                                    /*for (int j = 0; j < ja.length(); j++) {
                                        JSONObject jsonObject = ja.getJSONObject(j);*/

                                // storeName = jsonObject.getString("store_name");
                                       /* transaction_id = jsonObject.getString("transaction_id");
                                        billAmount = jsonObject.getString("bill_amount");
                                        points = jsonObject.getString("points");
                                        type = jsonObject.getString("type");
                                        dateTime = jsonObject.getString("transacted_at");

                                        if (type.equalsIgnoreCase("earn")) {
                                            EarnTransactionBO earnTransactionBO = new EarnTransactionBO();
                                            earnTransactionBO.setTransaction_id(transaction_id);
                                            earnTransactionBO.setBill_amount(billAmount);
                                            earnTransactionBO.setPoints(points);
                                            earnTransactionBO.setType(type);
                                            earnTransactionBO.setDate_time(dateTime);

                                            earnTransactionList.add(earnTransactionBO);
                                        }*/

                                try{

                                    helper = new DbHelper(PointsActivity.this);
                                    sqLiteDatabase = helper.getWritableDatabase();

                                    sqLiteDatabase.beginTransaction();
                                    if(ja.length()>0) {
                                        storeName = ja.getJSONObject(0).getString("store_name");

                                        sqLiteDatabase.delete(DbHelper.TABLE_EARN_REDEEM, "STORE_NAME=?", new String[]{storeName});
                                    }

                                    for(int j = 0; j < ja.length(); j++){
                                        JSONObject jsonObject = ja.getJSONObject(j);
                                        storeName = jsonObject.getString("store_name");
                                        String billAmount = jsonObject.getString("bill_amount");
                                        String total_points=jsonObject.getString("points");
                                        type = jsonObject.getString("type");
                                        String dateTime = jsonObject.getString("transacted_at");
                                        String branchID=jsonObject.getString("storeId");
                                        String transaction_id = jsonObject.getString("transaction_id");
                                        points = jsonObject.getString("points");
                                        String discount=jsonObject.getString("discount");
                                        String newBillAmount=jsonObject.getString("discounted_price");
                                        //earnTransactionList.add(earnTransactionBO);
                                        ContentValues cv = new ContentValues();

                                        cv.put(DbHelper.STORE_NAME_COL_1, storeName);
                                        cv.put(DbHelper.TOTAL_POINTS_COL_3, points);
                                        cv.put(DbHelper.TYPE_COL_4, type);
                                        cv.put(DbHelper.DATE_TIME_COL_5, dateTime);
                                        cv.put(DbHelper.DEVICE_ID_COL_6, device_Id);
                                        cv.put(DbHelper.BRANCH_ID_COL_7, branchID);
                                        cv.put(DbHelper.STORE_ID_COL_8, branchID);
                                        cv.put(DbHelper.BILL_AMOUNT_COL_2, billAmount);
                                        cv.put(DbHelper.TOTAL_POINTS_COL_3, total_points);
                                        if(type.equals("redeem")){
                                            cv.put(DbHelper.NEW_BILL_AMOUNT_COL_9, newBillAmount);
                                            cv.put(DbHelper.DISCOUNT_AMOUNT_COL_10,discount);
                                            cv.put(DbHelper.REDEEM_POINTS_COL_12, points);
                                            cv.put(DbHelper.REDEEM_TRANSACTION_ID_COL_14, transaction_id);
                                        }else{
                                            cv.put(DbHelper.EARN_POINTS_COL_11, points);
                                            cv.put(DbHelper.EARN_TRANSACTION_ID_COL_13, transaction_id);
                                        }
                                        sqLiteDatabase.insert(DbHelper.TABLE_EARN_REDEEM, null, cv);
                                    }
                                    sqLiteDatabase.setTransactionSuccessful();

                                }catch (SQLException e){

                                }finally {
                                    sqLiteDatabase.endTransaction();
                                }





                                //  }
                               /* EarnTransactionAdapter earnTransaction = new EarnTransactionAdapter(earnTransactionList, context);
                                earnTransaction.notifyDataSetChanged();
                                recyclerView.setAdapter(earnTransaction);
                                // stopping swipe refresh
                                swipeRefreshLayout.setRefreshing(false);*/

                            }else if (status.equalsIgnoreCase("error")){
                                String error_message = jo.getString("response");
                                //    Toast.makeText(getActivity(), error_message, Toast.LENGTH_SHORT).show();
                               // progressBar.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                // stopping swipe refresh
                               // swipeRefreshLayout.setRefreshing(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(getApplicationContext(), "Something went wrong, please try again or check internet connection", Toast.LENGTH_LONG).show();
                //progressBar.setVisibility(View.VISIBLE);
                //recyclerView.setVisibility(View.GONE);
                // stopping swipe refresh
                //swipeRefreshLayout.setRefreshing(false);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        request.setRetryPolicy(new DefaultRetryPolicy(
                300000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }




    @Override
    protected void onResume() {
        super.onResume();
        if (checkInternet.isInternetConnected(this)){
//            retrieveDataFromServer();
//            tvOffline.setText("Online");
            return;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.your_points_back_arrow){
            finish();
        }else if (id == R.id.iv_sync_data){
          // startActivity(new Intent(this, StoreHomeActivity.class));
        }else if (v == btnRetry){
            retrieveDataFromServer();
            getDataFromServer();

            storeList.clear();
            pb.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
        }
    }
}
