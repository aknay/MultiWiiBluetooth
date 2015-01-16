package com.example.nau.myswiptabapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MyMainSwipTabActivity extends FragmentActivity {
    //    private DataBaseHelper DbHelper;
    ViewPager viewPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_main_swip_tab);
        //this pager
        viewPager = (ViewPager) findViewById(R.id.pager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new MyAdapter(fragmentManager));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_main_swip_tab, menu);
        return true;
    }

}

class MyAdapter extends FragmentPagerAdapter {

    public MyAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        //return fragment
        Fragment fragment = null;
      //  if (i == 0) {
       //     fragment = new FragmentA();
     //   }
     //   if (i == 1) {
            fragment = new FragmentB();
     //   }
       // if (i == 2) {
       //     fragment = new FragmentC();
     //   }
        return fragment;
    }

    @Override
    public int getCount() {
        //if you want to add more tab, you can put more 'return 2;'
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
     //   String tilte = new String();
//        if (position == 0) {
//            return "Tab 1";
//        }
        if (position == 1) {
            return "Tab 2";
        }
//        if (position == 2) {
//            return "Tab 3";
//        }

        return null;
    }
}

