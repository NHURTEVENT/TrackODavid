package com.example.nico.myfirstapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private ListView listView;
    private Button buttonScan;
    private int size =0;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Intent intent = new Intent(this, wifiReceiver.getClass());
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        startActivity(intent);*/
        buttonScan = findViewById(R.id.scanBtn);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanWifi();
            }
        });

        listView = findViewById(R.id.wifiList);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);


        if(!wifiManager.isWifiEnabled()){
            Toast.makeText(this,"Wifi disabled", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(myAdapter);
        scanWifi();
    }

    private void scanWifi(){
        arrayList.clear();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this, "Scanning wifi...", Toast.LENGTH_SHORT).show();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);

            for(ScanResult scanResult : results){
                //on peut recup le bssid
                //arrayList.add(scanResult.BSSID);
                //la Received Signal Strength
                //arrayList.add(""+scanResult.level);
                arrayList.add(scanResult.BSSID+" "+scanResult.SSID+" "+scanResult.level+"dBm, distance: "+calculateDistance(scanResult.level));
                myAdapter.notifyDataSetChanged();
            }
        }
    };


    //scan 10 times, record the 10 values, average
    //list and suggest distance, let change the distance or not
    //calibrate new n

    private double calculateDistance(int RSSI){
        //RRSI(d) = RSSI(d0) - 10*n*ln(d) + Xdelta
        //d = exp((RSSI(d)-RSSI(d0)+Xdelta)/10n))
        //on va faire sans delta au début
        double n = 2.6;
        int RSSI0 = -59;
        double X = 0;
        double distance = Math.exp((RSSI-RSSI0)/10*n+X);
        return 0;
    }

    private double calibrateN (int RSSI, double distance){
        int RSSI0 = -59;
        double n = (RSSI - RSSI0)/(10*Math.log(distance));
        return n;
    }
}
