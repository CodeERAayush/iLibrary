package com.codeeraayush.ilibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.codeeraayush.ilibrary.databinding.ActivityPdfAddBinding;
import com.codeeraayush.ilibrary.databinding.ActivityPdfEditBinding;
import com.codeeraayush.ilibrary.models.ModelPdf;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class PdfEditActivity extends AppCompatActivity {
private String bookId;
 private ProgressDialog progressDialog;
private ActivityPdfEditBinding binding;


//arrayList
    ArrayList<String> categoryIdArrayList;
    ArrayList<String> categoryTitleArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfEditBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        bookId = getIntent().getStringExtra("bookId");

        //setup prdlg
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        loadCategories();
        bookInfo();


        //handle click , pick category
        binding.categoryEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryDialog();
            }
        });



        //handle click , upload changes
binding.updateBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        validateBookData();
    }
});

    }


    String Title="",description="";
    private void validateBookData() {
        Title=binding.titleEt.getText().toString().trim();
        description=binding.descriptionEt.getText().toString();


        //validation
        if(TextUtils.isEmpty(Title))binding.titleEt.setError("Enter Book Title");
        else if(TextUtils.isEmpty(description))binding.descriptionEt.setError("Enter Book Description");
        else if(TextUtils.isEmpty(selectedCategoryId))binding.categoryEt.setError("select Category First");
        else{
            updatePdf();
        }

    }

    private void updatePdf() {
        progressDialog.setMessage("Updating Book Info...");
        progressDialog.show();


        //setup data to db
        HashMap<String , Object>hashMap=new HashMap<>();
        hashMap.put("title",""+Title);
        hashMap.put("description",""+description);
        hashMap.put("categoryId",""+selectedCategoryId);


        //start uploading
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(PdfEditActivity.this, "Book Info Updated Successfully ", Toast.LENGTH_SHORT).show();
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PdfEditActivity.this, "Update Faild due to : "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void bookInfo() {
        DatabaseReference bookRef=FirebaseDatabase.getInstance().getReference("Books");
        bookRef.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        selectedCategoryId=""+snapshot.child("categoryId").getValue();
                        String description=""+snapshot.child("description").getValue();
                        String Title=""+snapshot.child("title").getValue();

                        binding.descriptionEt.setText(description);
                        binding.titleEt.setText(Title);

                        DatabaseReference refBookCategory=FirebaseDatabase.getInstance().getReference("Categories");
                        refBookCategory.child(selectedCategoryId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String category=""+snapshot.child("category").getValue();
                                        binding.categoryEt.setText(category);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String selectedCategoryId="",selectedCategoryTitle="";


    public void categoryDialog(){
        //make string array from arraylist
        String []categoriesArray=new String[categoryTitleArrayList.size()];
        for(int i=0;i<categoryTitleArrayList.size();i++){
            categoriesArray[i]=categoryTitleArrayList.get(i);
        }


        //alert dialogue
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Choose Category")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedCategoryTitle=categoryTitleArrayList.get(i);
                        selectedCategoryId=categoryIdArrayList.get(i);


                        //set to textview
                        binding.categoryEt.setText(selectedCategoryTitle);

                    }
                })
                .show();


    }


    private void loadCategories() {
    categoryIdArrayList=new ArrayList<>();
    categoryTitleArrayList=new ArrayList<>();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Categories");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear();
                categoryIdArrayList.clear();
                for(DataSnapshot sn:snapshot.getChildren()){
                    String id=""+sn.child("id").getValue();
                    String category=""+sn.child("category").getValue();
                    categoryIdArrayList.add(id);
                    categoryTitleArrayList.add(category);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}