package com.codeeraayush.ilibrary.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codeeraayush.ilibrary.Constants;
import com.codeeraayush.ilibrary.MyApplication;
import com.codeeraayush.ilibrary.activities.PdfDetailActivity;
import com.codeeraayush.ilibrary.databinding.PdfRowUserBinding;
import com.codeeraayush.ilibrary.filters.FilterPdfUser;
import com.codeeraayush.ilibrary.models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPdfUser extends RecyclerView.Adapter<AdapterPdfUser.HolderPdfUser> implements Filterable {
private Context context;
public ArrayList<ModelPdf>pdfArrayList,filterList;
private FilterPdfUser filterPdfUser;
private PdfRowUserBinding binding;


    public AdapterPdfUser(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList=pdfArrayList;
        this.filterList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding=PdfRowUserBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderPdfUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfUser holder, int position) {



        ModelPdf model=pdfArrayList.get(position);
        String bookId=model.getId();
        String title=model.getTitle();
        String description=model.getDescription();
        String pdfUrl=model.getUrl();
        String categoryId= model.getCategoryId();
        long timeStamp= model.getTimeStamp();



        String date = MyApplication.formatTimestamp(timeStamp);

        //set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(date);


        MyApplication.loadpdfFromUrl(""+pdfUrl,""+title, holder.pdfView);
        MyApplication.loadCategory(""+categoryId, holder.categoryTv);
        MyApplication.loadSize(""+pdfUrl,""+title,holder.sizeTv);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",bookId);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {

        if(filterPdfUser==null){
            filterPdfUser=new FilterPdfUser(filterList,this);

        }
        return filterPdfUser ;
    }

    public class HolderPdfUser extends RecyclerView.ViewHolder{
            TextView titleTv,descriptionTv,sizeTv,categoryTv,dateTv;
            PDFView pdfView;
        public HolderPdfUser(@NonNull View itemView) {
            super(itemView);
            titleTv=binding.titleTv;
            descriptionTv=binding.descriptionTv;
            sizeTv=binding.sizeTv;
            categoryTv=binding.categoryTv;
            dateTv=binding.dateTv;
            pdfView=binding.pdfView;
        }
    }

}
