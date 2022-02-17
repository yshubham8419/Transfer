package com.example.transfer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BroadcastListener extends BroadcastReceiver {
    private final MainActivity activity;
    private final WifiP2pManager manager;
    private final WifiP2pManager.Channel channel;

    public BroadcastListener(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       MainActivity wifiDirectActivity) {
        super();
        this.manager=manager;
        this.channel=channel;
        this.activity = wifiDirectActivity;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            //            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            activity.setIsWifiP2pEnabled(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED);

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // The peer list has changed! We should probably do something about
            // that.
            activity.peersChanged();

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed! We should probably do something about
            // that.
            if(manager==null)
                return;
            NetworkInfo networkInfo=(NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(networkInfo.isConnected())
                activity.setText("Connected");
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            

        }
    }
}
