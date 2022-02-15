package com.example.transfer;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String[] permissions =
            {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
    BroadcastListener receiver;
    WifiP2pManager.Channel channel;
    WifiP2pManager manager;
    IntentFilter intentFilter = new IntentFilter();
    TextView tv = findViewById(R.id.text);
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private final WifiP2pManager.PeerListListener Pll = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            List<WifiP2pDevice> refreshedPeers = (List<WifiP2pDevice>) peerList.getDeviceList();
            if (!peerList.equals(peers)) {
                peers.clear();
                peers.addAll(refreshedPeers);
                updatePeers();
            }
            if (peers.size() == 0) {
                Log.d("", "failed to discover");
            }
        }
    };

    private void updatePeers() {
        String createdStr = "";
        for (WifiP2pDevice device : peers) {
            createdStr += device.deviceName + "\n";
        }
        tv.setText(createdStr);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityResultLauncher<String> Arl = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        });
        for (String x : permissions) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), x) != PackageManager.PERMISSION_GRANTED) {
                Arl.launch(x);
            }
        }


        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new BroadcastListener(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void setIsWifiP2pEnabled(boolean wifiP2pEnabled) {
        if (!wifiP2pEnabled)
            tv.setText("wifiP2pEnabled" + wifiP2pEnabled);
    }

    public void peersChanged() {
            if (manager!=null && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                manager.requestPeers(channel, Pll);
            }
    }
}