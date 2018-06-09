package in.bizzmark.smartpoints_user.earnredeemtab;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;

import in.bizzmark.smartpoints_user.EarnRedeemActivity;
import in.bizzmark.smartpoints_user.wifidirect.WiFiDirectActivity;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Scanner_Fragment extends Fragment implements ZXingScannerView.ResultHandler {

    ZXingScannerView mzXingScannerView;
    private String deviced;
    private String IMEIstring;
    private String imeiString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deviced = getIMEIstring();

    }

    public String getIMEIstring() {
        imeiString = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getActivity()
                            .getSystemService(Context.TELEPHONY_SERVICE);
            imeiString = telephonyManager.getDeviceId();
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }
        return IMEIstring;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mzXingScannerView = new ZXingScannerView(getActivity());
        mzXingScannerView.setFocusableInTouchMode(true);
        mzXingScannerView.requestFocus();
        mzXingScannerView.setAutoFocus(true);
        return mzXingScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mzXingScannerView.setResultHandler(this);
        mzXingScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mzXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {

        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION,100);
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
        if (result != null){
            Intent i = new Intent(getActivity(), WiFiDirectActivity.class);
            i.putExtra("key_store_name",result.toString());
            startActivity(i);
        }else {
            Toast.makeText(getActivity(), "scan again", Toast.LENGTH_SHORT).show();
        }
    }
}
