package com.codeeraayush.ilibrary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.codeeraayush.ilibrary.activities.DashboardActivity;
import com.codeeraayush.ilibrary.activities.MainActivity;
import com.codeeraayush.ilibrary.databinding.ActivityCheckVerificationBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class checkVerification extends AppCompatActivity {
        private ActivityCheckVerificationBinding binding;
        private FirebaseUser firebaseUser;
        private FirebaseAuth firebaseAuth;
        ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCheckVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        firebaseAuth = FirebaseAuth.getInstance();




        loadDetails();

        binding.verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseUser=firebaseAuth.getCurrentUser();

                if(firebaseUser.isEmailVerified()){
                    startActivity(new Intent(checkVerification.this, DashboardActivity.class));
                    finish();
                }else{
                    verifyUser();
                }
            }
        });
        binding.enterDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseUser=firebaseAuth.getCurrentUser();
                if(firebaseUser.isEmailVerified()){
                    startActivity(new Intent(checkVerification.this, DashboardActivity.class));
                    finish();
                }else{
                    Toast.makeText(checkVerification.this, "Verify User First!", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    private void verifyUser() {
        progressDialog.setMessage("Sending Email Verification Message to Your Email"+firebaseUser.getEmail());
        progressDialog.show();

        firebaseUser
                .sendEmailVerification()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
//successfully sent
                        progressDialog.dismiss();
                        Toast.makeText(checkVerification.this, "Check Your Email!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressDialog.dismiss();
                        Toast.makeText(checkVerification.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });



    }

    private void loadDetails() {


        firebaseUser=firebaseAuth.getCurrentUser();

        if(firebaseUser.isEmailVerified()){
            binding.verificationStatus.setText("Verified!");
        }
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String email=snapshot.child("email").toString();
                        String name=snapshot.child("name").getValue().toString();



                        //loading details to the textView
                        binding.userName.setText(name);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(checkVerification.this, MainActivity.class));
        finish();
    }
}