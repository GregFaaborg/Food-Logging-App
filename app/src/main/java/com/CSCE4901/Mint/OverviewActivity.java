package com.CSCE4901.Mint;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.CSCE4901.Mint.Entry.EntryFragment;
import com.CSCE4901.Mint.Home.HomeFragment;
import com.CSCE4901.Mint.Report.ReportFragment;
import com.CSCE4901.Mint.Search.SearchFragment;
import com.CSCE4901.Mint.User.UserFragment;

import java.util.Objects;

public class OverviewActivity extends AppCompatActivity {



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @SuppressLint("RestrictedApi")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:

                    selectedFragment = new HomeFragment();
                    changeActionBarText("Home");
                    Objects.requireNonNull(getSupportActionBar()).show();
                    break;
                case R.id.navigation_report:
                    selectedFragment = new ReportFragment();
                    changeActionBarText("Report");
                    Objects.requireNonNull(getSupportActionBar()).show();
                    break;

                case R.id.navigation_entry:
                    selectedFragment = new EntryFragment();
                    changeActionBarText("Create Entry");
                    Objects.requireNonNull(getSupportActionBar()).show();
                    break;

                case R.id.navigation_search:
                    selectedFragment = new SearchFragment();
                    changeActionBarText("Search");
                    Objects.requireNonNull(getSupportActionBar()).setShowHideAnimationEnabled(false);
                    getSupportActionBar().hide();
                    break;

                case R.id.navigation_user:
                    selectedFragment = new UserFragment();
                    changeActionBarText("User Info");
                    Objects.requireNonNull(getSupportActionBar()).show();
                    break;

            }
            assert selectedFragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();

            return true;
        }
    };

    public void changeActionBarText(String text){
        Objects.requireNonNull(getSupportActionBar()).setTitle(text);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        //doing this prevents the fragment from switching to home when device is rotated
        if (savedInstanceState == null)
        {
            //select Home fragment to be displayed on app startup
            Fragment launchFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    launchFragment).commit();
            changeActionBarText("Home");
            navigation.setSelectedItemId(R.id.navigation_home);
        }






    }

}
