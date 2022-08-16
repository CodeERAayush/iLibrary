package com.codeeraayush.ilibrary.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.codeeraayush.ilibrary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {
FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

firebaseAuth=FirebaseAuth.getInstance();
        //Start main Activity after 2 second

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUser();
            }
        },2000);

    }

    private void checkUser() {
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser==null){
            //Start new intent
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
        else{
            //user logged in automatically , check user type
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
            reference.child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String userTyp=""+snapshot.child("userType").getValue();

                            if(userTyp.equals("user")){
                                startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                                finish();
                            }
                            else if(userTyp.equals("admin")){
                                startActivity(new Intent(SplashActivity.this,DashboardActivity.class));
                                finish();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }

    }
}