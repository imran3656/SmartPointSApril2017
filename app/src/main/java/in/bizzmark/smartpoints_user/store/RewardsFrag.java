package in.bizzmark.smartpoints_user.store;

import android.content.Context;
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
import in.bizzmark.smartpoints_user.adapter.RewardsAdapter;
import in.bizzmark.smartpoints_user.bo.RewardsBO;
import in.bizzmark.smartpoints_user.login.CheckInternet;

import static in.bizzmark.smartpoints_user.store.StoreHomeActivity.branch_Id;

/**
 * Created by User on 18-May-17.
 */

public class RewardsFrag extends Fragment {
    View view;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;

    ProgressBar progressBar;

    ArrayList<RewardsBO> rewardsList;
    Context context = getActivity();

    String REWARDS_URL = "http://bizzmark.in/smartpoints/customer-api/get-rewards-details?branchId="+branch_Id;

    CheckInternet checkInternet = new CheckInternet();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.rewards_frag, container, false);
        findAllIds(view);
        return view;
    }

    private void findAllIds(View view) {
        rewardsList = new ArrayList<>();
        rewardsList.clear();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.rewards_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (checkInternet.isInternetConnected(getActivity())){
                    rewardsDataRetrieFromServer();
                   // rewardsList.clear();
                    return;
                }
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.rewards_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        progressBar = (ProgressBar) view.findViewById(R.id.pb_rewards);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        if (checkInternet.isInternetConnected(getActivity())){
            rewardsDataRetrieFromServer();
            return;
        }
    }

    private void rewardsDataRetrieFromServer() {
        rewardsList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, REWARDS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            String status = jsonObject.getString("status_type");

                            if (status.equalsIgnoreCase("success")){
                                JSONArray jsonArray = jsonObject.getJSONArray("response");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jo = jsonArray.getJSONObject(i);

                                    String store_id = jo.getString("store_id");
                                    String points = jo.getString("points");
                                    String rewards = jo.getString("rewards");
                                    String expiry_date = jo.getString("expiry_date");
                                    String description = jo.getString("description");

                                    RewardsBO rewardsBO = new RewardsBO();
                                    rewardsBO.setStore_id(store_id);
                                    rewardsBO.setPoints(points);
                                    rewardsBO.setRewards(rewards);
                                    rewardsBO.setExp_date(expiry_date);
                                    if(description!=null)
                                        rewardsBO.setDescription(description);
                                    else
                                        rewardsBO.setDescription("");

                                    rewardsList.add(rewardsBO);
                                }

                                RewardsAdapter rewardsAdapter = new RewardsAdapter(rewardsList, context);
                                recyclerView.setAdapter(rewardsAdapter);
                                // stopping swipe refresh
                                swipeRefreshLayout.setRefreshing(false);

                            }else if (status.equalsIgnoreCase("error")){
                                String error_msg = jsonObject.getString("response");
                               // Toast.makeText(getContext(), error_msg.toString(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                300000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
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
            rewardsDataRetrieFromServer();
            return;
        }
    }
}
