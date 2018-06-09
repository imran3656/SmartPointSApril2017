package in.bizzmark.smartpoints_user.login;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import in.bizzmark.smartpoints_user.R;

import static in.bizzmark.smartpoints_user.NavigationActivity.device_Id;
import static in.bizzmark.smartpoints_user.utility.UrlUtility.REGISTER_URL;

public class RegisterActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private ImageView imageView_back_arrow;
    private TextView tvSignin;
    private EditText etUserName, etName, etEmail, etPassword, etConfirmPassword, etMobile, etDOB, etCity;
    private Spinner spnGender;
    private CheckBox checkBox_Password, checkBox_Confirm_Password, cb_Terms_Conditions;
    private Button btnRegister;
    private String username, name, email, password, confirmPassword, mobile, dob, city, gender;
    private int gender_position ;
    private int mYear, mMonth, mDay;
    private ProgressDialog progressDialog;

    CheckInternet checkInternet = new CheckInternet();
    Animation animBounce;

    private boolean checkDOBFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // find all ids
        findViewByAllId();

        // Load Animation
        animBounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
    }

    private void findViewByAllId() {
        imageView_back_arrow = (ImageView) findViewById(R.id.register_back_arrow);
        tvSignin = (TextView) findViewById(R.id.tv_sign_in);

        etUserName = (EditText) findViewById(R.id.et_username);
        etName = (EditText) findViewById(R.id.et_person_name);
        etEmail = (EditText) findViewById(R.id.et_email_register);
        etPassword = (EditText) findViewById(R.id.et_password_register);
        etConfirmPassword = (EditText) findViewById(R.id.et_confirm_password_register);
        etMobile = (EditText) findViewById(R.id.et_mobile);
        etDOB = (EditText) findViewById(R.id.et_dob);
        etCity = (EditText) findViewById(R.id.et_city);

        spnGender = (Spinner) findViewById(R.id.spgender);

        checkBox_Password = (CheckBox) findViewById(R.id.checkbox_password_register);
        checkBox_Confirm_Password = (CheckBox) findViewById(R.id.checkbox_confirm_password_register);
        cb_Terms_Conditions = (CheckBox) findViewById(R.id.cb_terms_conditions);

        btnRegister = (Button) findViewById(R.id.btn_sign_up);

        imageView_back_arrow.setOnClickListener(this);
        tvSignin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        checkBox_Password.setOnCheckedChangeListener(this);
        checkBox_Confirm_Password.setOnCheckedChangeListener(this);
        cb_Terms_Conditions.setOnCheckedChangeListener(this);
        etDOB.setOnClickListener(this);

        spnGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //gender = spnGender.getItemAtPosition(position).toString();
                gender_position = position;
                if (gender_position == 1){
                    gender = "1";
                }else if (gender_position == 2){
                    gender = "0";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // USER SIGNUP
    private void userSignUp(View v) {
        username = etUserName.getText().toString().trim();
        name = etName.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        confirmPassword = etConfirmPassword.getText().toString().trim();
        mobile = etMobile.getText().toString().trim();
        dob = etDOB.getText().toString().trim();
        city = etCity.getText().toString().trim();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait....Registering");

        String userNameValidation = "[a-z_0-9]+";
        String emailValidation = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(username)){
            Snackbar.make(v,"Please enter username", Snackbar.LENGTH_SHORT).show();
        }else if (!username.matches(userNameValidation)){
            etUserName.setError("username must be lowercase letters, underscore and alphanumeric");
        }else if (TextUtils.isEmpty(name)){
            Snackbar.make(v,"Please enter your name", Snackbar.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(email)){
            Snackbar.make(v,"Please enter your email", Snackbar.LENGTH_SHORT).show();
        }else if (!email.matches(emailValidation)){
            Snackbar.make(v,"Invalid email address", Snackbar.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(password)){
            Snackbar.make(v,"Please enter password", Snackbar.LENGTH_SHORT).show();
        }else if (password.length() < 8 || password.length() > 16){
            Snackbar.make(v,"Password must be 8 to 16 character", Snackbar.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(confirmPassword)){
            Snackbar.make(v,"Please enter confirm password", Snackbar.LENGTH_SHORT).show();
        }else if (!password.equalsIgnoreCase(confirmPassword)) {
            Snackbar.make(v,"you entered password do not match", Snackbar.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(mobile)){
            Snackbar.make(v,"Please enter your mobile number", Snackbar.LENGTH_SHORT).show();
        }else if (mobile.length() < 10){
            Snackbar.make(v,"Please enter 10 digits mobile number", Snackbar.LENGTH_SHORT).show();
        }else if (gender_position == 0){
            Snackbar.make(v,"Please select your gender", Snackbar.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(dob)){
            Snackbar.make(v,"Please enter date of birth", Snackbar.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(city)) {
            Snackbar.make(v,"Please enter your city", Snackbar.LENGTH_SHORT).show();
        }else if (!cb_Terms_Conditions.isChecked()){
            Snackbar.make(v,"Please accept term and condition", Snackbar.LENGTH_SHORT).show();
        }else {
            progressDialog.show();
            // Do-SignUp
            StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("status_type");
                                if (status.equalsIgnoreCase("success")){
                                    Toast.makeText(RegisterActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplication(), LoginActivity.class));
                                    finish();
                                }else if (status.equalsIgnoreCase("error")){
                                    String error_message = jsonObject.getString("response");
                                    Toast.makeText(RegisterActivity.this, error_message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Error : "+error.toString(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String,String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("name", name);
                    params.put("email", email);
                    params.put("password", password);
                    params.put("mobile", mobile);
                    params.put("gender", gender);
                    params.put("date_of_birth", dob);
                    params.put("city", city);
                    params.put("customerDeviceId", device_Id);
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

    // for Password hide and show
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == checkBox_Password) {
            if (isChecked) {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        } else if (buttonView == checkBox_Confirm_Password){
            if (isChecked) {
                etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }else if (buttonView == cb_Terms_Conditions){
            // Do something for accept term and condition
            if (isChecked){

            }else {

            }
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.register_back_arrow:
                finish();
                break;
            case R.id.btn_sign_up:
                btnRegister.startAnimation(animBounce);
               if (checkInternet.isInternetConnected(this)){
                   userSignUp(v);
                   return;
               }else {
                   Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
               }
                break;
            case R.id.tv_sign_in:
                startActivity(new Intent(this,LoginActivity.class));
                finish();
                break;
            case R.id.et_dob:
                // For Date-Picker-Dialog
                showDatePickerDialog(v);
                break;
        }
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

                    etDOB.setText(new StringBuilder()
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
    protected void onResume() {
        super.onResume();
        if (checkInternet.isInternetConnected(this)){
            return;
        }else {
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (checkInternet.isInternetConnected(this)){
            return;
        }else {
            return;
        }
    }
}
