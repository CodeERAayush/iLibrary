package com.codeeraayush.ilibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.codeeraayush.ilibrary.databinding.ActivityPdfDetailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PdfDetailActivity extends AppCompatActivity {
private ActivityPdfDetailBinding binding;
String bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPdfDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent=getIntent();
        bookId=intent.getStringExtra("bookId");
        loadBookDetails();

        binding.readTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(PdfDetailActivity.this,pdfViewActivity.class);
                intent1.putExtra("bookId",bookId);
                startActivity(intent1);
            }
        });

    }

    private void loadBookDetails() {

        DatabaseReference dbr= FirebaseDatabase.getInstance().getReference("Books");
        dbr.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String title=""+snapshot.child("title").getValue();
                        String description=""+snapshot.child("description").getValue();
                        String category=""+snapshot.child("category").getValue();
                        String categoryId=""+snapshot.child("categoryId").getValue();
                        String viewsCount=""+snapshot.child("viewsCount").getValue();
                        String downloadsCount=""+snapshot.child("downloadsCount").getValue();
                        String url=""+snapshot.child("url").getValue();
                        String timestamp=""+snapshot.child("timestamp").getValue();

                        //format date
                        String date=MyApplication.formatTimestamp(Long.parseLong(timestamp));
                        MyApplication.loadCategory(""+categoryId,
                                binding.categoryTv);
                        MyApplication.loadpdfFromUrl(""+url
                        ,""+title
                        ,binding.pdfView
                       );
                        MyApplication.loadSize(
                                ""+url,
                                ""+title
                                ,binding.sizeTv
                        );
MyApplication.getBookViewCount(bookId);

                        //set data
                        binding.titleTv.setText(title);
                        binding.descriptionTv.setText(description);
                        binding.viewsTv.setText(viewsCount.replace("null","N/A"));
                        binding.dateTv.setText(date);
                        binding.categoryTv.setText(category);
                        binding.downloadsTv.setText(downloadsCount.replace("null","N/A"));


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}