package in.bizzmark.smartpoints_user.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by User on 18-May-17.
 */

public class TransactionAdapter extends FragmentPagerAdapter {

    Context ctxt=null;

    ArrayList<Fragment> fragment = new ArrayList<>();
    ArrayList<String> tabTitles = new ArrayList<>();

    public void addFragments(Fragment fragments,String titles){
        this.fragment.add(fragments);
        this.tabTitles.add(titles);
    }

    public TransactionAdapter(Context ctxt, FragmentManager manager) {
        super(manager);
        this.ctxt=ctxt;
    }

    @Override
    public int getCount() {
        return fragment.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragment.get(position);
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }
}
