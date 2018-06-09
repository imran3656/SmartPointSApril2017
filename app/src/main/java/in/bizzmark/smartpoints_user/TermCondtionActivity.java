package in.bizzmark.smartpoints_user;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class TermCondtionActivity extends AppCompatActivity {

    ImageView iv_Term_And_Condition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term__condtion);

        iv_Term_And_Condition = (ImageView) findViewById(R.id.term_condition__back_arrow);
        iv_Term_And_Condition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
