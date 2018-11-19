package com.example.nico.myfirstapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private ListView listView;
    private Button buttonScan;
    private Button buttonValidate;
    private int size =0;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private MyAdapter myAdapter;
    private HashMap<String,Point> knownBSSIDs;
    private HashMap<String, ArrayList> mesuredValues;
    private Semaphore semaphore;
    private ArrayList<Tuple> tuples;

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
        buttonValidate = findViewById(R.id.validateBtn);
        buttonValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
        listView = findViewById(R.id.wifiList);
        listView.setItemsCanFocus(true);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        if(!wifiManager.isWifiEnabled()){
            Toast.makeText(this,"Wifi disabled", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        tuples = new ArrayList<>();
        myAdapter = new MyAdapter(this, tuples);
        listView.setAdapter(myAdapter);
        scanWifi();

    }

    public void validate(){

    }

    private void setKnownBSSIDs() {
        //adds the known bssids and their positions

        //knownBSSIDs = new HashMap<>();
        //knownBSSIDs.put()

        calibrateBSSID();
    }

    private void calibrateBSSID() {
        for(ScanResult scanResult : results) {
            //if bssid appartient à la liste des bssid connus
            //TODO remettre ça une fois les bornes identifiées
            //          if(knownBSSIDs.keySet().contains(scanResult.wifiInfo)) {
            //ajoute le tuple dans une liste

            if (mesuredValues.get(scanResult.BSSID) != null) {
                ArrayList<Integer> list = new ArrayList<>();
                list.add(scanResult.level);
                mesuredValues.put(scanResult.BSSID, list);
            } else {
                mesuredValues.get(scanResult.BSSID).add(scanResult.level);
            }
        }



        TreeMap<Integer, String> sorted = sortHighestBSSID();
        for(Map.Entry<Integer, String> map: sortHighestBSSID().descendingMap().entrySet()){
            arrayList.add(map.getValue()+" "+map.getKey());
            myAdapter.notifyDataSetChanged();
        }
        //String wifiInfo = getHighestBSSID().getKey();
        //bouton monbouton
        //texte bssid
        //TextView bssidText = (TextView)findViewById(R.id.wifiInfo);
        //texte level
        //texte endoir où on est
    }


    private  TreeMap<Integer, String> sortHighestBSSID() {
        TreeMap<Integer, String> highestRSSI = new TreeMap<>();

        for (Map.Entry<String,ArrayList> map: mesuredValues.entrySet()) {
            int i = Collections.max((ArrayList<Integer>)map.getValue());
            highestRSSI.put(i,map.getKey());
        }
        Map.Entry<String, Integer> closestAccessPoint = null;

        for(Map.Entry<Integer,String> entry : highestRSSI.entrySet() ){
            tuples.add(new Tuple(entry.getValue()+" "+entry.getKey(), "", "0"));
            myAdapter.notifyDataSetChanged();

        }
        /*List<Integer> values = new ArrayList<>(highestRSSI.values());
        List<String> keys = new ArrayList<>(highestRSSI.keySet());
        Collections.sort(values);
        Collections.sort(keys);

        HashMap<String, Integer> sortedMap = new HashMap<>();

        Iterator<Integer> valueIt = values.iterator();
        while (valueIt.hasNext()) {
            Integer val = valueIt.next();
            Iterator<String> keyIt = keys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                Integer comp1 = highestRSSI.get(key);
                Integer comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        */

        /*
        for (Map.Entry<String, Integer> entry : highestRSSI.entrySet())
        {
            if (closestAccessPoint == null || entry.getValue().compareTo(closestAccessPoint.getValue()) > 0)
            {
                closestAccessPoint = entry;
            }
        }
        */

        return highestRSSI;
    }

    private void scanWifi(){
        arrayList.clear();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        //clear la liste des resuts des scans
        mesuredValues = new HashMap<>();
        mesuredValues.clear();
        //scan 10 fois
 //       List<ListenableFuture<ScanResult>> list = new ArrayList<>();
 /*       for(int i=0;i<10;i++) {

            ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
            ListenableFuture<ScanResult> futureTask = (ListenableFuture<ScanResult>) executor.submit(new Runnable() {
                @Override
                public void run() {
*/                    wifiManager.startScan();
/*                }
            });
            list.add(futureTask);
        }
        //une fois le scan fini
        //calculer la position
        ListenableFuture<List<ScanResult>> future = Futures.allAsList(list);
        Futures.addCallback(future, new FutureCallback<List<ScanResult>>() {
            @Override
            public void onSuccess(@Nullable List<ScanResult> result) {
                setKnownBSSIDs(result);
                //findPosition(result);
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getApplicationContext() , "Scan failed", Toast.LENGTH_SHORT).show();
            }
        });
*/        Toast.makeText(this, "Scanning wifi...", Toast.LENGTH_SHORT).show();
    }

    private void findPosition() {
        //recup les mesuredValues
        //calcule les distances
        //triangule
    }

    private void findPosition(List<ScanResult> result){

    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);

            for(ScanResult scanResult : results){
                //if bssid appartient à la liste des bssid connus
                //TODO remettre ça une fois les bornes identifiées
    //          if(knownBSSIDs.keySet().contains(scanResult.wifiInfo)) {
                //ajoute le tuple dans une liste
                  if(mesuredValues.get(scanResult.BSSID) != null){
                        mesuredValues.get(scanResult.BSSID).add(scanResult.level);
                    }
                    else{
                        ArrayList<Integer> list = new ArrayList<>();
                        list.add(scanResult.level);
                        mesuredValues.put(scanResult.BSSID, list);
                    }
     //         }
                //on peut recup le bssid
                //arrayList.add(scanResult.wifiInfo);
                //la Received Signal Strength
                //arrayList.add(""+scanResult.level);

                //for each result
                //si le bssid est dans ma liste des bornes connues
                //calculer la distance
                //ajouter à la liste de la position actuelle le bssid et la position


            }
            setKnownBSSIDs();
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
