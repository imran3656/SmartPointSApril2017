package in.bizzmark.smartpoints_user.store;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.bizzmark.smartpoints_user.R;
import in.bizzmark.smartpoints_user.adapter.TransactionAdapter;

/**
 * Created by User on 02-May-17.
 */

public class TransactionFrag extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    TransactionAdapter transactionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout_transaction_list);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager_transaction_list);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            transactionAdapter = new TransactionAdapter(getActivity(),getChildFragmentManager());
        }
        transactionAdapter.addFragments(new EarnTransaction(), "EARN");
        transactionAdapter.addFragments(new RedeemTransaction(), "REDEEM");

        viewPager.setAdapter(transactionAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(Color.BLACK, Color.WHITE);
    }
}
