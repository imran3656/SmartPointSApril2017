// Copyright 2011 Google Inc. All Rights Reserved.

package in.bizzmark.smartpoints_user.wifidirect;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class FileTransferService extends IntentService {

    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_FILE = "SEND_DATA";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
    public static final String MESSAGE = "message";

    private static FileTransferService instance = null;

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    public FileTransferService(String name) {
        super(name);
    }

    public FileTransferService() {
        super("FileTransferService");
    }

    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent.getAction().equals(ACTION_SEND_FILE)) {
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);

            Socket socket = new Socket();
            OutputStream outputStream = null;
            InputStream inputStream = null;

            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);

            try {

                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

                /*returns an output stream to write data into this socket*/
                outputStream = socket.getOutputStream();
                String message = intent.getExtras().getString(MESSAGE);
                outputStream.write(message.getBytes());
                //  outputStream.flush();


            }catch (NullPointerException e){
                e.printStackTrace();
            } catch (IOException e) {

                Log.e("bizzmark", e.getMessage());
                showToast("Error opening client socket. Ask seller to refresh.");

            } finally {

                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
            }

        }




    }


    public void showToast(String message) {
        final String msg = message;
        new Handler(Looper.getMainLooper())
                .post(
                        new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            }
                        });
    }


}
