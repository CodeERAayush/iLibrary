package com.codeeraayush.ilibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.codeeraayush.ilibrary.adapters.AdapterPdfAdmin;
import com.codeeraayush.ilibrary.databinding.ActivityPdfListAdminBinding;
import com.codeeraayush.ilibrary.models.ModelPdf;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PdfListAdmin extends AppCompatActivity {


    private ArrayList<ModelPdf> pdfArr;

    AdapterPdfAdmin adapterPdfAdmin;
    private String categoryId,categoryTitle;
private ActivityPdfListAdminBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPdfListAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent=getIntent();
        categoryId=intent.getStringExtra("categoryId");
        categoryTitle=intent.getStringExtra("categoryTitle");




        loadPdfList();


        binding.searchTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                   adapterPdfAdmin.getFilter().filter(charSequence);
                }catch (Exception e){

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void loadPdfList() {
        pdfArr=new ArrayList<>();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArr.clear();
                        for(DataSnapshot sn:snapshot.getChildren()){
                            ModelPdf model=sn.getValue(ModelPdf.class);
                            //add to list
                            pdfArr.add(model);
                        }
                    adapterPdfAdmin=new AdapterPdfAdmin(PdfListAdmin.this,pdfArr);
                        binding.bookRv.setAdapter(adapterPdfAdmin);
                        binding.bookRv.setLayoutManager(new LinearLayoutManager(PdfListAdmin.this));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}