package in.bizzmark.smartpoints_user.store;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import in.bizzmark.smartpoints_user.R;
import in.bizzmark.smartpoints_user.login.CheckInternet;

import static in.bizzmark.smartpoints_user.store.StoreHomeActivity.branch_Id;


/**
 * Created by User on 02-May-17.
 */

public class AboutStoreFrag extends Fragment implements View.OnClickListener {

    Activity activity = getActivity();

    TextView tvStoreName, tv_branch_name, tv_store_address;
    Button btnCall, btnReward, btnMap;

    String ABOUT_STORE_URL = "http://bizzmark.in/smartpoints/customer-api/get-branch-details?branchId="+branch_Id;
    String storeName,owner_name,owner_mobile,branch_name,branch_address;

    CheckInternet checkInternet = new CheckInternet();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_store,container,false);
        findAllIds(view);
        return view;
    }

    private void findAllIds(View view) {
        tvStoreName = (TextView) view.findViewById(R.id.tv_storeName);
        tv_branch_name = (TextView) view.findViewById(R.id.tv_branch_name);
        tv_store_address = (TextView) view.findViewById(R.id.tv_store_address);
        btnCall = (Button) view.findViewById(R.id.btn_call_to_store);
        btnReward = (Button) view.findViewById(R.id.btn_);
        btnMap = (Button) view.findViewById(R.id.btn_view_map);

        btnReward.setOnClickListener(this);
        btnCall.setOnClickListener(this);
        btnMap.setOnClickListener(this);

        goToRewardsFrag();
        btnReward.setBackgroundColor(getResources().getColor(R.color.green));
        btnReward.setTextColor(Color.WHITE);
        btnCall.setBackgroundColor(Color.WHITE);
        btnCall.setTextColor(Color.BLACK);
        btnMap.setBackgroundColor(Color.WHITE);
        btnMap.setTextColor(Color.BLACK);

        if (checkInternet.isInternetConnected(getActivity())){
            dataRetrieveFromServer();
            return;
        }
    }

    // Data retrieve from server
    private void dataRetrieveFromServer() {
        StringRequest request = new StringRequest(Request.Method.GET, ABOUT_STORE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {

                        try {
                            JSONObject jo = new JSONObject(result);
                            String status = jo.getString("status_type");
                            if (status.equalsIgnoreCase("success")) {
                                JSONObject jo2 = jo.getJSONObject("response");

                                storeName = jo2.getString("store_name");
                                owner_name = jo2.getString("owner_name");
                                owner_mobile = jo2.getString("owner_mobile");
                                branch_name = jo2.getString("branch_name");
                                branch_address = jo2.getString("branch_address");

                                tvStoreName.setText(storeName);
                                tv_branch_name.setText(branch_name);
                                tv_store_address.setText(branch_address);

                            }else if (status.equalsIgnoreCase("error")){
                                String error = jo.getString("response");
                              //  showErrorToast(error);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              //  showErrorToast(error.getMessage().toString());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        request.setRetryPolicy(new DefaultRetryPolicy(
                300000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);

    }

    private void showErrorToast(String error) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v == btnReward){
            btnReward.setBackgroundColor(getResources().getColor(R.color.green));
            btnReward.setTextColor(Color.WHITE);

            btnCall.setBackgroundColor(Color.WHITE);
            btnCall.setTextColor(Color.BLACK);
            btnMap.setBackgroundColor(Color.WHITE);
            btnMap.setTextColor(Color.BLACK);

            goToRewardsFrag();

        }else if (v == btnCall){
            btnCall.setBackgroundColor(getResources().getColor(R.color.green));
            btnCall.setTextColor(Color.WHITE);

            if (owner_mobile != null) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + owner_mobile)));
            }else {
                Toast.makeText(getActivity(), "Number not available", Toast.LENGTH_SHORT).show();
            }

            btnReward.setBackgroundColor(Color.WHITE);
            btnReward.setTextColor(Color.BLACK);
            btnMap.setBackgroundColor(Color.WHITE);
            btnMap.setTextColor(Color.BLACK);

            goToCallFrag();

        }else if (v == btnMap){
            btnMap.setBackgroundColor(getResources().getColor(R.color.green));
            btnMap.setTextColor(Color.WHITE);
            //Toast.makeText(getActivity(), "Map", Toast.LENGTH_SHORT).show();

            btnReward.setBackgroundColor(Color.WHITE);
            btnReward.setTextColor(Color.BLACK);
            btnCall.setBackgroundColor(Color.WHITE);
            btnCall.setTextColor(Color.BLACK);

            goToMapFrag();
        }

    }

    private void goToCallFrag() {
        CallFrag callFrag = new CallFrag();

        android.support.v4.app.FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout_about_store, callFrag, "fragment");
        ft.addToBackStack(null);
        ft.commit();
    }

    private void goToMapFrag() {
        AboutStoreMapFrag aboutStoreMapFrag = new AboutStoreMapFrag();

        android.support.v4.app.FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout_about_store, aboutStoreMapFrag, "fragment");
        ft.addToBackStack(null);
        ft.commit();
    }

    private void goToRewardsFrag() {
        RewardsFrag rewardsFrag = new RewardsFrag();

        android.support.v4.app.FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout_about_store, rewardsFrag, "fragment");
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
       if (checkInternet.isInternetConnected(getActivity())){
           dataRetrieveFromServer();
           return;
       }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (checkInternet.isInternetConnected(getActivity())){
            dataRetrieveFromServer();
            return;
        }
    }
}
