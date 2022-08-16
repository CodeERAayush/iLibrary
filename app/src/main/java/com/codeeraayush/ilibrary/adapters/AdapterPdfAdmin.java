package com.codeeraayush.ilibrary.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codeeraayush.ilibrary.MyApplication;
import com.codeeraayush.ilibrary.activities.PdfDetailActivity;
import com.codeeraayush.ilibrary.activities.PdfEditActivity;
import com.codeeraayush.ilibrary.databinding.PdfRowAdminBinding;
import com.codeeraayush.ilibrary.filters.FilterPdfAdmin;
import com.codeeraayush.ilibrary.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> implements Filterable {

    private FilterPdfAdmin filter;

    //context
    private Context context;

    //Arraylist to store list of data of type ModelPdf
    public ArrayList<ModelPdf>pdfArrayList,filterList;


    private ProgressDialog progressDialog;


    //constructor
    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList=pdfArrayList;


        //init progressDialogue
        progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);


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
        String categoryId= model.getCategoryId();
        long timestamp=model.getTimeStamp();
        String pdfUrl=model.getUrl();
        String pdfId= model.getId();
        //we need to convert timestamp to dd/mm/yy format
        String formattedDate= MyApplication.formatTimestamp(timestamp);

        //set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formattedDate);

        //load further details like category , pdf from url,pdf size in seperate function
        MyApplication.loadCategory(""+categoryId, holder.categoryTv);

        MyApplication.loadpdfFromUrl(""+pdfUrl,""+title,holder.pdfView);
        MyApplication.loadSize(""+pdfUrl
        ,""+title
        ,holder.sizeTv);


        //handel click , show dialogue with options a> Edit and b>delete
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreOptionsDialogue(model,holder);
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",pdfId);
                context.startActivity(intent);
            }
        });

    }

    private void moreOptionsDialogue(ModelPdf model, HolderPdfAdmin holder) {
        String bookId= model.getId();
        String bookUrl= model.getUrl();
        String bookTitle=model.getTitle();
        //options to show in dialogue
        String []options={"Edit","Delete"};

//        alert Dialogue
        AlertDialog.Builder builder =new AlertDialog.Builder(context);
        builder.setTitle("Choose Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       //handle dialogue option click
                       if(i==0){
                           //Edit clicked
                           //have to make new activity for editing in the book
                           Intent intent=new Intent(context, PdfEditActivity.class)
                                   .putExtra("bookId",model.getId());
                                    context.startActivity(intent);

                       }else if(i==1){
//                           del clicked
//                       deleteBook(model , holder);
                           MyApplication.deleteBook(context,""+bookId,""+bookUrl,""+bookTitle);
                       }

                    }
                })
                .show();

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
//            progressBar=binding.progressBar;
            titleTv=binding.titleTv;
            descriptionTv=binding.descriptionTv;
            categoryTv=binding.categoryTv;
            sizeTv=binding.sizeTv;
            dateTv=binding.dateTv;
            moreBtn=binding.moreBtn;
        }
    }
}
