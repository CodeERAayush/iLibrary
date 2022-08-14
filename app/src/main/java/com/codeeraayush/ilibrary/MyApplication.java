package com.codeeraayush.ilibrary;

import static android.content.ContentValues.TAG;
import static com.codeeraayush.ilibrary.Constants.MAX_SIZE_PDF;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;

import com.codeeraayush.ilibrary.adapters.AdapterPdfAdmin;
import com.codeeraayush.ilibrary.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
//application class runs before our launcher activity
public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
    }

    //created a static method to convert timestamp to dd/mm/yyyy format , so that it can be used anywhere in the project


    public static final String formatTimestamp(long timestamp){
        Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);
        // format timestamp in form of dd/mm/yy
        String date = DateFormat.format("dd/MM/yyyy",calendar).toString();
        return date;
    }





    //delete book
    public static void deleteBook(Context context,String bookId,String bookUrl,String bookTitle) {
        ProgressDialog progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Deleting "+bookTitle);
        progressDialog.show();

        //delete from storage
        StorageReference ref= FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
        ref.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();


                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Books");
                        databaseReference.child(bookId)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Book deleted Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }



    public static void loadSize(String pdfUrl, String pdfTitle ,TextView sizeTv) {
        String TAG="LOAD_PDF_SIZE";
        //using url we can access pdf and its size from firebase Storage
        StorageReference ref= FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        double bytes=storageMetadata.getSizeBytes();

                        //convert bytes to KB , MB
                        double KB=bytes/1024;
                        double MB=KB/1024;
                        if(MB>=1){
                            sizeTv.setText(String.format("%.2f",MB)+" Mb");
                        }else if(KB>=1){
                            sizeTv.setText(String.format("%.2f",KB)+" Kb");
                        }
                        else{
                            sizeTv.setText(String.format("%.2f",bytes)+" bytes");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e.getMessage());

                    }
                });



    }



    public static void loadpdfFromUrl(String pdfUrl, String pdfTitle,PDFView pdfView) {
        String TAG="LOAD_PDF_URL";
        StorageReference reference=FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        reference.getBytes(MAX_SIZE_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        pdfView.fromBytes(bytes)
                                .pages(0)//show only the first page of the pdf
                                .spacing(0)
                                .swipeHorizontal(false)
                                .enableSwipe(false)
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
//                                        Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onError: "+t.getMessage());
                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
//                                        Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onPageError: "+t.getMessage());
                                    }
                                })
                                .load();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: "+e.getMessage());
                    }
                });

    }



    public static void loadCategory(String categoryId, TextView categoryTv) {

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String category=""+snapshot.child("category").getValue();
                        categoryTv.setText(category);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }


    public static void getBookViewCount(String bookId){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String viewsCount=""+snapshot.child("viewsCount").getValue();

                        if(viewsCount.equals("")||viewsCount.equals("null")){
                            viewsCount="0";

                        }

                        long newViewsCount=Long.parseLong(viewsCount)+1;

                        HashMap<String , Object>hashMap=new HashMap<>();
                        hashMap.put("viewsCount",newViewsCount);
                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Books");
                        reference.child(bookId)
                                .updateChildren(hashMap);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



}
