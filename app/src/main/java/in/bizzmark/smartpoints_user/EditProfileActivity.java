package in.bizzmark.smartpoints_user;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import in.bizzmark.smartpoints_user.login.CheckInternet;

import static in.bizzmark.smartpoints_user.NavigationActivity.ACCESS_TOKEN;
import static in.bizzmark.smartpoints_user.utility.UrlUtility.UPDATE_PROFILE_URL;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    int CAMERA_REQUEST_CODE = 100;
    int GALLERY_REQUEST_CODE = 101;

    ImageView iv_back_arrow_edit_profile;
    ImageView iv_Profile_pic;
    TextView editProfile;

    EditText etName, etEmail, etMobile, etDob;
    Button btnUpdate, btnLogout;

    boolean status = true;
    String name, email, mobile, dob, gender;
    RadioGroup radioGroup;
    RadioButton rbMale, rbFemale;

    CheckInternet checkInternet = new CheckInternet();
    ProgressDialog progressDialog;
    String updateName, updatePhone, genderUpDate, upDateDOB;
    int mYear, mMonth, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Find All Ids
        findViewByAllIds();

        // RETRIEVING DETAILS FROM SP
        retrievingDataFromSp();
    }

    // RETRIEVING DETAILS FROM SP
    private void retrievingDataFromSp() {
        SharedPreferences sp = getApplication().getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE);
        name = sp.getString("name", "");
        email = sp.getString("email", "");
        mobile = sp.getString("mobile", "");
        dob = sp.getString("dob", "");
        gender = sp.getString("gender", "");

        etName.setText(name);
        etEmail.setText(email);
        etMobile.setText(mobile);
        etDob.setText(dob);
        if (gender.equalsIgnoreCase("Male")){
            rbMale.setChecked(true);
        }else if (gender.equalsIgnoreCase("Female")){
            rbFemale.setChecked(true);
        }
    }

    private void findViewByAllIds() {
        iv_Profile_pic = (ImageView) findViewById(R.id.circleView_profile_pic);
        iv_back_arrow_edit_profile = (ImageView) findViewById(R.id.back_arrow_edit_profile);
        editProfile = (TextView) findViewById(R.id.profile_pic_edit_tv);
        iv_back_arrow_edit_profile.setOnClickListener(this);
        editProfile.setOnClickListener(this);

        etName = (EditText) findViewById(R.id.et_edit_profile_name);
        etName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setClickable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        etEmail = (EditText) findViewById(R.id.et_edit_profile_email_id);
        etEmail.setEnabled(false);
        etMobile = (EditText) findViewById(R.id.et_edit_profile_mobile_no);
        etMobile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setClickable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        etDob = (EditText) findViewById(R.id.et_edit_profile_date_of_birth);
        etDob.setOnClickListener(this);

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        rbMale = (RadioButton) findViewById(R.id.rb_male);
        rbFemale= (RadioButton) findViewById(R.id.rb_female);

        btnUpdate = (Button) findViewById(R.id.btn_update);
        btnLogout = (Button) findViewById(R.id.btn_logout);
        btnUpdate.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    // For Date Picker-Dialog
    private void showDatePickerDialog(View v) {
        //To show current date in the datepicker
        Calendar mcurrentDate = Calendar.getInstance();
        mYear = mcurrentDate.get(Calendar.YEAR);
        mMonth = mcurrentDate.get(Calendar.MONTH);
        mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                    //      Your code   to get date and time
                    mYear = selectedyear;
                    mMonth = selectedmonth;
                    mDay = selectedday;

                    etDob.setText(new StringBuilder()
                            .append(mYear).append("-")
                            .append(mMonth + 1).append("-")
                            .append(mDay).append(""));

                }
            }, mYear, mMonth, mDay);
            mDatePicker.setTitle("Select date");
            mDatePicker.show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == iv_back_arrow_edit_profile){
            finish();
        }else if (v == editProfile){
            // for select profile image
            showSelectProfilePicDialog();
        }else if (v == btnUpdate){
            // do update profile
            if (checkInternet.isInternetConnected(this)) {
                updateProfileData();
                return;
            }else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }else if (v == btnLogout){
            // do logout
            showLogoutDialog();
        }else if (v == etDob){
            // For Date-Picker-Dialog
            showDatePickerDialog(v);
        }
    }

    // do logout
    private void showLogoutDialog() {
        if (!ACCESS_TOKEN.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setMessage("Are you wana logout ?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // Clear data from SharedPreferences
                            clearDataFromSP();
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setCancelable(false)
                    .create().show();
        }else {
            //  Toast.makeText(this, "You not logged in, please login first", Toast.LENGTH_SHORT).show();
        }
    }

    // Clear data from SharedPreferences
    private void clearDataFromSP() {
        SharedPreferences preferences = getSharedPreferences("USER_DETAILS",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        Toast.makeText(getApplicationContext(), "Logout successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    // Update profile
    private void updateProfileData() {
        updateName = etName.getText().toString().trim();
        updatePhone = etMobile.getText().toString().trim();
        upDateDOB = etDob.getText().toString();

        int id = radioGroup.getCheckedRadioButtonId();
        if (id == R.id.rb_male){
            genderUpDate = rbMale.getText().toString();
            if (genderUpDate.equalsIgnoreCase("Male")){
                genderUpDate = "1";
            }
        }else if (id == R.id.rb_female){
            genderUpDate = rbFemale.getText().toString();
            if (genderUpDate.equalsIgnoreCase("Female")){
                genderUpDate = "0";
            }
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait updating...........");

        if (TextUtils.isEmpty(updateName)) {
            etName.setError("Empty field");
        } else if (TextUtils.isEmpty(updatePhone)) {
            etMobile.setError("Empty Field");
        } else if (updatePhone.length() < 10) {
            etMobile.setError("Phone number should be 10 digits");
        } else {
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_PROFILE_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status_type");
                                if (status.equalsIgnoreCase("success")){
                                    String success_msg = jsonObject.getString("response");
                                    new AlertDialog.Builder(EditProfileActivity.this)
                                            .setMessage(success_msg)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    finish();
                                                    clearDataFromSP();
                                                }
                                            }).setCancelable(false).create().show();
                                }else if (status.equalsIgnoreCase("error")){
                                    String error_msg = jsonObject.getString("response");
                                    Toast.makeText(EditProfileActivity.this, error_msg, Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Updating error : "+error, Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String,String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("name", updateName);
                    params.put("mobile", updatePhone);
                    params.put("email", email);
                    params.put("gender", genderUpDate);
                    params.put("date_of_birth", upDateDOB);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    300000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        }
    }

    // for select profile image
    private void showSelectProfilePicDialog() {
        new AlertDialog.Builder(EditProfileActivity.this)
                .setTitle("Choose from")
                .setCancelable(true)
                .setPositiveButton("GALLERY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getImageFromGallery();
                    }
                })
                .setNegativeButton("CAMERA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getImageFromCamera();
                    }
                }).create().show();
    }

    // Select Image from Gallery
    private void getImageFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i,GALLERY_REQUEST_CODE);
    }

    // Get Image-from Camera
    private void getImageFromCamera() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK  && null != data ){
            Uri uri = data.getData();
            //Using Picasso Library
            Picasso.with(this)
                    .load(uri)
                    .into(iv_Profile_pic);

            //View Image in Normal Method
         /*   String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            iv_Profile_pic = (ImageView) findViewById(R.id.circleView_profile_pic);
            iv_Profile_pic.setImageBitmap(BitmapFactory.decodeFile(picturePath));*/

        }else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK  && null != data ){
            Uri uri = data.getData();
            //Using Picasso Library
            Picasso.with(this)
                    .load(uri)
                    .into(iv_Profile_pic);
        }
    }
}
