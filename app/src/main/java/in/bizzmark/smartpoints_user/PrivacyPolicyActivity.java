package in.bizzmark.smartpoints_user;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class PrivacyPolicyActivity extends AppCompatActivity {

    ImageView iv_Privacy_Policy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);


        iv_Privacy_Policy = (ImageView) findViewById(R.id.privacy_policy__back_arrow);
        iv_Privacy_Policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
