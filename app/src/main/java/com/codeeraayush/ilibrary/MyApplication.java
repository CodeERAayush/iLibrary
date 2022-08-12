package com.codeeraayush.ilibrary;

import android.app.Application;
import android.text.format.DateFormat;

import java.util.Calendar;
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
}
