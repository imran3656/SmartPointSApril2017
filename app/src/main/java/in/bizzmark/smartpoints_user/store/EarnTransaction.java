package in.bizzmark.smartpoints_user.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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

import in.bizzmark.smartpoints_user.R;
import in.bizzmark.smartpoints_user.adapter.EarnTransactionAdapter;
import in.bizzmark.smartpoints_user.bo.EarnTransactionBO;
import in.bizzmark.smartpoints_user.login.CheckInternet;
import in.bizzmark.smartpoints_user.sqlitedb.DbHelper;

import static in.bizzmark.smartpoints_user.NavigationActivity.device_Id;
import static in.bizzmark.smartpoints_user.store.StoreHomeActivity.branch_Id;

/**
 * Created by User on 18-May-17.
 */

public class EarnTransaction extends Fragment {
    View view;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;

    ProgressBar progressBar;

    String transaction_id, storeName, billAmount, points, type, dateTime;
    ArrayList<EarnTransactionBO> earnTransactionList;
    Context context = getActivity();
    JSONObject jo;

    DbHelper helper;
    SQLiteDatabase sqLiteDatabase;

    String EARN_TRANSACTION_URL = "http://bizzmark.in/smartpoints/customer-api/get-single-customer-all-branch-transactions?customerDeviceId="+device_Id+"&branchId="+branch_Id ;

    CheckInternet checkInternet = new CheckInternet();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.earn_transaction,container,false);
        findAllIds(view);
        return view;
    }

    // Find all ids
    private void findAllIds(View view) {
        earnTransactionList = new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_earn_transaction);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        progressBar = (ProgressBar) view.findViewById(R.id.pb_earn_transaction);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.earn_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (checkInternet.isInternetConnected(getActivity())){
                    getDataFromServer();
                    return;
                }else {
                    getDataFromSQLite();
                }
            }
        });

        if (checkInternet.isInternetConnected(getActivity())){
            getDataFromServer();
            return;
        }else {
            getDataFromSQLite();
        }
    }

    // Retrieve data from SQLite-Database
    private void getDataFromSQLite() {
        earnTransactionList.clear();
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        helper = new DbHelper(getActivity());
        sqLiteDatabase = helper.getWritableDatabase();

        //String query = "SELECT BILL_AMOUNT, POINTS, DATE_TIME FROM CUSTOMER_EARN_REDEEM WHERE TYPE= 'earn' AND BRANCH_ID="+branch_Id;
        String query = "SELECT EARN_TRANSACTION_ID, BILL_AMOUNT, EARN_POINTS, DATE_TIME, TYPE FROM CUSTOMER_EARN_REDEEM WHERE BRANCH_ID="+branch_Id;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                    transaction_id = cursor.getString(cursor.getColumnIndex("EARN_TRANSACTION_ID"));
                    billAmount = cursor.getString(cursor.getColumnIndex("BILL_AMOUNT"));
                    points = cursor.getString(cursor.getColumnIndex("EARN_POINTS"));
                    dateTime = cursor.getString(cursor.getColumnIndex("DATE_TIME"));
                    type = cursor.getString(cursor.getColumnIndex("TYPE"));

                    if (type.equalsIgnoreCase("earn")) {

                        EarnTransactionBO earnTransactionBO = new EarnTransactionBO();
                        earnTransactionBO.setTransaction_id(transaction_id);
                        earnTransactionBO.setBill_amount(billAmount);
                        earnTransactionBO.setPoints(points);
                        earnTransactionBO.setDate_time(dateTime);

                    //    Toast.makeText(context, "Earn : "+"\n"+ "Bill amount : "+earnTransactionBO.getBill_amount()+ "\n"+ "Points : "+earnTransactionBO.getPoints()+ "\n"+ "Date : "+ earnTransactionBO.getDate_time(), Toast.LENGTH_SHORT).show();

                        earnTransactionList.add(earnTransactionBO);
                    }
                }while (cursor.moveToNext());

                EarnTransactionAdapter earnTransaction = new EarnTransactionAdapter(earnTransactionList, context);
              //  earnTransaction.notifyDataSetChanged();
                recyclerView.setAdapter(earnTransaction);
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }


    // Earn Transaction Data-Retrieving from Server
    private void getDataFromServer() {
        earnTransactionList.clear();
        StringRequest request = new StringRequest(Request.Method.GET, EARN_TRANSACTION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {

                        try {
                            jo = new JSONObject(result);

                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            String status = jo.getString("status_type");
                            if (status.equalsIgnoreCase("success")) {
                                JSONArray ja = jo.getJSONArray("response");

                                    for (int j = 0; j < ja.length(); j++) {
                                        JSONObject jsonObject = ja.getJSONObject(j);

                                        // storeName = jsonObject.getString("store_name");
                                        transaction_id = jsonObject.getString("transaction_id");
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
                                        }

                                        /* try{

                                             helper = new DbHelper(getActivity());
                                             sqLiteDatabase = helper.getWritableDatabase();

                                             sqLiteDatabase.beginTransaction();
                                             if(ja.length()>0) {
                                                 storeName = ja.getJSONObject(0).getString("store_name");

                                                 sqLiteDatabase.delete(DbHelper.TABLE_EARN_REDEEM, "STORE_NAME=?", new String[]{storeName});
                                             }

                                             for(int j = 0; j < ja.length(); j++){
                                                 JSONObject jsonObject = ja.getJSONObject(j);
                                                 storeName = jsonObject.getString("store_name");
                                                 billAmount = jsonObject.getString("bill_amount");
                                                 String total_points=jsonObject.getString("points");
                                                 type = jsonObject.getString("type");
                                                 dateTime = jsonObject.getString("transacted_at");
                                                 String branchID=jsonObject.getString("storeId");
                                                 transaction_id = jsonObject.getString("transaction_id");
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
*/


                                    }
                                //getDataFromSQLite();
                                EarnTransactionAdapter earnTransaction = new EarnTransactionAdapter(earnTransactionList, context);
                                earnTransaction.notifyDataSetChanged();
                                recyclerView.setAdapter(earnTransaction);
                                // stopping swipe refresh
                                swipeRefreshLayout.setRefreshing(false);

                            }else if (status.equalsIgnoreCase("error")){
                                String error_message = jo.getString("response");
                            //    Toast.makeText(getActivity(), error_message, Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                // stopping swipe refresh
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Something went wrong, please try again or check internet connection", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        request.setRetryPolicy(new DefaultRetryPolicy(
                300000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkInternet.isInternetConnected(getActivity())){
            return;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (checkInternet.isInternetConnected(getActivity())){
            getDataFromServer();
            earnTransactionList.clear();
            return;
        }
    }
}
