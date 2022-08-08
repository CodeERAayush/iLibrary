package com.codeeraayush.ilibrary;

import android.content.Context;
import android.graphics.ColorSpace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.codeeraayush.ilibrary.databinding.RowCategoryBinding;

import java.util.ArrayList;
import java.util.Locale;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.HolderCategory> {
private Context context;
private ArrayList<ModelCategory> categoryArrayList;
//private ViewBinding binding;
//view binding
private RowCategoryBinding binding;

    public AdapterCategory(Context context, ArrayList<ModelCategory> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
    }

    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       binding=RowCategoryBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderCategory(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position) {
        ModelCategory model=categoryArrayList.get(position);
        String uid= model.getUid();
        String id= model.getId();
        String category= model.getCategory();

        //set data
        holder.categoryRe.setText(category);
        holder.delBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, ""+category, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    /* view holder classto hold UI views for recycler_des */
    class HolderCategory extends RecyclerView.ViewHolder{
TextView categoryRe;
ImageButton delBut;

        public HolderCategory(@NonNull View itemView) {

            super(itemView);
            categoryRe=binding.cateRe;
            delBut=binding.delBut;
        }
    }
}
