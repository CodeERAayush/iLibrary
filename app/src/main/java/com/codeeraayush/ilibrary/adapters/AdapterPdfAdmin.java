package com.codeeraayush.ilibrary.adapters;

import static android.content.ContentValues.TAG;
import static com.codeeraayush.ilibrary.Constants.MAX_SIZE_PDF;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codeeraayush.ilibrary.MyApplication;
import com.codeeraayush.ilibrary.databinding.PdfRowAdminBinding;
import com.codeeraayush.ilibrary.filters.FilterPdfAdmin;
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

import java.util.ArrayList;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> implements Filterable {

    private FilterPdfAdmin filter;

    //context
    private Context context;

    //Arraylist to store list of data of type ModelPdf
    public ArrayList<ModelPdf>pdfArrayList,filterList;


    //constructor
    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList=pdfArrayList;
    }

    //viewbinding for pdf_row_admin
    private PdfRowAdminBinding binding;

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //bind layout using view Binding
        binding=PdfRowAdminBinding.inflate(LayoutInflater.from(context),parent,false);


        return new HolderPdfAdmin(binding.getRoot());

    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfAdmin holder, int position) {
        // get data, set data, handle clicks, etc

        //get data
        ModelPdf model=pdfArrayList.get(position);
        String title=model.getTitle();
        String description=model.getDescription();
        long timestamp=model.getTimeStamp();

        //we need to convert timestamp to dd/mm/yy format
        String formattedDate= MyApplication.formatTimestamp(timestamp);

        //set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formattedDate);

        //load further details like category , pdf from url,pdf size in seperate function
        loadCategory(model,holder);
        loadpdfFromUrl(model,holder);
        loadSize(model,holder);
        


    }

    private void loadpdfFromUrl(ModelPdf model, HolderPdfAdmin holder) {
        String pdfUrl=model.getUrl();
        StorageReference reference=FirebaseStorage.getInstance().getReference(pdfUrl);
        reference.getBytes(MAX_SIZE_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                    holder.pdfView.fromBytes(bytes)
                            .pages(0)//show only the first page of the pdf
                             .spacing(0)
                            .swipeHorizontal(false)
                            .enableSwipe(false)
                            .onError(new OnErrorListener() {
                                @Override
                                public void onError(Throwable t) {
                                    Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .onPageError(new OnPageErrorListener() {
                                @Override
                                public void onPageError(int page, Throwable t) {
                                    Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void loadSize(ModelPdf model, HolderPdfAdmin holder) {

        String pdfUrl=model.getUrl();
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
                            holder.sizeTv.setText(String.format("%.2f",MB)+" Mb");
                        }else if(KB>=1){
                            holder.sizeTv.setText(String.format("%.2f",KB)+" Kb");
                        }
                        else{
                            holder.sizeTv.setText(String.format("%.2f",bytes)+" bytes");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });



    }

    private void loadCategory(ModelPdf model, HolderPdfAdmin holder) {

        //load category using category id
        String categoryId=model.getCategoryId();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String category=""+snapshot.child("category").getValue();
                       holder.categoryTv.setText(category);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {

            filter=new FilterPdfAdmin(filterList,this);
        }
        return filter;
    }


    // view holder class for pdf_row_admin.xml
    class HolderPdfAdmin extends RecyclerView.ViewHolder{

        //UI view of pdf_row_admin
        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTv,descriptionTv,categoryTv,sizeTv,dateTv;
        ImageButton moreBtn;


        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);


            //init variables
            pdfView=binding.pdfView;
            progressBar=binding.progressBar;
            titleTv=binding.titleTv;
            descriptionTv=binding.descriptionTv;
            categoryTv=binding.categoryTv;
            sizeTv=binding.sizeTv;
            dateTv=binding.dateTv;
            moreBtn=binding.moreBtn;
        }
    }
}
