package com.codeeraayush.ilibrary.filters;

import android.widget.Filter;

import com.codeeraayush.ilibrary.adapters.AdapterCategory;
import com.codeeraayush.ilibrary.models.ModelCategory;

import java.util.ArrayList;

public class FilterCategory extends Filter {
    //arrayList in which we want to search
    ArrayList<ModelCategory>filterList;
    //adapter in which filter need to be implimented
    AdapterCategory adapterCategory;

    public FilterCategory(ArrayList<ModelCategory> filterList, AdapterCategory adapterCategory) {
        this.filterList = filterList;
        this.adapterCategory = adapterCategory;
    }


    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results=new FilterResults();
        //value should not be null
        if(charSequence!=null&&charSequence.length()>0){

            //make it upper or lower case
            charSequence=charSequence.toString().toUpperCase();


            ArrayList<ModelCategory>filtered=new ArrayList<>();
            for(int i=0;i<filterList.size();i++){

                //validate
                if(filterList.get(i).getCategory().toUpperCase().contains(charSequence)){
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
        adapterCategory.categoryArrayList=(ArrayList<ModelCategory>) filterResults.values;

        //notify changes
        adapterCategory.notifyDataSetChanged();
    }
}
