package com.example.nico.myfirstapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private ListView listView;
    private Button buttonScan;
    private int size =0;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter myAdapter;
    private HashMap<String,Point> knownBSSIDs;
    private HashMap<String, ArrayList> mesuredValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setKnownBSSIDs();
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

    private void setKnownBSSIDs() {
        //adds the known bssids and their positions

        //knownBSSIDs = new HashMap<>();
        //knownBSSIDs.put()

        calibrateBSSID();
    }

    private void calibrateBSSID() {
        String BSSID = getHighestBSSID().getKey();
    }

    private  Map.Entry<String, Integer> getHighestBSSID() {
        HashMap<String,Integer> highestRSSI = new HashMap<>();

        for (Map.Entry<String,ArrayList> map: mesuredValues.entrySet()) {
            int i = Collections.max((ArrayList<Integer>)map.getValue());
            highestRSSI.put(map.getKey(), i);
        }
        Map.Entry<String, Integer> closestAccessPoint = null;

        for (Map.Entry<String, Integer> entry : highestRSSI.entrySet())
        {
            if (closestAccessPoint == null || entry.getValue().compareTo(closestAccessPoint.getValue()) > 0)
            {
                closestAccessPoint = entry;
            }
        }
        return closestAccessPoint;
    }

    private void scanWifi(){
        arrayList.clear();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        //clear la liste des resuts des scans
        mesuredValues.clear();
        //scan 10 fois
        for(int i=0;i<10;i++) {
            ListenableFuture future = new ListenableFuture() {
                @Override
                public void addListener(Runnable listener, Executor executor) {

                }

                @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    return false;
                }

                @Override
                public boolean isCancelled() {
                    return false;
                }

                @Override
                public boolean isDone() {
                    return false;
                }

                @Override
                public Object get() throws ExecutionException, InterruptedException {
                    return null;
                }

                @Override
                public Object get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
                    return null;
                }
            };
            wifiManager.startScan();
        }
        //une fois le scan fini
        //calculer la position
        findPosition();
        Toast.makeText(this, "Scanning wifi...", Toast.LENGTH_SHORT).show();
    }

    private void findPosition() {
        //recup les mesuredValues
        //calcule les distances
        //triangule
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);

            for(ScanResult scanResult : results){
                //if bssid appartient à la liste des bssid connus
                //TODO remettre ça une fois les bornes identifiées
    //          if(knownBSSIDs.keySet().contains(scanResult.BSSID)) {
                //ajoute le tuple dans une liste
                  if(mesuredValues.get(scanResult.BSSID) != null){
                        ArrayList<Integer> list = new ArrayList<>();
                        list.add(scanResult.level);
                        mesuredValues.put(scanResult.BSSID,list);
                    }
                    else{
                        mesuredValues.get(scanResult.BSSID).add(scanResult.level);
                    }
     //         }
                //on peut recup le bssid
                //arrayList.add(scanResult.BSSID);
                //la Received Signal Strength
                //arrayList.add(""+scanResult.level);

                //for each result
                //si le bssid est dans ma liste des bornes connues
                //calculer la distance
                //ajouter à la liste de la position actuelle le bssid et la position


                arrayList.add(scanResult.BSSID+" "+scanResult.SSID+" "+scanResult.level+"dBm, distance: "+calculateDistance(scanResult.level));
                myAdapter.notifyDataSetChanged();
            }
        }
    };


    //scan 10 times, record the 10 mesuredValues, average
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
