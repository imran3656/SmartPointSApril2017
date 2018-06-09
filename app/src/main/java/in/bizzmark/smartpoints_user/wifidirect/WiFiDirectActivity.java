/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.bizzmark.smartpoints_user.wifidirect;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import in.bizzmark.smartpoints_user.R;
import in.bizzmark.smartpoints_user.adapter.ViewPageAdapter;
import in.bizzmark.smartpoints_user.earnredeemtab.Earn;
import in.bizzmark.smartpoints_user.earnredeemtab.Redeem;
import in.bizzmark.smartpoints_user.utility.NetworkUtils;

/**
 * An activity that uses WiFi Direct APIs to discover and connect with available
 * devices. WiFi Direct APIs are asynchronous and rely on callback mechanism
 * using interfaces to notify the application of operation success or failure.
 * The application should also register a BroadcastReceiver for notification of
 * WiFi state related events.
 */
public class WiFiDirectActivity extends AppCompatActivity
        implements ChannelListener, DeviceListFragment.DeviceActionListener {

    public static final String TAG = "wifidirectdemo";
    public static String storeName = null;
    public static Button btnRefresh = null;
    public static ProgressDialog progressDialog = null;
    private final IntentFilter intentFilter = new IntentFilter();
    protected BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String earnString = intent.getStringExtra("data");
            try {
                JSONObject obj = new JSONObject(earnString);
                // String message=;
                obj = new JSONObject(obj.getString("message"));
                if (obj.getString("status_type").equalsIgnoreCase("Success")) {
                    final Dialog dialogue = new Dialog(WiFiDirectActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View view = null;
                    Button btnOK = null;

                    if (obj.getString("type").equals("earn")) {
                        view = inflater.inflate(R.layout.earn_acknowledgement_new, null);
                        TextView tvStoreName = (TextView) view.findViewById(R.id.tv_store_name);
                        TextView tvPoints = (TextView) view.findViewById(R.id.tv_points_from_seller);
                        TextView tvPoints2 = (TextView) view.findViewById(R.id.tv_p);
                        btnOK = (Button) view.findViewById(R.id.btn_save_acknowledgement_data);
                        tvStoreName.setText(storeName);
                        String points = obj.getInt("points") + "";
                        tvPoints.setText(points);
                        tvPoints2.setText(points + " Smart points");

                    } else {
                        view = inflater.inflate(R.layout.redeem_acknowledgement_new, null);
                        TextView tvStoreName = (TextView) view.findViewById(R.id.tv_store_name_redeem);
                        TextView tvDiscountAmount = (TextView) view.findViewById(R.id.tv_discount_amount_from_seller_redeem);
                        TextView tvRemainingAmount = (TextView) view.findViewById(R.id.tv_new_amount_from_seller_redeem);
                        btnOK = (Button) view.findViewById(R.id.btn_save_acknowledgement_data);
                        tvStoreName.setText(storeName);
                        tvDiscountAmount.setText(obj.getString("discount"));
                        tvRemainingAmount.setText(obj.getString("remaining_price"));


                    }

                    if (btnOK != null)
                        btnOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogue.dismiss();
                            }
                        });

                    if (view != null) {
                        dialogue.setContentView(view);
                        dialogue.show();
                    }
                }else{
                    AlertDialog.Builder dialogue= new AlertDialog.Builder(WiFiDirectActivity.this);
                    dialogue.setMessage("Seller has canceled  transaction please contact him");
                    dialogue.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialogue.show();

                }

                //dialogue.setMessage(obj.getString("message"));

                //dialogue.setTitle(obj.getString("title"));
                /* dialogue.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         // dialog.cancel();
                         Intent intent = new Intent(WiFiDirectActivity.this, PointsActivity.class);
                         startActivity(intent);

                     }
                 });
                 dialogue.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.cancel();
                     }
                 });*/

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    TextView tv_StoreName, tv_Points;
    ScrollView svWifiDirect;
    RelativeLayout rlWifiDirect;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter viewPagerAdapter;
    private ImageView imageView_back_arrow;
    private ImageView ivWifiSettings;
    private WifiManager wifiManager;
    private List<WifiConfiguration> list;
    private WifiP2pManager manager;
    private WifiP2pManager.PeerListListener peerListListener;
    private List _peers = new ArrayList();
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;
    private Channel channel;
    private BroadcastReceiver receiver = null;

    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Auto-Refresh p2p
        autoDiscoverPeers();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait !");
        progressDialog.setMessage("Searching seller device......");
        // progressDialog.show();

        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationReceiver, new IntentFilter("some_custom_id"));


        svWifiDirect = (ScrollView) findViewById(R.id.svWifiDirect);
        rlWifiDirect = (RelativeLayout) findViewById(R.id.rlWifiDirect);


        // Turn on wifi
        turnOnWifi();
        // Forgot wifi-network if connected
        forgotWifiNetwork();

        // for back-press button
        imageView_back_arrow = (ImageView) findViewById(R.id.btn_back_arrow_select_store);
        imageView_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // for refresh button
        btnRefresh = (Button) findViewById(R.id.iv_refresh_wifi_direct);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For-disconnect-if-device-connected
                disconnect();
                // Cancel Connection
                cancelConnect();
                //deletePersistentGroups();
                discoverPeers();
                final Animation rotation = AnimationUtils.loadAnimation(WiFiDirectActivity.this, R.anim.refresh_btn_rotate);
                rotation.setRepeatCount(Animation.INFINITE);
                btnRefresh.startAnimation(rotation);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i <= 5; i++) {
                                Thread.sleep(500);
                            }
                            rotation.cancel();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });


        // for wifi-direct settings
        ivWifiSettings = (ImageView) findViewById(R.id.iv_wifi_direct_settings);
        ivWifiSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manager != null && channel != null) {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
            }
        });


        // add necessary intent values to be matched.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        //View-Pager
        viewPager();
        showViewsBasedOnInternet();

    }

    private void showViewsBasedOnInternet() {

        NetworkUtils.checkInternetConnection(getApplicationContext(), new NetworkUtils.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                svWifiDirect.setVisibility(View.GONE);
                rlWifiDirect.setVisibility(View.GONE);
                ViewGroup.LayoutParams lp = viewPager.getLayoutParams();
                float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250,
                        getResources().getDisplayMetrics());
                lp.height = (int) dp;
                viewPager.setLayoutParams(lp);

            }

            @Override
            public void onNetworkNotAvailable() {
                svWifiDirect.setVisibility(View.VISIBLE);
                rlWifiDirect.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams lp = viewPager.getLayoutParams();
                float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150,
                        getResources().getDisplayMetrics());
                lp.height = (int) dp;
                viewPager.setLayoutParams(lp);
            }
        });

    }

    private void viewPager() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPagerAdapter = new ViewPageAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new Earn(), "EARN");
        viewPagerAdapter.addFragments(new Redeem(), "REDEEM");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(Color.BLACK, Color.WHITE);

        tv_StoreName = (TextView) findViewById(R.id.tv_earn_redeem_store_name);
        tv_StoreName.setMovementMethod(new ScrollingMovementMethod());

        tv_Points = (TextView) findViewById(R.id.tv_earn_redeem_points);

        // Get StoreName after scanning
        Bundle b = getIntent().getExtras();
        if (b != null) {
            storeName = b.getString("key_store_name");
            tv_StoreName.setText(storeName);
        } else {
            //storeName = tv_StoreName.getText().toString();
            // storeName = "Test Store";
        }

        // save storeName in sharedPreferences
        SharedPreferences.Editor editor = getApplicationContext().
                getSharedPreferences("MY_STORE_NAME", Context.MODE_PRIVATE).edit();
        editor.putString("key_store_name", storeName);
        editor.commit();
    }

    // Cancel Connection
    public void cancelConnect() {
        if (manager != null) {
            manager.cancelConnect(channel, new ActionListener() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure(int reason) {
                }
            });
        }
    }

    /*Clear remembered groups */
    private void deletePersistentGroups() {
        try {
            Method[] methods = WifiP2pManager.class.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals("deletePersistentGroup")) {
                    // Delete any persistent group
                    for (int netid = 0; netid < 32; netid++) {
                        methods[i].invoke(manager, channel, netid, null);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Turn on wifi
    private void turnOnWifi() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            list = wifiManager.getConfiguredNetworks();
        }

    }

    // Forgot wifi-network if connected
    private void forgotWifiNetwork() {
        try {
            // if (wifiManager.isWifiEnabled()) {
            if (!list.isEmpty() && list != null) {
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.warning)
                        .setTitle("Warning....")
                        .setMessage(R.string.wifi_warning)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                // Forgot wifi-network if connected
                                for (WifiConfiguration i : list) {
                                    wifiManager.removeNetwork(i.networkId);
                                    wifiManager.saveConfiguration();
                                }
                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).create().show();
            }
            // }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    // Auto-Refresh p2p
    private void autoDiscoverPeers() {
        peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                _peers.clear();
                _peers.addAll(peers.getDeviceList());

                if (_peers.size() == 0) {
                    return;
                }
            }
        };
    }

    // For discover new peesa
    public void discoverPeers() {
        if (!isWifiP2pEnabled) {
            Toast.makeText(WiFiDirectActivity.this, "Enable wifi from system settings",
                    Toast.LENGTH_SHORT).show();
        }

        DeviceListFragment deviceListFragment = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
        deviceListFragment.onInitiateDiscovery();
        manager.discoverPeers(channel, new ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(WiFiDirectActivity.this, "Refreshing.....",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                // Turn on wifi
                WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifi.setWifiEnabled(true);

                Toast.makeText(WiFiDirectActivity.this, "Discovery failed :" + reason,
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    /**
     * register the BroadcastReceiver with the intent values to be matched
     */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this, peerListListener);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        forgotWifiNetwork();
    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
        DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
        if (fragmentDetails != null) {
            fragmentDetails.resetViews();
        }
    }

    @Override
    public void showDetails(WifiP2pDevice device) {
        DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        fragment.showDetails(device);

    }

    @Override
    public void connect(WifiP2pConfig config) {
        manager.connect(channel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                // Here data send after connected
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(WiFiDirectActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void disconnect() {
        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        fragment.resetViews();
        manager.removeGroup(channel, new ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
            }

            @Override
            public void onSuccess() {
                // fragment.getView().setVisibility(View.GONE);
            }

        });
    }

    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void cancelDisconnect() {

        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (manager != null) {
            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            if (fragment.getDevice() == null
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {

                manager.cancelConnect(channel, new ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(WiFiDirectActivity.this, "Aborting connection",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(WiFiDirectActivity.this,
                                "Connect abort request failed. Reason Code: " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNotificationReceiver);
        super.onDestroy();
    }

}
