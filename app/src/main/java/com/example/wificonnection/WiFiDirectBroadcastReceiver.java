package com.example.wificonnection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import java.nio.channels.Channel;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {


    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity activity;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       MainActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();


        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int status = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            if(status == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
                Toast.makeText(activity.getApplicationContext(), "WiFi is ON", Toast.LENGTH_SHORT).show();

            else Toast.makeText(activity.getApplicationContext(), "WiFi is OFF", Toast.LENGTH_SHORT).show();

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers

            if(manager != null) manager.requestPeers(channel, activity.peerListListener);

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections

            if(manager == null) return;
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if(networkInfo.isConnected()) manager.requestConnectionInfo(channel, activity.connectionInfoListener);
            else activity.connection_mode_tv.setText("Devicd Disconnected");

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }


    }
}
