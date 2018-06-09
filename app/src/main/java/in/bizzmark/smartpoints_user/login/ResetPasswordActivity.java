package in.bizzmark.smartpoints_user.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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

/**
 * Created by User on 08-Apr-17.
 */

public class ResetPasswordActivity extends Activity implements View.OnClickListener {
    private TextInputEditText etResetPassword;
    private Button btnSubmit;
    private String email;

    private String FORGOT_PASSWORD_URL;
    private String status_type;
    private String success_msg;
    private String error_msg;
    private ProgressDialog pd;

    private CheckInternet checkInternet = new CheckInternet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        // find all ids
        findViewByAllId();
    }

    private void findViewByAllId() {
        etResetPassword = (TextInputEditText) findViewById(R.id.et_password_reset);
        btnSubmit = (Button) findViewById(R.id.btn_reset_password_submit);

        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      if (v == btnSubmit) {
          if (checkInternet.isInternetConnected(this)) {
              sendResetLink();
              return;
          } else {
              return;
          }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkInternet.isInternetConnected(this)) {
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (checkInternet.isInternetConnected(this)) {
            return;
        } else {
            return;
        }
    }

    private void sendResetLink() {
        pd = new ProgressDialog(this);
        pd.setMessage("Please wait....");
        email = etResetPassword.getText().toString().trim();
        FORGOT_PASSWORD_URL = "http://bizzmark.in/smartpoints/api/request-password-reset?userEmail="+email;

        String emailValidation = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";

        // Check empty field
        if (TextUtils.isEmpty(email)){
            etResetPassword.setError("Empty field");
        }else if (!email.matches(emailValidation)){
            etResetPassword.setError("Invalid email address");
        }else {
            // code for reset password
            pd.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, FORGOT_PASSWORD_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                status_type = jsonObject.getString("status_type");
                                if (status_type.equalsIgnoreCase("success")){
                                    success_msg = jsonObject.getString("response");
                                    finish();
                                    etResetPassword.setText("");
                                    Toast.makeText(ResetPasswordActivity.this, success_msg.toString(), Toast.LENGTH_LONG).show();
                                }else if (status_type.equalsIgnoreCase("error")){
                                    error_msg = jsonObject.getString("response");
                                    Toast.makeText(ResetPasswordActivity.this, error_msg.toString(), Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ResetPasswordActivity.this, "Something went wrong ! please try again", Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    300000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        }
    }
}
