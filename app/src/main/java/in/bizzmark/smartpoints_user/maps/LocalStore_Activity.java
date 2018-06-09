package in.bizzmark.smartpoints_user.maps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import in.bizzmark.smartpoints_user.R;

public class LocalStore_Activity extends AppCompatActivity {

    ImageView iv_Local_Store_Back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_store);

        iv_Local_Store_Back = (ImageView) findViewById(R.id.btn_back_arrow_local_store);
        iv_Local_Store_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
