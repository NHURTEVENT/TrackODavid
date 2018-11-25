package com.example.nico.pt;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    boolean tracked = false;
    int clickcounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = findViewById(R.id.track);
        button.setBackgroundColor(Color.rgb(255,100,100));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tracked) {

                    button.setBackgroundColor(Color.RED);
                    Toast.makeText(getApplicationContext(), "You are now beeing tracked", Toast.LENGTH_SHORT).show();
                    button.setBackgroundColor(Color.rgb(255,100,100));
                    button.setText("Stop beeing tracked");
                    tracked = true;
                }
                else {
                    Toast.makeText(getApplicationContext(), "You aren't tracked anymore", Toast.LENGTH_SHORT).show();
                    button.setText("Be tracked");
                    tracked = false;
                }
            }
        });
        Button buttonSee = findViewById(R.id.see);
        buttonSee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.trackedlist);
                final ImageView img1 = findViewById(R.id.imageView1);
                img1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setContentView(R.layout.trackedlistclicked);
                        final ImageView img2 = findViewById(R.id.imageView2);
                        img2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setContentView(R.layout.trackedlist2);
                                final ImageView img3 = findViewById(R.id.imageView3);
                                img3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        setContentView(R.layout.location);
                                    }
                                });
                            }
                        });
                    }
                });



            }
        });

    }
}
