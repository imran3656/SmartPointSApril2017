package in.bizzmark.smartpoints_user.login;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by User on 01-Jun-17.
 */

public class CheckInternet {

    public CheckInternet(){
        // Required empty constructor
    }

    public boolean isInternetConnected(Context context){

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            return true;
        }else {
          //  Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
            return false;
        }
      // return networkInfo != null && networkInfo.isConnected();
    }
}
