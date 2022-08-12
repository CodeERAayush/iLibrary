package com.codeeraayush.ilibrary.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codeeraayush.ilibrary.PdfListAdmin;
import com.codeeraayush.ilibrary.filters.FilterCategory;
import com.codeeraayush.ilibrary.models.ModelCategory;
import com.codeeraayush.ilibrary.databinding.RowCategoryBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.HolderCategory> implements Filterable {
private Context context;
public ArrayList<ModelCategory> categoryArrayList,filterList;
//private ViewBinding binding;
//view binding
private RowCategoryBinding binding;


//instance of filter
    private FilterCategory filter;

    public AdapterCategory(Context context, ArrayList<ModelCategory> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
        this.filterList=categoryArrayList;
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
long timestamp=model.getTimestamp();
        //set data
        holder.categoryTv.setText(category);





        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, ""+category, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete this category ?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(context, "Deleting Category...", Toast.LENGTH_LONG).show();
                                deleteCategory(model , holder);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }

        });



        //handle item click to go to pdfListAdmin activity , also pass pdf category and category id
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, PdfListAdmin.class);
                intent.putExtra("categoryId",id)
                        .putExtra("category",category);
                context.startActivity(intent);
            }
        });


    }



    private void deleteCategory(ModelCategory model, HolderCategory holder) {
        String id=model.getId();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Categories");
                ref.child(id)
                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //deleted successfully
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

    }


    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new FilterCategory(filterList,this);
        }
        return filter;
    }

    /* view holder classto hold UI views for recycler_des */
    class HolderCategory extends RecyclerView.ViewHolder{
TextView categoryTv;
ImageButton deleteBtn;

        public HolderCategory(@NonNull View itemView) {

            super(itemView);
            categoryTv=binding.categoryTv;
            deleteBtn=binding.deleteBtn;
        }
    }
}
