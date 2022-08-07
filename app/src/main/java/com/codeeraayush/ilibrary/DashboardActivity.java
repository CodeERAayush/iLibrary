package com.codeeraayush.ilibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.codeeraayush.ilibrary.databinding.ActivityDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {
private ActivityDashboardBinding binding;


private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase
        firebaseAuth=FirebaseAuth.getInstance();

        //get user name and email
        checkUser();

        //logout btn
        binding.logoutBtnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser==null){
            //not logged in goto main screen
            startActivity(new Intent(DashboardActivity.this,MainActivity.class));
            finish();
        }
        else
        {
            //logged in , get user info
            String email=firebaseUser.getEmail();
            binding.emailUser.setText(email);
        }

    }
}