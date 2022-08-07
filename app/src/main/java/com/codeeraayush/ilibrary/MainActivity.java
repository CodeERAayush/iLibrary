package com.codeeraayush.ilibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
Button Login , Skip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
Login=findViewById(R.id.loginBtn);
Skip=findViewById(R.id.skipBtn);

Login.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
startActivity(new Intent(MainActivity.this,LoginActivity.class));
    }
});
Skip.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

    }
});


    }
}