package in.bizzmark.smartpoints_user.earnredeemtab;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import in.bizzmark.smartpoints_user.R;

import static android.Manifest.permission.CAMERA;

/**
 * Created by User on 22-Apr-17.
 */

public class QRCodeScannerPermission extends AppCompatActivity {
    public static final int REQUEST_CODE = 100;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_layout);

        if (checkPermissions()){
            Toast.makeText(getApplicationContext(), "Permission already granted", Toast.LENGTH_LONG).show();
        }else {
            requestPermission();
        }

    }

    private boolean checkPermissions() {
        return ( ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA ) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE){
            if (grantResults.length > 0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(CAMERA)){
                            new AlertDialog.Builder(QRCodeScannerPermission.this)
                                    .setMessage("You haven't given us permission to use camera, please enable the permission in setting to start scanning SmartpointS code")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{CAMERA},REQUEST_CODE);
                                            }
                                        }
                                    }).setCancelable(false)
                                    .create()
                                    .show();
                        }
                    }
                }
            }
        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
