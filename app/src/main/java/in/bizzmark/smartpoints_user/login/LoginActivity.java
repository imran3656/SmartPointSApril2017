package in.bizzmark.smartpoints_user.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.bizzmark.smartpoints_user.NavigationActivity;
import in.bizzmark.smartpoints_user.R;

import static in.bizzmark.smartpoints_user.NavigationActivity.device_Id;
import static in.bizzmark.smartpoints_user.utility.UrlUtility.LOGIN_URL;

public class LoginActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private ImageView imageView_back_arrow;
    private TextView tvSignup,tvForgotPassword;
    private TextView tvSkip;

    private EditText etEmail,etPassword;
    private CheckBox checkBox_Password;
    private Button btnLogin;
    private String email,password;
    private ProgressDialog progressDialog;

    private Animation animBounce;

    private String status, name, mobile, mail, dob, gender, error_message, user_type;
    private String access_token;

    CheckInternet checkInternet = new CheckInternet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // find all ids
        findViewByAllId();

        // Load the animation
        animBounce = AnimationUtils.loadAnimation(this,R.anim.bounce);
    }

    private void findViewByAllId() {
        imageView_back_arrow = (ImageView) findViewById(R.id.login_back_arrow);
        etEmail = (EditText) findViewById(R.id.et_email_login);
        etPassword = (EditText) findViewById(R.id.et_password_login);
        tvForgotPassword = (TextView) findViewById(R.id.tv_forgot_password);
        checkBox_Password = (CheckBox) findViewById(R.id.checkbox_password_login);

        btnLogin = (Button) findViewById(R.id.btn_login);
        tvSignup = (TextView) findViewById(R.id.tv_sign_up);

        tvSkip = (TextView) findViewById(R.id.tv_skip);
        tvSkip.setOnClickListener(this);

        imageView_back_arrow.setOnClickListener(this);
        checkBox_Password.setOnCheckedChangeListener(this);
        tvForgotPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        tvSignup.setOnClickListener(this);
    }


    @Override
    public void onClick(final View v) {
        int i = 0;
        int id = v.getId();
        switch (id){
            case R.id.login_back_arrow: {
                finish();
            }break;
            case R.id.tv_forgot_password: {
                startActivity(new Intent(this, ResetPasswordActivity.class));
                finish();
            }break;
            case R.id.btn_login: {
                btnLogin.startAnimation(animBounce);
                if (checkInternet.isInternetConnected(this)) {
                    userLogin(v);
                    return;
                }else {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            } break;
            case R.id.tv_sign_up: {
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
            } break;
            case R.id.tv_skip: {
                // startActivity(new Intent(this,NavigationActivity.class));
                if (i == 0) {
                    tvSkip.setVisibility(View.GONE);
                    finish();
                    tvSkip.startAnimation(animBounce);
                }
            }break;
        }
    }

    // for user login
    private void userLogin(View v) {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString().trim();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait loging in.....");

        String emailValidation = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";

        // Check email empty
        if (TextUtils.isEmpty(email)){
            Snackbar.make(v,"Please enter email or username", Snackbar.LENGTH_SHORT).show();
            // Check email validation
        }else if (!email.matches(emailValidation)){
            Snackbar.make(v,"Invalid email address", Snackbar.LENGTH_SHORT).show();
            // Check password empty
        }else if (TextUtils.isEmpty(password)){
            Snackbar.make(v,"Please enter password", Snackbar.LENGTH_SHORT).show();
        }else if (password.length() < 8 || password.length() > 16){
            Snackbar.make(v,"Password must be 8 to 16 characters", Snackbar.LENGTH_SHORT).show();
        }else {
            progressDialog.show();
            // do login
            StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                status = jsonObject.getString("status_type");

                                if (status.equalsIgnoreCase("success")){
                                    Toast.makeText(LoginActivity.this, "success", Toast.LENGTH_SHORT).show();
                                    finish();

                                    access_token = jsonObject.getString("access-token");
                                    user_type = jsonObject.getString("usertype");
                                    name = jsonObject.getString("name");
                                    mobile = jsonObject.getString("mobile");
                                    dob = jsonObject.getString("date_of_birth");
                                    gender = jsonObject.getString("gender");
                                    mail = jsonObject.getString("email");

                                    // save details in sharedPreferences
                                    SharedPreferences.Editor editor = getApplication().getSharedPreferences("USER_DETAILS", Context.MODE_PRIVATE).edit();
                                    editor.putString("access_token", access_token);
                                    editor.putString("name", name);
                                    editor.putString("email", mail);
                                    editor.putString("mobile", mobile);
                                    editor.putString("dob", dob);
                                    editor.putString("gender", gender);
                                    editor.commit();

                                }else if (status.equalsIgnoreCase("error")){
                                    error_message = jsonObject.getString("response");
                                    Toast.makeText(LoginActivity.this, error_message, Toast.LENGTH_SHORT).show();
                                    etPassword.setText("");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                    etPassword.setText("");
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("email" , email);
                    parameters.put("password", password);
                    parameters.put("deviceId", device_Id);
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
    }

    // Password hide and show
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked){
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
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
