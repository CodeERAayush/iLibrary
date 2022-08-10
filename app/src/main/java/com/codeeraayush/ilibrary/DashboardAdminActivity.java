package com.codeeraayush.ilibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.codeeraayush.ilibrary.databinding.ActivityDashboardAdminBinding;
import com.codeeraayush.ilibrary.databinding.ActivityDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardAdminActivity extends AppCompatActivity {
private ActivityDashboardAdminBinding binding;

private FirebaseAuth firebaseAuth;

//ArrayList to store categories
   private ArrayList<ModelCategory>categoryArrayList;

    //adapter
   private AdapterCategory adapterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDashboardAdminBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        //init firebase
        firebaseAuth=FirebaseAuth.getInstance();

        //get user name and email
        checkUser();

        loadCategories();

        //realtime searching
        binding.searchBtn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//calls when user changes the letters
                try {
                    adapterCategory.getFilter().filter(charSequence);
                }catch (Exception e){
                    Toast.makeText(DashboardAdminActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //logout btn
        binding.logoutBtnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });



        binding.addCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardAdminActivity.this,AddCategoryBooks.class));
                finish();
            }
        });

    }

    private void loadCategories() {
        //initialize arrayList
        categoryArrayList=new ArrayList<>();

        //get all categories from firebse > categories
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
    ref.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            categoryArrayList.clear();
            for(DataSnapshot ds:snapshot.getChildren()){
                //get data
                ModelCategory model=ds.getValue(ModelCategory.class);
                //add to arraylist
                categoryArrayList.add(model);
            }
            //setup adapter
            adapterCategory=new AdapterCategory(DashboardAdminActivity.this,categoryArrayList);
            //setup adapter
            binding.reCat.setAdapter(adapterCategory);
            binding.reCat.setLayoutManager(new LinearLayoutManager(DashboardAdminActivity.this));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            //not logged in goto main screen
            startActivity(new Intent(DashboardAdminActivity.this, MainActivity.class));
            finish();
        } else {
            //logged in , get user info
            String email = firebaseUser.getEmail();
            binding.emailAdmin.setText(email);
        }
    }
}