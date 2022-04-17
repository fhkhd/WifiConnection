package com.example.wificonnection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private Button listen_btn , send_btn , deviseslist_btn ;
    private EditText mssg_ed;
    TextView connection_mode_tv , txt_message , nofind_tv;
    private ListView listView_devices;
    private String string ,st_message;


    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;

    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    private List<WifiP2pDevice> peers = new ArrayList<>();
    private String[] deviceNameArray;

    private WifiP2pDevice[] deviceArrey;

    private Socket socket;

    Serverclas serverclas;
    ClientClass clientClass;

    boolean ishost;
    int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initID();
        initWifiP2p();
    }


    private void initID(){
        listen_btn = findViewById(R.id.listen_btn);
        send_btn = findViewById(R.id.send_btn);
        deviseslist_btn = findViewById(R.id.deviseslist_btn);
        mssg_ed = findViewById(R.id.mssg_ed);
        connection_mode_tv = findViewById(R.id.connection_mode_tv);
        txt_message = findViewById(R.id.txt_message);
        listView_devices = findViewById(R.id.listView_devices);
        nofind_tv = findViewById(R.id.nofind_tv);
    }

    private void initWifiP2p(){
        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(this, getMainLooper(), null);
//        broadcastReceiver = new WiFiDirectBroadcastReceiver(wifiP2pManager ,channel , this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public  class Serverclas extends Thread{
        ServerSocket serverSocket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        @Override
        public void run(){
            try {
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

            }catch (IOException e){
                e.printStackTrace();
            }

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer = new byte[1024];
                    int bytes;

                    while (socket != null){
                        try {
                            bytes = inputStream.read(buffer);
                            if (bytes > 0){
                                int finalBytes = bytes;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String tempMSG = new String(buffer , 0 , finalBytes);
                                        txt_message.setText(tempMSG);
                                        mssg_ed.setText("");
                                    }
                                });
                            }
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public class ClientClass extends Thread{
        String hostAdd;
        private InputStream inputStream;
        private OutputStream outputStream;

        public ClientClass(InetAddress hostAddress){
            hostAdd = hostAddress.getHostAddress();
            socket = new Socket();
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        @Override
        public void run(){
            try {
                socket.connect(new InetSocketAddress(hostAdd , 8888) , 500);
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            }catch (IOException e){
                e.printStackTrace();
            }

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer = new byte[1024];
                    int bytes;

                    while (socket != null){
                        try {
                            bytes = inputStream.read(buffer);
                            if (bytes >0){
                                int finalbytes = bytes;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String tempMSG = new String(buffer, 0 ,finalbytes);
                                        txt_message.setText(tempMSG);
                                        mssg_ed.setText("");
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            if (!wifiP2pDeviceList.equals(peers)){

                peers.clear();
                peers.addAll(wifiP2pDeviceList.getDeviceList());

                deviceNameArray = new String[wifiP2pDeviceList.getDeviceList().size()];
                deviceArrey = new WifiP2pDevice[wifiP2pDeviceList.getDeviceList().size()];

                int index = 0 ;
                for (WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()){
                    deviceNameArray[index] = device.deviceName;
                    deviceArrey[index] = device;
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1 , deviceNameArray);
            listView_devices.setAdapter(adapter);

            if (peers.size() == 0){
                connection_mode_tv.setText("no Device found");
                return;
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner)
            {
                connection_mode_tv.setText("host");
                ishost = true;
                serverclas = new Serverclas();
                serverclas.start();
            }else if (wifiP2pInfo.groupFormed){

                connection_mode_tv.setText("client");
                ishost = false;
                clientClass = new ClientClass(groupOwnerAddress);
                clientClass.start();
            }
        }
    };
}