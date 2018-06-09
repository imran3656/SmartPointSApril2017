package in.bizzmark.smartpoints_user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.bizzmark.smartpoints_user.adapter.NavigationItemAdapter;
import in.bizzmark.smartpoints_user.bo.NavigationItems;
import in.bizzmark.smartpoints_user.database.PointsActivity;
import in.bizzmark.smartpoints_user.login.CheckInternet;
import in.bizzmark.smartpoints_user.login.LoginActivity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;
import static in.bizzmark.smartpoints_user.utility.UrlUtility.LOGIN_URL;
import static in.bizzmark.smartpoints_user.utility.UrlUtility.SEND_DEVICE_TOKEN;

public class NavigationActivity extends Activity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_CODE = 100;
    NavigationView navigationView;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    ImageView imageView_Menu, imageView_Share, imageView_signin;
    CircleImageView profileImageView;
    TextView tvDeviceId;
    Button btnExit;

    ListView listView_nav_drawer;

    Button btn_Your_Points;
    boolean doubleBackToExitPressedOnce = false;

    public static String device_Id = null;
    public static String ACCESS_TOKEN = null;

    CheckInternet checkInternet = new CheckInternet();

    Animation animBounce, animFadeIn;

    ArrayList<NavigationItems> navigationItemList = new ArrayList<>();
    NavigationItemAdapter navigationItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Load the animation
        animBounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);


        imageView_Menu = (ImageView) findViewById(R.id.image_menu);
        imageView_Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        imageView_Share = (ImageView) findViewById(R.id.header_share);
        imageView_Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApkAlertDialog();
            }
        });

        imageView_signin = (ImageView) findViewById(R.id.header_signin);
        imageView_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_signin.startAnimation(animBounce);
                // code for login
                if (checkInternet.isInternetConnected(getBaseContext())) {
                    if (ACCESS_TOKEN.isEmpty()) {
                        showUserSigninDialog();
                    } else {
                        startActivity(new Intent(getApplication(), EditProfileActivity.class));
                    }
                    return;
                } else {
                    if (!ACCESS_TOKEN.isEmpty()) {
                        startActivity(new Intent(getApplication(), EditProfileActivity.class));
                    }
                    Toast.makeText(NavigationActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        listView_nav_drawer = (ListView) findViewById(R.id.listview_navigation);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //Mount listview with adapter
        initDrawerLayout();


        // for points button
        btn_Your_Points = (Button) findViewById(R.id.your_points_button);
        btn_Your_Points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  btn_Your_Points.startAnimation(animBounce);
                btn_Your_Points.startAnimation(animFadeIn);
                startActivity(new Intent(NavigationActivity.this, PointsActivity.class));

            }
        });

        // Request Camera Permission
        requestCameraPermission();

        // For Access-Token
        retrievingAccessTokenFromSP();

        //Check internet-connection
        checkConnection();

        // Check-New-version
        // checkNewVersionForUpdate();
        getDeviceID();
    }

    private void getDeviceID() {
        if (FirebaseInstanceId.getInstance().getToken() != null) {
            String deviceToken = FirebaseInstanceId.getInstance().getToken();
            Log.v("GCMID", deviceToken);
            sendDeviceToken(deviceToken);
        }
    }

    private void sendDeviceToken(final String deviceToken) {
        getIMEIString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_DEVICE_TOKEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NavigationActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                //etPassword.setText("");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("deviceId", device_Id);
                parameters.put("devicetoken", deviceToken);
                parameters.put("usertype", "customer");
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                300000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }


    //Mount listview with adapter
    private void initDrawerLayout() {
        setListViewData();
        setListViewHeader();
        setListViewFooter();
        navigationItemAdapter = new NavigationItemAdapter(this, R.layout.nav_items, navigationItemList);
        listView_nav_drawer.setAdapter(navigationItemAdapter);

        toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
    }

    // For navigation footer
    private void setListViewFooter() {
        LayoutInflater inflater = getLayoutInflater();
        View footer = inflater.inflate(R.layout.nav_listview_footer, listView_nav_drawer, false);
        btnExit = (Button) footer.findViewById(R.id.btn_exit_app);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                System.exit(0);
            }
        });
        listView_nav_drawer.addFooterView(footer, null, false);
    }

    // For navigation header
    private void setListViewHeader() {
        LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.nav_header_main, listView_nav_drawer, false);
        // set device id
        tvDeviceId = (TextView) header.findViewById(R.id.tv_deviceId);
        tvDeviceId.setText("Your device Id : " + device_Id);
        listView_nav_drawer.addHeaderView(header, null, false);
    }

    // Set navigation items
    private void setListViewData() {
        try {
            navigationItemList.add(new NavigationItems(R.drawable.ic_share_black_24px, "Share this app"));
            navigationItemList.add(new NavigationItems(R.drawable.ic_person_add_black_24px, "Invite"));
            navigationItemList.add(new NavigationItems(R.drawable.ic_version_black_24dp, "Contact us"));
            navigationItemList.add(new NavigationItems(R.drawable.ic_help_black_24dp, "FAQ"));
            navigationItemList.add(new NavigationItems(R.drawable.ic_privacy_policy, "Privacy policy"));
            navigationItemList.add(new NavigationItems(R.drawable.ic_announcement_black_24dp, "Term and Conditions"));
            // navigationItemList.add(new NavigationItems(R.drawable.ic_exit, "Exit"));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    // Check-New-version
    private void checkNewVersionForUpdate() {
        try {
            String oldVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            String newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=in.bizzmark.smartpoints_user&rdid=in.bizzmark.smartpoints_user")
                    .timeout(300000)
                    .userAgent("Mozilla/5.0 (Linux; Android 5.1.1; Nexus 5 Build/LMY48B; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/43.0.2357.65 Mobile Safari/537.36")
                    .referrer("http://www.google.com").get()
                    .select("div[itemprop=softwareVersion]").first()
                    .ownText();
            Toast.makeText(this, "New :" + newVersion + "\n" + "Old :" + oldVersion, Toast.LENGTH_LONG).show();

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Check connection
    private void checkConnection() {
        if (checkInternet.isInternetConnected(this)) {
            if (ACCESS_TOKEN.isEmpty()) {
                //  startActivity(new Intent(NavigationActivity.this, LoginActivity.class));
            }
            return;
        } else {
            return;
        }
    }

    // Hide Navigation Items
   /* private void hideNavigationItems() {
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_logout).setVisible(false);
    }*/

    // retrieve access-token from sharedPreferences
    private void retrievingAccessTokenFromSP() {
        SharedPreferences sp = getApplication().getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE);
        ACCESS_TOKEN = sp.getString("access_token", "");
    }

    // Runtime Permission
    private void requestCameraPermission() {
        if (checkPermissions()) {
            getIMEIString();
            // Already Permission granted
        } else {
            requestPermission();
        }
    }

    private boolean checkPermissions() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA, READ_PHONE_STATE, ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getIMEIString();
                    Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(CAMERA)) {
                            new AlertDialog.Builder(NavigationActivity.this)
                                    .setMessage("You haven't given us permission to use camera, please enable the permission in setting to start scanning SmartpointS code")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{CAMERA, READ_PHONE_STATE, ACCESS_FINE_LOCATION}, REQUEST_CODE);
                                            }
                                        }
                                    }).setCancelable(false)
                                    .create()
                                    .show();
                        }
                    }
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // Access DeviceId
    private void getIMEIString() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getApplication()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            device_Id = telephonyManager.getDeviceId();
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            tvDeviceId.setText("Your device Id : " + device_Id);
            retrievingAccessTokenFromSP();
            if (checkInternet.isInternetConnected(this)) {
                return;
            } else {
                return;
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            tvDeviceId.setText("Your device Id : "+device_Id);
            retrievingAccessTokenFromSP();
            if (checkInternet.isInternetConnected(this)) {
                if (ACCESS_TOKEN.isEmpty()) {
                    // showUserSigninDialog();
                } else {
                    // startActivity(new Intent(getApplication(), EditProfileActivity.class));
                }
                return;
            } else {
                if (!ACCESS_TOKEN.isEmpty()) {
                    //  startActivity(new Intent(getApplication(), EditProfileActivity.class));
                }
                return;
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    // backpressed click
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press again to close SmartpointS", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_faq) {
            startActivity(new Intent(NavigationActivity.this,FAQActivity.class));
        } else if (id == R.id.nav_terms_conditions) {
            startActivity(new Intent(NavigationActivity.this,TermCondtionActivity.class));}
        else if (id == R.id.nav_privacy_policy) {
            startActivity(new Intent(NavigationActivity.this,PrivacyPolicyActivity.class));
        } else if (id == R.id.nav_share) {
            shareApkAlertDialog();
        } else if (id == R.id.nav_local_store) {
           // startActivity(new Intent(NavigationActivity.this,LocalStoreActivity.class));
        } else if (id == R.id.nav_contact_us) {
            showContactUsDialog();
        } else if (id == R.id.nav_signin) {
            // do login
            //userSignin();
        } else if (id == R.id.nav_logout){
            // do logout
            if (!ACCESS_TOKEN.isEmpty()) {
                //userLogout();
            }else {
              //  hideNavigationItems();
            }
        }else if (id == R.id.nav_exit) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            super.onBackPressed();
        }else if (id == R.id.nav_invite){
            startActivity(new Intent(this, InviteActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // User-Logout
    private void userLogout() {
       if (checkInternet.isInternetConnected(this)){
           if (!ACCESS_TOKEN.isEmpty()) {
               showUserLogoutDialog();
           }
           return;
       }else {
           return;
       }
    }

    // User-Login
    private void userSignin() {
        if (checkInternet.isInternetConnected(this)) {
            if (ACCESS_TOKEN.isEmpty()) {
                showUserSigninDialog();
            }
            return;
        } else {
            return;
        }
    }

    // Logout-Dialog
    private void showUserLogoutDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Arr you wana logout ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // Clear data from SharedPreferences
                        SharedPreferences preferences =getSharedPreferences("USER_DETAILS",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                        Toast.makeText(getApplicationContext(), "Logout successfully", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false)
                .create().show();
    }

    private void showUserSigninDialog() {
        startActivity(new Intent(NavigationActivity.this, LoginActivity.class));
    }

    private void showContactUsDialog() {
        new AlertDialog.Builder(this)
                .setMessage("For any queries contact this email \n bizzmark.in@gmail.com")
                .setPositiveButton("send email", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:bizzmark.in@gmail.com")));
                    }
                }).create().show();
    }

    private void shareApkAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Choose action for share this app....")
                .setPositiveButton("BLUETOOTH", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shareApplicationByBluetooth();
                    }
                }).setNegativeButton("OTHERS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                shareApplicationByShareit();
            }
        }).create().show();
    }

    private void shareApplicationByShareit() {
        PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo appInfo;
        try {
            appInfo = pm.getApplicationInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
            Intent sendBt = new Intent(Intent.ACTION_SEND);
            sendBt.setType("*/*");
          //  sendBt.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + appInfo.publicSourceDir));
            sendBt.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(appInfo.publicSourceDir)));
            sendBt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(sendBt, "Share it using"));
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    // Share Application By Bluetooth
    private void shareApplicationByBluetooth() {
        try {
            ApplicationInfo app=getApplicationContext().getApplicationInfo();
            String filepath = app.sourceDir;
            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            // Only use Bluetooth to send .apk
            intent.setPackage("com.android.bluetooth");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filepath)));
            startActivity(Intent.createChooser(intent,"Share app"));
            Toast.makeText(getApplicationContext(),"Share the SmartpointS by bluetooth ", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateMainLayout(NavigationItems item) {
        String itemName = item.getNavItemName();
        if (itemName.equalsIgnoreCase("Share this app")){
            shareApkAlertDialog();
        }else if (itemName.equalsIgnoreCase("Invite")){
            startActivity(new Intent(this, InviteActivity.class));
        }else if (itemName.equalsIgnoreCase("Contact us")){
            showContactUsDialog();
        }else if (itemName.equalsIgnoreCase("FAQ")){
            startActivity(new Intent(this, FAQActivity.class));
        }else if (itemName.equalsIgnoreCase("Privacy policy")){
            startActivity(new Intent(this, PrivacyPolicyActivity.class));
        }else if (itemName.equalsIgnoreCase("Term and Conditions")){
            startActivity(new Intent(this, TermCondtionActivity.class));
        }
    }
}
