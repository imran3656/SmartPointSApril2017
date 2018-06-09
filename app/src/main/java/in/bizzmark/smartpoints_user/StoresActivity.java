package in.bizzmark.smartpoints_user;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.List;

import in.bizzmark.smartpoints_user.adapter.EarnTransactionAdapter;
import in.bizzmark.smartpoints_user.adapter.StoresAdapter;
import in.bizzmark.smartpoints_user.bo.EarnTransactionBO;
import in.bizzmark.smartpoints_user.bo.StoresListBO;
import in.bizzmark.smartpoints_user.database.PointsActivity;
import in.bizzmark.smartpoints_user.utility.NetworkUtils;

import static in.bizzmark.smartpoints_user.utility.UrlUtility.GET_STORES_LIST;

/**
 * Created by Saikrupa on 4/1/2018.
    
 */

public class StoresActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerview;
    StoresAdapter mAdapter;
    List<StoresListBO> storesList;
    Button btnPoints,btnMakeTransaction;
    public Toolbar toolbar;
    TextView tvEmptyView;
    ImageView imageView_Menu, imageView_Share, imageView_signin;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores_list);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
      // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerview=(RecyclerView)findViewById(R.id.recyclerView);
        tvEmptyView=(TextView)findViewById(R.id.tvEmptyView);
        imageView_Share = (ImageView)findViewById(R.id.header_share);
        imageView_Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StoresActivity.this,"share the app",Toast.LENGTH_LONG).show();
            }
        });

        imageView_signin = (ImageView)findViewById(R.id.header_signin);
        imageView_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"signin",Toast.LENGTH_LONG).show();
            }
        });


        loadData();
        btnMakeTransaction=(Button)findViewById(R.id.btnTransaction);
        btnPoints=(Button)findViewById(R.id.btnPoints);
        storesList=new ArrayList<>();
        mAdapter=new StoresAdapter(storesList,StoresActivity.this);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));
        recyclerview.setAdapter(mAdapter);
        //setUpToolBar();
        btnPoints.setOnClickListener(this);
        btnMakeTransaction.setOnClickListener(this);


    }



    private void setUpToolBar() {

        View v = getLayoutInflater().inflate(R.layout.activity_stores_list,null);
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Stores");
        toolbar.setTitleTextAppearance(this, R.style.MyToolbarTitleApperance);
        setSupportActionBar(toolbar);

    }


    private void loadData() {

        NetworkUtils.checkInternetConnection(getApplicationContext(), new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {

                recyclerview.setVisibility(View.VISIBLE);
                tvEmptyView.setVisibility(View.GONE);

                StringRequest request = new StringRequest(Request.Method.GET, GET_STORES_LIST,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String result) {

                                try {
                                    JSONObject obj=new JSONObject(result);
                                    obj.getString("status_type");
                                    JSONArray array=obj.getJSONArray("result");
                                    for(int i=0;i<array.length();i++){
                                        JSONObject singleJsonObj=array.getJSONObject(i);
                                        StoresListBO singleObj=new StoresListBO();
                                        singleObj.setStoreName(singleJsonObj.getString("store_name"));
                                        singleObj.setBranchName(singleJsonObj.getString("branch_name"));
                                        singleObj.setBranchAddress(singleJsonObj.getString("address"));
                                        singleObj.setPointsPercent( singleJsonObj.getString("points_percentage"));
                                        singleObj.setPointsValue(singleJsonObj.getString("points_value"));
                                        singleObj.setDescription(singleJsonObj.getString("description"));
                                        storesList.add(singleObj);
                                    }
                                    mAdapter.notifyDataSetChanged();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Something went wrong, please try again or check internet connection", Toast.LENGTH_LONG).show();

                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(StoresActivity.this);
                request.setRetryPolicy(new DefaultRetryPolicy(
                        300000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(request);


            }

            @Override
            public void onNetworkNotAvailable() {
                recyclerview.setVisibility(View.GONE);
                tvEmptyView.setVisibility(View.VISIBLE);
                tvEmptyView.setText("Your using in offline mode, Click on make transaction to use in offline");
            }
        });






    }




    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnPoints){
            startActivity(new Intent(getApplicationContext(), PointsActivity.class));
        }else if(v.getId()==R.id.btnTransaction){
            startActivity(new Intent(getApplicationContext(), NavigationActivity.class));
        }
    }


}
