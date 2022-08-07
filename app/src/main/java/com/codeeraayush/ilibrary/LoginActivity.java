package com.codeeraayush.ilibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.codeeraayush.ilibrary.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
private ActivityLoginBinding binding;


//firebase auth
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });


    }
    private String email="",password="";
    private void validateData() {
        //get data
        email=binding.userLog.getText().toString().trim();
        password=binding.passLog.getText().toString().trim();

        //Validate data
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())binding.userLog.setError("invalid pattern");
        else if(TextUtils.isEmpty(password))binding.passLog.setError("Enter password");
        else LoginUser();
    }

    private void LoginUser() {
        progressDialog.setTitle("Logging in...");
        progressDialog.show();


        //login
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //login sucess
                //check is the user is user or Admin
                checkUser();
                
                
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //login faild;
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUser() {
        //check is the user is user or Admin from realtime database
        //Get current user
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

        //check in database
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("users");
        ref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userType=""+snapshot.child("userType").getValue();
                        if(userType.equals("user")){
                            //this is a simple user , open user dashboard
                            startActivity(new Intent(LoginActivity.this,DashboardActivity.class));
                            finish();
                        }
                        else if(userType.equals("admin")){
                            //this is an admin // open admin dashboard
                            startActivity(new Intent(LoginActivity.this,DashboardAdminActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
;
                    }
                });


    }
}