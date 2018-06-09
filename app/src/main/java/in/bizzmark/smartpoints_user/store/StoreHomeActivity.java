package in.bizzmark.smartpoints_user.store;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.bizzmark.smartpoints_user.R;
import in.bizzmark.smartpoints_user.login.CheckInternet;

/**
 * Created by User on 01-May-17.
 */

public class StoreHomeActivity extends FragmentActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    ImageView ivBackPress;
    BottomNavigationView bottomNavigationView;
    TextView textView;

    public static String branch_Id = null;

    public static String store_Name;
    public static String storeId;

    CheckInternet checkInternet = new CheckInternet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_details);

        Intent i = getIntent();
        branch_Id = i.getStringExtra("branchId");

        // findAllIds
        findViewByAllId();

        if (checkInternet.isInternetConnected(this)) {
            // For Store-Details
            viewStoreDetails();
            return;
        }else {
            // for all transactions
            viewAllTransaction();
        }
    }

    private void findViewByAllId() {
        textView = (TextView) findViewById(R.id.tv);
        ivBackPress = (ImageView) findViewById(R.id.iv_back_arrow_store_details);
        ivBackPress.setOnClickListener(this);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setActivated(true);
        bottomNavigationView.setSelected(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setChecked(true);
        if (item.getItemId() == R.id.store_details){
            viewStoreDetails();
            textView.setText("Store details");
        }else if (item.getItemId() == R.id.transactions){
            viewAllTransaction();
        }else if (item.getItemId() == R.id.offer_promotion){
            viewAllOffersPromotions();
        }

        return false;
    }

    // For all offers-promotions
    private void viewAllOffersPromotions() {
        textView.setText("Offers and promotions");

        OfferPromoFrag offerPromoFrag = new OfferPromoFrag();

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, offerPromoFrag, "fragment");
        ft.addToBackStack(null);
        ft.commit();
    }

    // for all transactions
    private void viewAllTransaction() {
        textView.setText("Transactions");

        TransactionFrag transactionFrag = new TransactionFrag();

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, transactionFrag, "fragment");
        ft.addToBackStack(null);
        ft.commit();
    }

    // for store details
    private void viewStoreDetails() {
        textView.setText("Store details");

        AboutStoreFrag storeFrag = new AboutStoreFrag();

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.framelayout, storeFrag, "fragment");
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        if ( v == ivBackPress){
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
