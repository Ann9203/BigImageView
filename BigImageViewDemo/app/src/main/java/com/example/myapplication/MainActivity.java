package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private BigView bigView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bigView = findViewById(R.id.bigView);
        InputStream in = null;
        try {
            in = getAssets().open("1.jpg");
            bigView.setImage(in);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ;
            }
        }

    }
}
