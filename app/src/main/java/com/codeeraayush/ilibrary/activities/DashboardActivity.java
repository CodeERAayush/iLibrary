package com.codeeraayush.ilibrary.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.codeeraayush.ilibrary.BooksUserFragment;
import com.codeeraayush.ilibrary.Constants;
import com.codeeraayush.ilibrary.databinding.ActivityDashboardBinding;
import com.codeeraayush.ilibrary.models.ModelCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {
private ActivityDashboardBinding binding;

public ArrayList<ModelCategory>categoryArrayList;

public ViewPagerAdapter viewPagerAdapter;


private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase
        firebaseAuth=FirebaseAuth.getInstance();

        //get user name and email
        checkUser();



        setUpViewPagerAdapter(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);


        //logout btn
        binding.logoutBtnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });
    }



    private void setUpViewPagerAdapter(ViewPager viewPager){
      viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,this);

       categoryArrayList=new ArrayList<>();


        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                categoryArrayList.clear();

                ModelCategory modelCategoryAll=new ModelCategory("01","All","",1);
                ModelCategory modelCategoryMostViewed=new ModelCategory("02","Most Viewed","",1);
                ModelCategory modelCategoryDownloads=new ModelCategory("03","Most Downloaded","",1);


                categoryArrayList.add(modelCategoryAll);
                categoryArrayList.add(modelCategoryMostViewed);
                categoryArrayList.add(modelCategoryDownloads);


                //add data to view pager adapter
                viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
                        ""+modelCategoryAll.getId(),
                        ""+modelCategoryAll.getCategory(),
                        ""+modelCategoryAll.getUid()
                ),modelCategoryAll.getCategory());


                viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
                        ""+modelCategoryMostViewed.getId(),
                        ""+modelCategoryMostViewed.getCategory(),
                        ""+modelCategoryMostViewed.getUid()
                ),modelCategoryMostViewed.getCategory());



                viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
                        ""+modelCategoryDownloads.getId(),
                        ""+modelCategoryDownloads.getCategory(),
                        ""+modelCategoryDownloads.getUid()
                ),modelCategoryDownloads.getCategory());




                //refresh list
                viewPagerAdapter.notifyDataSetChanged();

                //load data from firebase
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelCategory model=ds.getValue(ModelCategory.class);
                    categoryArrayList.add(model);

                    viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
                            ""+model.getId(),
                            ""+model.getCategory(),
                            ""+model.getUid())
                            ,model.getCategory()
                    );


                    //refresh list
                    viewPagerAdapter.notifyDataSetChanged();
                }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewPager.setAdapter(viewPagerAdapter);
    }




    public class  ViewPagerAdapter extends FragmentPagerAdapter{

       private ArrayList<BooksUserFragment> fragmentsList=new ArrayList<>();
        private ArrayList<String> fragmentsTitle=new ArrayList<>();
        private Context context;

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior, Context context) {
            super(fm, behavior);
            this.context=context;
        }



        @NonNull
        @Override
        public Fragment getItem(int position) {

            return fragmentsList.get(position);

        }

        @Override
        public int getCount() {

            return fragmentsList.size();

        }


        private void addFragment(BooksUserFragment fragment,String title){
            fragmentsList.add(fragment);
            fragmentsTitle.add(title);


        }




        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsTitle.get(position);
        }
    }





    private void checkUser() {
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser==null){
            //not logged in goto main screen
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
        }
        else
        {
            //logged in , get user info
            String email=firebaseUser.getEmail();
            binding.emailUser.setText(email);
        }

    }
}