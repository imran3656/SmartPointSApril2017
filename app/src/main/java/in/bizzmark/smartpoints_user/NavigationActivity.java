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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import in.bizzmark.smartpoints_user.database.PointsActivity;
import in.bizzmark.smartpoints_user.login.CheckInternet;
import in.bizzmark.smartpoints_user.login.LoginActivity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;

public class NavigationActivity extends Activity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_CODE = 100;
    NavigationView navigationView;
    ImageView imageView_Menu,imageView_Share,imageView_signin;
    CircleImageView profileImageView;
    TextView tvDeviceId;

    Button btn_Your_Points;
    boolean doubleBackToExitPressedOnce = false;

    public static String device_Id = null;
    public static String ACCESS_TOKEN = null;

    CheckInternet checkInternet = new CheckInternet();

    Animation animBounce, animFadeIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Load the animation
        animBounce = AnimationUtils.loadAnimation(this,R.anim.bounce);
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
                startActivity(new Intent(getApplicationContext(),EarnRedeemActivity.class));
            }
        });

        imageView_signin = (ImageView) findViewById(R.id.header_signin);
        imageView_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_signin.startAnimation(animBounce);
              // code for login
               if (checkInternet.isInternetConnected(getBaseContext())){
                   if (ACCESS_TOKEN.isEmpty()) {
                       showUserSigninDialog();
                   }else {
                       startActivity(new Intent(getApplication(), EditProfileActivity.class));
                   }
                   return;
               }else {
                   if (!ACCESS_TOKEN.isEmpty()){
                       startActivity(new Intent(getApplication(), EditProfileActivity.class));
                   }
                   return;
               }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        // Hide Navigation Items
        hideNavigationItems();

        // for profile picture
        profileImageView = (CircleImageView) navHeader.findViewById(R.id.profile_circleView);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //startActivity(new Intent(NavigationActivity.this, EditProfileActivity.class));
                    //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        // set device id
        tvDeviceId = (TextView) navHeader.findViewById(R.id.tv_deviceId);
        tvDeviceId.setText("Your device Id : "+device_Id);

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
    }

    //Check connection
    private void checkConnection() {
        if (checkInternet.isInternetConnected(this)){
            if (ACCESS_TOKEN.isEmpty()) {
                startActivity(new Intent(NavigationActivity.this, LoginActivity.class));
            }
            return;
        }else {
            return;
        }
    }

    // Hide Navigation Items
    private void hideNavigationItems() {
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_logout).setVisible(false);
    }

    // retrieve access-token from sharedPreferences
    private void retrievingAccessTokenFromSP() {
        SharedPreferences sp = getApplication().getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE);
        ACCESS_TOKEN = sp.getString("access_token", "");
    }

    // Runtime Permission
    private void requestCameraPermission() {
        if (checkPermissions()){
            getIMEIString();
            // Already Permission granted
        }else {
            requestPermission();
        }
    }

    private boolean checkPermissions() {
        return ( ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA ) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA,READ_PHONE_STATE, ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE){
            if (grantResults.length > 0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getIMEIString();
                    Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(CAMERA)){
                            new AlertDialog.Builder(NavigationActivity.this)
                                    .setMessage("You haven't given us permission to use camera, please enable the permission in setting to start scanning SmartpointS code")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{CAMERA,READ_PHONE_STATE,ACCESS_FINE_LOCATION},REQUEST_CODE);
                                            }
                                        }
                                    }).setCancelable(false)
                                    .create()
                                    .show();
                        }
                    }
                }
            }
        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // Access DeviceId
    private void getIMEIString() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getApplication()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            device_Id = telephonyManager.getDeviceId();
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvDeviceId.setText("Your device Id : "+device_Id);
        retrievingAccessTokenFromSP();
        if (checkInternet.isInternetConnected(this)){
            return;
        }else {
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        tvDeviceId.setText("Your device Id : "+device_Id);
        retrievingAccessTokenFromSP();
        if (checkInternet.isInternetConnected(this)){
            if (ACCESS_TOKEN.isEmpty()) {
               // showUserSigninDialog();
            }else {
               // startActivity(new Intent(getApplication(), EditProfileActivity.class));
            }
            return;
        }else {
            if (!ACCESS_TOKEN.isEmpty()){
              //  startActivity(new Intent(getApplication(), EditProfileActivity.class));
            }
            return;
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
            Toast.makeText(this, "Press again to close SmartPoints", Toast.LENGTH_SHORT).show();
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
            showAlertDialog();
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
                hideNavigationItems();
            }
        } if (id == R.id.nav_exit) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            super.onBackPressed();
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
            }else {
                Toast.makeText(this, "You already signed in", Toast.LENGTH_SHORT).show();
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

    private void showAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Choose action. . . .")
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
}
