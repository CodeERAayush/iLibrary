package com.codeeraayush.ilibrary.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codeeraayush.ilibrary.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
private EditText emailReg,passReg,confReg,nameLog;
private Button reg;

//fireBase auth
    private FirebaseAuth firebaseAuth;

    //progress dialogue
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        emailReg=findViewById(R.id.emailReg);
        passReg=findViewById(R.id.passReg);
        confReg=findViewById(R.id.confReg);
        nameLog=findViewById(R.id.nameLog);
        reg=findViewById(R.id.reg);


        //init firebase auth
        firebaseAuth=FirebaseAuth.getInstance();

        //setup progress dialogue
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }
    private String name="",email="",password="";

    private void validateData() {

        //get data
        name =nameLog.getText().toString().trim();
        email=emailReg.getText().toString().trim();
        password=passReg.getText().toString().trim();
        String cPass=confReg.getText().toString().trim();

        //Validate data
        if (TextUtils.isEmpty(name)) {
nameLog.setError("Enter name");
//            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())emailReg.setError("invalid pattern");
        else if(TextUtils.isEmpty(password))passReg.setError("Enter password");
        else if(TextUtils.isEmpty(cPass))confReg.setError("confirm password!")
                ;
    else if(!password.equals(cPass)){
        confReg.setError("pass not matched!");
        }
    else createUserAccount();
    }

    private void createUserAccount() {
        //show progressDialogue
        progressDialog.setMessage("Creating Account......");
        progressDialog.show();

        //creating user in firebase
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //account created , now add in firebase realtime database
                updateUserInfo();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserInfo() {
        progressDialog.setMessage("Saving Info.......");

        //timeStamp
        long timeStamp =System.currentTimeMillis();

        //get current user uid,since used is registered so we can get now
        String uid=firebaseAuth.getUid();

        //setup data to add in database
        HashMap<String , Object>hashMap=new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("email",email);
        hashMap.put("name",name);
        hashMap.put("profileImage","");//will do later
        hashMap.put("userType","user");//possible values are user and admin:will make admin manually in firebase realtime database by changing this valu\e
        hashMap.put("timestamp",timeStamp);


        //set data to db
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference(
                "users"
        );
        ref.child(uid).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //data added to db
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Account Created...", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}