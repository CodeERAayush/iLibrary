package com.codeeraayush.ilibrary.filters;

import android.widget.Filter;

import com.codeeraayush.ilibrary.adapters.AdapterCategory;
import com.codeeraayush.ilibrary.adapters.AdapterPdfAdmin;
import com.codeeraayush.ilibrary.models.ModelCategory;
import com.codeeraayush.ilibrary.models.ModelPdf;

import java.util.ArrayList;

public class FilterPdfAdmin extends Filter {
    //arrayList in which we want to search
    ArrayList<ModelPdf>filterList;
    //adapter in which filter need to be implimented
    AdapterPdfAdmin adapterPdfAdmin;

    public FilterPdfAdmin(ArrayList<ModelPdf> filterList, AdapterPdfAdmin adapterPdfAdmin) {
        this.filterList = filterList;
      this.adapterPdfAdmin=adapterPdfAdmin;
    }


    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results=new FilterResults();
        //value should not be null
        if(charSequence!=null&&charSequence.length()>0){

            //make it upper or lower case
            charSequence=charSequence.toString().toUpperCase();


            ArrayList<ModelPdf> filtered=new ArrayList<>();
            for(int i=0;i<filterList.size();i++){

                //validate
                if(filterList.get(i).getTitle().toUpperCase().contains(charSequence)){
                    filtered.add(filterList.get(i));
                }
            }
            results.count=filtered.size();
            results.values=filtered;
        }
        else{
            results.count=filterList.size();
            results.values=filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//apply changes
        adapterPdfAdmin.pdfArrayList=(ArrayList<ModelPdf>) filterResults.values;

        //notify changes
        adapterPdfAdmin.notifyDataSetChanged();
    }
}
