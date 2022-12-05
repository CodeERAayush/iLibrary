package com.codeeraayush.ilibrary.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.codeeraayush.ilibrary.databinding.ActivityAddCategoryBooksBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddCategoryBooks extends AppCompatActivity {
    private ActivityAddCategoryBooksBinding binding;
private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityAddCategoryBooksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait...")
        ;
        progressDialog.setCanceledOnTouchOutside(false);
        binding.subCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }


    private String category="";
    private void validateData() {

        category=binding.addCategory.getText().toString();

        if(TextUtils.isEmpty(category)){
            binding.addCategory.setError("Field can't be empty!");

        }
        else{
            uploadCategoryFirebase();
        }

    }

    private void uploadCategoryFirebase() {
        progressDialog.setMessage(
                "Adding Category..."
        );
        progressDialog.show();

        long TimeStamp=System.currentTimeMillis();

        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("id",""+TimeStamp);
        hashMap.put("category",""+category);
        hashMap.put("timestamp", TimeStamp);
        hashMap.put("uid",""+firebaseAuth.getUid());

        //Storing into database ...... db Root > Categories > categoryId > categoryInfo
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Categories");
        databaseReference.child(""+TimeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(AddCategoryBooks.this, "Category Successfully added !", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddCategoryBooks.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }
}