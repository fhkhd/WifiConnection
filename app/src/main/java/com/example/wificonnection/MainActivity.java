package com.example.wificonnection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button listen_btn , send_btn , deviseslist_btn ;
    private EditText mssg_ed;
    private TextView connection_mode_tv , txt_message , nofind_tv;
    private ListView listView_devices;
    private String string ,st_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initID();
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
}