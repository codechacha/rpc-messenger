package com.example.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by mjs on 2017-03-04.
 */

public class RemoteService extends Service{
    private final String TAG = "RemoteService";
    public static final int MSG_CLIENT_CONNECT = 1;
    public static final int MSG_CLIENT_DISCONNECT = 2;
    public static final int MSG_ADD_VALUE = 3;
    public static final int MSG_ADDED_VALUE = 4;

    private ArrayList<Messenger> mClientCallbacks = new ArrayList<Messenger>();
    final Messenger mMessenger = new Messenger( new CallbackHandler());
    int mValue = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private class CallbackHandler  extends Handler {
        @Override
        public void handleMessage( Message msg ){
            switch( msg.what ){
                case MSG_CLIENT_CONNECT:
                    Log.d(TAG, "Received MSG_CLIENT_CONNECT message from client");
                    mClientCallbacks.add(msg.replyTo);
                    break;
                case MSG_CLIENT_DISCONNECT:
                    Log.d(TAG, "Received MSG_CLIENT_DISCONNECT message from client");
                    mClientCallbacks.remove(msg.replyTo);
                    break;
                case MSG_ADD_VALUE:
                    Log.d(TAG, "Received message from client: MSG_ADD_VALUE");
                    mValue += msg.arg1;
                    for (int i = mClientCallbacks.size() - 1; i >= 0; i--) {
                        try{
                            Log.d(TAG, "Send MSG_ADDED_VALUE message to client");
                            Message added_msg = Message.obtain(null, RemoteService.MSG_ADDED_VALUE);
                            added_msg.arg1 = mValue;
                            mClientCallbacks.get(i).send(added_msg);
                        }
                        catch( RemoteException e){
                            mClientCallbacks.remove( i );
                        }
                    }
                    break;
            }
        }
    }


}
