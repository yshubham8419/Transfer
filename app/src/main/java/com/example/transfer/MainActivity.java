package com.example.transfer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    /**
     *total permission required
     */
    private final String[] permissions =
    {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };




    /**
     *Some important variables
     */
    BroadcastListener receiver;
    WifiP2pManager.Channel channel;
    WifiP2pManager manager;
    IntentFilter intentFilter = new IntentFilter();
    private List<WifiP2pDevice> peers = new ArrayList<>();




    /**
     * peerlist change Handler
     */
    @SuppressLint("MissingPermission")
    private final WifiP2pManager.PeerListListener Pll = peerList -> {
        List<WifiP2pDevice> refreshedPeers = new ArrayList<>(peerList.getDeviceList());
        if (!peerList.equals(peers)) {
            peers.clear();
            peers.addAll(refreshedPeers);
            updatePeers();
        }
        if (peers.size() == 0) {
            Log.d("xyz", "failed to discover");
        } else {
            WifiP2pDevice dev = peers.get(0);
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = dev.deviceAddress;
            config.wps.setup = WpsInfo.PBC;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        setText("connection is initiated");
                    }

                    @Override
                    public void onFailure(int i) {
                        setText("failed to initiate connection");
                    }
                });
            }

        }
    };




    /**
     * function to update peerlist on ui
     */
    private void updatePeers() {
        String createdStr = "";
        for (WifiP2pDevice device : peers)
            createdStr += device.deviceName + "\n";

        setText(createdStr);
    }




    /**
     *Entry point of app
     */
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
        Button b=findViewById(R.id.button);
        b.setOnClickListener(view -> initiateDiscovery());

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        initiateDiscovery();
    }




    /**
     * register Listener as the activity resumes
     */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new BroadcastListener(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }




    /**
     * unregister Listener as the activity pauses
     */
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }




    /**
     * notifies the user about p2p enable
     */
    public void setIsWifiP2pEnabled(boolean wifiP2pEnabled) {

        if (!wifiP2pEnabled)
            setText("wifiP2pEnabled" + wifiP2pEnabled);
    }




    /**
     * discovering peers
     */
    public void peersChanged() {
        if (manager != null && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            manager.requestPeers(channel, Pll);
        }
    }




    /**
     * initiating discovery
     */
    public void initiateDiscovery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(int i) {

                }
            });
        }
        else{

        }

    }




    /**
     *
     * setting text
     */
    public void setText(String x) {
        TextView tv = findViewById(R.id.text);
        tv.setText(x);
    }
}