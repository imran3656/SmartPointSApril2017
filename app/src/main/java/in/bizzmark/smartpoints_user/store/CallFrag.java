package in.bizzmark.smartpoints_user.store;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.bizzmark.smartpoints_user.R;

/**
 * Created by User on 18-May-17.
 */

public class CallFrag extends Fragment {
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_call, container, false);
        return view;
    }
}
