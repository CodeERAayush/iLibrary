package com.codeeraayush.ilibrary.filters;

import android.widget.Filter;

import com.codeeraayush.ilibrary.adapters.AdapterPdfUser;
import com.codeeraayush.ilibrary.models.ModelPdf;

import java.util.ArrayList;
import java.util.Locale;

public class FilterPdfUser extends Filter {

    ArrayList<ModelPdf>filterList;
    AdapterPdfUser adapterPdfUser;

    public FilterPdfUser(ArrayList<ModelPdf> filterList, AdapterPdfUser adapterPdfUser) {
        this.filterList = filterList;
        this.adapterPdfUser = adapterPdfUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {

        FilterResults results=new FilterResults();
//        return null;
    if(charSequence!=null||charSequence.length()>0){

        charSequence=charSequence.toString().toUpperCase();
        ArrayList<ModelPdf>filteredModels=new ArrayList<>();

        for (int i=0;i<filterList.size();i++){
            if(filterList.get(i).getTitle().toUpperCase().contains(charSequence)){

                filteredModels.add(filterList.get(i));
            }
        }

        results.values=filteredModels;
        results.count=filteredModels.size();




    }
    else{
        results.count=filterList.size();
        results.values=filterList;
    }

        return results;
    }


    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapterPdfUser.pdfArrayList=(ArrayList<ModelPdf>) filterResults.values;
        adapterPdfUser.notifyDataSetChanged();
    }
}
