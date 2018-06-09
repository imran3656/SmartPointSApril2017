package in.bizzmark.smartpoints_user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by Admin on 8/2/2017.
 */

public class InviteActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton ib_BackArrow;
    EditText etName,etEMail, etMobile, etAddress;
    Button btnInvite;

    String name, email, mobile, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        //Find-All-Ids
        findViewByAllIds();
    }

    //Find-All-Ids
    private void findViewByAllIds() {
        ib_BackArrow = (ImageButton) findViewById(R.id.ib_back);
        ib_BackArrow.setOnClickListener(this);

        etName = (EditText) findViewById(R.id.et_invite_name);
        etEMail = (EditText) findViewById(R.id.et_invite_email);
        etMobile = (EditText) findViewById(R.id.et_invite_mobile);
        etAddress = (EditText) findViewById(R.id.et_invite_address);

        btnInvite = (Button) findViewById(R.id.btn_invite);
        btnInvite.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == ib_BackArrow){
            finish();
        }else if (v == btnInvite){
            // Do-Invite
            sendInvite(v);
        }
    }

    // For-Send-Invitation
    private void sendInvite(View v) {
        try {
            name = etName.getText().toString().trim();
            email = etEMail.getText().toString().trim();
            mobile = etMobile.getText().toString().trim();
            address = etAddress.getText().toString().trim();
            if (TextUtils.isEmpty(name)){
                etName.setError("Name required");
            }else if (TextUtils.isEmpty(email)){
                etEMail.setError("Email required");
            } else if (TextUtils.isEmpty(mobile)){
                etMobile.setError("Mobile number required");
            }else if (TextUtils.isEmpty(address)){
                etAddress.setError("Address required");
            }else {
                Toast.makeText(this, "Name :"+name+"\n"+"Email :"+email+"\n"+"Mobile :"+mobile+"\n"+"Address :"+address, Toast.LENGTH_SHORT).show();
            }

        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
