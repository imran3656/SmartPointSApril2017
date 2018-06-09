package in.bizzmark.smartpoints_user.store;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import in.bizzmark.smartpoints_user.adapter.RedeemTransactionAdapter;
import in.bizzmark.smartpoints_user.bo.RedeemTransactionBO;
import in.bizzmark.smartpoints_user.login.CheckInternet;
import in.bizzmark.smartpoints_user.sqlitedb.DbHelper;

import static in.bizzmark.smartpoints_user.NavigationActivity.device_Id;
import static in.bizzmark.smartpoints_user.store.StoreHomeActivity.branch_Id;

/**
 * Created by User on 18-May-17.
 */

public class RedeemTransaction extends Fragment {
    View view;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;

    String transaction_id, paid_bill, points, discount_amount, type, dateTime;
    ArrayList<RedeemTransactionBO> redeemTransactionList;
    Context context = getActivity();
    JSONObject jo;
    String REDEEM_TRANSACTION_URL = "http://bizzmark.in/smartpoints/customer-api/get-single-customer-all-branch-transactions?customerDeviceId="+device_Id+"&branchId="+branch_Id ;

    DbHelper helper;
    SQLiteDatabase sqLiteDatabase;

    CheckInternet checkInternet = new CheckInternet();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.redeem_transaction,container,false);
        findViewByAllIds(view);
        return view;
    }

    private void findViewByAllIds(View view) {
        redeemTransactionList = new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_redeem_transactiom);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (checkInternet.isInternetConnected(getActivity())) {
            dataRetrieveFromServer();
            return;
        }else {
            getDataFromSQLite();
        }
    }

    // Retrieve data from SQLite-Database
    private void getDataFromSQLite() {
        // earnTransactionList.clear();
        helper = new DbHelper(getActivity());
        sqLiteDatabase = helper.getWritableDatabase();

        //String query = "SELECT BILL_AMOUNT, POINTS, DATE_TIME FROM CUSTOMER_EARN_REDEEM WHERE TYPE= 'redeem' AND BRANCH_ID="+branch_Id;
        String query = "SELECT NEW_BILL_AMOUNT, DISCOUNT_AMOUNT, REDEEM_TRANSACTION_ID, REDEEM_POINTS, DATE_TIME, TYPE FROM CUSTOMER_EARN_REDEEM WHERE BRANCH_ID="+branch_Id;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                    paid_bill = cursor.getString(cursor.getColumnIndex("NEW_BILL_AMOUNT"));
                    points = cursor.getString(cursor.getColumnIndex("REDEEM_POINTS"));
                    dateTime = cursor.getString(cursor.getColumnIndex("DATE_TIME"));
                    type = cursor.getString(cursor.getColumnIndex("TYPE"));
                    discount_amount = cursor.getString(cursor.getColumnIndex("DISCOUNT_AMOUNT"));
                    transaction_id = cursor.getString(cursor.getColumnIndex("REDEEM_TRANSACTION_ID"));

                    if (type.equalsIgnoreCase("redeem")) {

                        RedeemTransactionBO redeemTransactionBO = new RedeemTransactionBO();
                        redeemTransactionBO.setTransaction_id(transaction_id);
                        redeemTransactionBO.setBill_amount(paid_bill);
                        redeemTransactionBO.setPoints(points);
                        redeemTransactionBO.setDate_time(dateTime);
                        redeemTransactionBO.setDiscount_amount(discount_amount);

                        redeemTransactionList.add(redeemTransactionBO);
                    }
                }while (cursor.moveToNext());

                RedeemTransactionAdapter redeemTransaction = new RedeemTransactionAdapter(redeemTransactionList, context);
                redeemTransaction.notifyDataSetChanged();
                recyclerView.setAdapter(redeemTransaction);
            }
        }
    }

    // Retrieve data from server
    private void dataRetrieveFromServer() {
        StringRequest request = new StringRequest(Request.Method.GET, REDEEM_TRANSACTION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {

                        try {
                            jo = new JSONObject(result);
                            String status = jo.getString("status_type");
                            if (status.equalsIgnoreCase("success")) {
                                JSONArray ja = jo.getJSONArray("response");

                                    for (int j = 0; j < ja.length(); j++) {
                                        JSONObject jsonObject = ja.getJSONObject(j);

                                        // storeName = jsonObject.getString("store_name");
                                        transaction_id = jsonObject.getString("transaction_id");
                                        paid_bill = jsonObject.getString("discounted_price");
                                        points = jsonObject.getString("points");
                                        type = jsonObject.getString("type");
                                        discount_amount = jsonObject.getString("discount");
                                        dateTime = jsonObject.getString("transacted_at");

                                        if (type.equalsIgnoreCase("redeem")) {
                                            RedeemTransactionBO redeemTransactionBO = new RedeemTransactionBO();
                                            redeemTransactionBO.setTransaction_id(transaction_id);
                                            redeemTransactionBO.setBill_amount(paid_bill);
                                            redeemTransactionBO.setPoints(points);
                                            redeemTransactionBO.setType(type);
                                            redeemTransactionBO.setDiscount_amount(discount_amount);
                                            redeemTransactionBO.setDate_time(dateTime);

                                            redeemTransactionList.add(redeemTransactionBO);
                                        }
                                    }

                                RedeemTransactionAdapter redeemTransaction = new RedeemTransactionAdapter(redeemTransactionList, context);
                                redeemTransaction.notifyDataSetChanged();
                                recyclerView.setAdapter(redeemTransaction);

                            }else if (status.equalsIgnoreCase("error")){
                                String error_message = jo.getString("response");
                                Toast.makeText(getActivity(), error_message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        request.setRetryPolicy(new DefaultRetryPolicy(
                300000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }
}
