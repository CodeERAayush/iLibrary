package com.codeeraayush.ilibrary.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codeeraayush.ilibrary.MyApplication;
import com.codeeraayush.ilibrary.databinding.ActivityPdfDetailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PdfDetailActivity extends AppCompatActivity {
private ActivityPdfDetailBinding binding;
String bookId,bookTitle,bookUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        loadBookDetails();

        binding.readTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent1 = new Intent(PdfDetailActivity.this, pdfViewActivity.class);
//                intent1.putExtra("bookId", bookId);
//                startActivity(intent1);

                Toast.makeText(PdfDetailActivity.this, "Sorry,Read Feature Disabled until next update!", Toast.LENGTH_SHORT).show();

            }
        });

        binding.downloadTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(PdfDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    MyApplication.downloadBook(PdfDetailActivity.this, "" + bookId, "" + bookTitle, "" + bookUrl);
//                    Log.d("DOWNLOADING_BOOK", "onClick: download book");
                } else {
                    reqestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        });

    }
        private ActivityResultLauncher<String> reqestPermissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(),isGranted->{
            if(isGranted){
                MyApplication.downloadBook(this,""+bookId,""+bookTitle,""+bookUrl);
            }
        });




    private void loadBookDetails() {

        DatabaseReference dbr= FirebaseDatabase.getInstance().getReference("Books");
        dbr.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bookTitle=""+snapshot.child("title").getValue();
                        String description=""+snapshot.child("description").getValue();
                        String category=""+snapshot.child("category").getValue();
                        String categoryId=""+snapshot.child("categoryId").getValue();
                        String viewsCount=""+snapshot.child("viewsCount").getValue();
                        String downloadsCount=""+snapshot.child("downloadsCount").getValue();
                        bookUrl=""+snapshot.child("url").getValue();
                        String timestamp=""+snapshot.child("timestamp").getValue();

                        //format date
                        String date= MyApplication.formatTimestamp(Long.parseLong(timestamp));
                        MyApplication.loadCategory(""+categoryId,
                                binding.categoryTv);
//                        MyApplication.loadpdfFromUrl(""+url
//                        ,""+title
//                        ,binding.pdfView
//                       );
                        MyApplication.loadSize(
                                ""+bookUrl,
                                ""+bookTitle
                                ,binding.sizeTv
                        );
MyApplication.getBookViewCount(bookId);

                        //set data
                        binding.titleTv.setText(bookTitle);
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