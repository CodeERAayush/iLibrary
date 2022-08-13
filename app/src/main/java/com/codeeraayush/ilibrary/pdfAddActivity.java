package com.codeeraayush.ilibrary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codeeraayush.ilibrary.databinding.ActivityPdfAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class pdfAddActivity extends AppCompatActivity {
private ActivityPdfAddBinding binding;

    //TAG for debugging
    private static final String TAG="ADD_PDF_TAG";

//firebase Auth
    private FirebaseAuth firebaseAuth;


    //progressDialogue
    private ProgressDialog progressDialog;

    //arrayList to store pdf categories
    private ArrayList<String> categoryTitleArrayList,categoryIdArrayList;


    private Uri pdf_uri=null;

    //PdF pic code
    private static final int PDF_PICK_VAL=1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPdfAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setup progress dialogue
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);



        //init firebase
        firebaseAuth=FirebaseAuth.getInstance();

            loadPdfCategories();


        //add pdf
        binding.addPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPicIntent();
                
            }
        });

        binding.categorySel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCategory();
            }
        });


        //upload pdf
        binding.uploadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

    }
String title="",description="";
    private void validateData() {
title=binding.titleBk.getText().toString().trim();
description=binding.desBk.getText().toString().trim();

//validation
        if(TextUtils.isEmpty(title))binding.titleBk.setError("Enter Title of the Book!");
        else if(TextUtils.isEmpty(description))binding.desBk.setError("Enter Book description!");
        else if(TextUtils.isEmpty(selectedCategoryTitle))binding.categorySel.setError("Please Select Category First!");
        else if(pdf_uri==null) Toast.makeText(pdfAddActivity.this, "Please select a Pdf file", Toast.LENGTH_SHORT).show();
        else{
            uploadToStorage();
        }
    }

    private void uploadToStorage() {

        //upload pdf to firebase storage
            progressDialog.setMessage("Uploading PDF...");
            progressDialog.show();

            //timestamp
        long timeStamp=System.currentTimeMillis();
//        path of pdf in firebase storage
        String filePathAndName="Books/"+timeStamp;
        //Storage reference
        StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(pdf_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(pdfAddActivity.this, "PDF uploaded Successfully!", Toast.LENGTH_SHORT).show();

                //save pdf info and link to db
                    Task<Uri> task=taskSnapshot.getStorage().getDownloadUrl();
                    while(!task.isSuccessful());
                    String uploadPdfurl=""+task.getResult();

                    //upload to firebase database
                uploadInfoToDb(uploadPdfurl,timeStamp);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(pdfAddActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void uploadInfoToDb(String pdfUrl,long timeStamp) {
        progressDialog.setMessage("Uploading Info to Db...");
        progressDialog.show();

        String uid=firebaseAuth.getUid();

        //Setup data to upload
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("uid",""+uid);
        hashMap.put("id",""+timeStamp);
        hashMap.put("title",""+title);
        hashMap.put("description",""+description);
        hashMap.put("categoryId",""+selectedcategoryId);
        hashMap.put("url",""+pdfUrl);
        hashMap.put("timestamp",timeStamp);

        //db reference
        DatabaseReference dbref= FirebaseDatabase.getInstance().getReference("Books");
        dbref.child(""+timeStamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                      progressDialog.dismiss();
                        Toast.makeText(pdfAddActivity.this, "Pdf Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(pdfAddActivity.this, "Error uploading pdf due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadPdfCategories() {
        Log.d(TAG, "loadPdfCategories: Loading pdf categories...");

        //initialising arraylist
        categoryTitleArrayList =new ArrayList<>();
        categoryIdArrayList=new ArrayList<>();
        //setting db ref to load categories
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear();
                categoryTitleArrayList.clear();
                for(DataSnapshot sn:snapshot.getChildren()){
                    String categoryId=""+sn.child("id").getValue();
                    String categoryTitle=""+sn.child("category").getValue();


                    categoryTitleArrayList.add(categoryTitle);
                    categoryIdArrayList.add(categoryId);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
//select category id and Title
    private String selectedcategoryId,selectedCategoryTitle;

    private void selectCategory() {

        String cateArray[]=new String[categoryTitleArrayList.size()];
        for(int i = 0; i< categoryTitleArrayList.size(); i++){
            cateArray[i]= categoryTitleArrayList.get(i);
        }

        //Alert dialogue to choose between each category
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Pick any one category")
                .setItems(cateArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //handle item clicked
                        //get clicked item from dialogue list]

                        selectedcategoryId=categoryIdArrayList.get(i);
                        selectedCategoryTitle=categoryTitleArrayList.get(i);

                        //set to category textview
                        binding.categorySel.setText(selectedCategoryTitle);


                        //log
                        Log.d(TAG, "onClick: Selected Category"+selectedcategoryId+" "+selectedCategoryTitle);

                    }
                })
        .show();

    }

    private void pdfPicIntent() {

        Log.d(TAG, "pdfPicIntent: Starting Pdf pic intent");

        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Pdf"),PDF_PICK_VAL);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==PDF_PICK_VAL){
                Log.d(TAG, "onActivityResult: PDF picked");
                pdf_uri=data.getData();

                Log.d(TAG, "onActivityResult: URI:"+pdf_uri);
            }
        }else{
            Log.d(TAG, "onActivityResult: cancelled picking pdf");
            Toast.makeText(pdfAddActivity.this, "cancelled picking pdf...", Toast.LENGTH_SHORT).show();
        }
    }
}