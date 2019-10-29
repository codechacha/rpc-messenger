package com.example.messenger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    private Messenger mServiceCallback = null;
    private Messenger mClientCallback = new Messenger(new CallbackHandler());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAddValue = (Button) findViewById(R.id.btn_add_value);
        btnAddValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mServiceCallback != null) {
                    // request 'add value' to service
                    Message msg = Message.obtain(null, RemoteService.MSG_ADD_VALUE);
                    msg.arg1 = 10;
                    try {
                        mServiceCallback.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "Send MSG_ADD_VALUE message to Service");
                }
            }
        });

        Log.d(TAG, "Trying to connect to service");
        Intent intent = new Intent(getApplicationContext(), RemoteService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            mServiceCallback = new Messenger(service);

            // connect to service
            Message connect_msg = Message.obtain( null, RemoteService.MSG_CLIENT_CONNECT);
            connect_msg.replyTo = mClientCallback;
            try {
                mServiceCallback.send(connect_msg);
                Log.d(TAG, "Send MSG_CLIENT_CONNECT message to Service");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            mServiceCallback = null;
        }
    };

    private class CallbackHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RemoteService.MSG_ADDED_VALUE:
                    Log.d(TAG, "Recevied MSG_ADDED_VALUE message from service ~ value :" + msg.arg1);
                    break;
            }
        }
    }
}
