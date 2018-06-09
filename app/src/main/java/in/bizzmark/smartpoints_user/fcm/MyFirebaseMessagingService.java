package in.bizzmark.smartpoints_user.fcm;

/**
 * Created by Venu on 1/6/2016.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import in.bizzmark.smartpoints_user.NavigationActivity;
import in.bizzmark.smartpoints_user.R;
import in.bizzmark.smartpoints_user.database.PointsActivity;
import in.bizzmark.smartpoints_user.login.LoginActivity;
import in.bizzmark.smartpoints_user.utility.Utility;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";
    private int mNotificationType;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> params = remoteMessage.getData();

        if (params.size() > 0) {

            //Calling method to generate notification
                JSONObject obj=new JSONObject(params);
                String message="";
                String title="Smart Points";
            try {
                JSONObject response=new JSONObject(obj.getString("message"));
                message=response.optString("status_type");
                message="Transaction "+message;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(Utility.isAppIsInBackground(this))
                sendNotification(title,message);
            else
            {
                Intent i = new Intent("some_custom_id");
                i.putExtra("data", obj.toString());
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
            }

        } else {
            Log.e(TAG, "Push Failure:: ");
        }
    }


    private void sendNotification(String title, String messageBody) {
        PendingIntent pendingIntent = null;

            Intent intent = new Intent(this, PointsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;
        notificationBuilder= new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.smartpoints_logo)
                    .setContentTitle(title)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.smartpoints_logo))
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

    }


}