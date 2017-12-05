package com.jackj.recipegifs;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by jackj on 21/11/2016.
 */
public class RecipeListActivity extends AppCompatActivity {
    ViewPager viewpager;
    TabLayout tabLayout;
    Drawable home;
    Drawable newRecipe;
    Drawable search;
    Drawable saved;
    FragmentPagerAdapter pagerAdapter;
    View homeTab;
    View searchTab;
    View faveTab;
    View newTab;
    TextView appTitle;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isNetworkConnected()){
            Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);
            getSupportActionBar().setTitle("All");

            MobileAds.initialize(getApplicationContext(),"ca-app-pub-6778210430507167~5732311333");
            AdView adView = new AdView(this);
            adView.setAdSize(AdSize.SMART_BANNER);
            adView.setAdUnitId(getString(R.string.banner_ad_unit_id));
            LinearLayout rootll= (LinearLayout)findViewById(R.id.rootll);
            rootll.addView(adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);

            tabLayout= (TabLayout)findViewById(R.id.tablayout);
            appTitle= (TextView) findViewById(R.id.apptitle);
            appTitle.setText("GifRecipes");
            Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/haikubold.ttf");
            appTitle.setTypeface(custom_font);

            tabLayout.addTab(tabLayout.newTab());
            tabLayout.addTab(tabLayout.newTab());
            tabLayout.addTab(tabLayout.newTab());
            tabLayout.addTab(tabLayout.newTab());
            viewpager = (ViewPager)findViewById(R.id.viewpager);
            FragmentManager fm=getSupportFragmentManager();
            pagerAdapter=  new FragmentPagerAdapter(fm) {
                public Fragment getItem(int position) {
                    switch(position){
                        case 0:return new RecipeListFragment();
                        case 1:return new RecentRecipeFragment();
                        case 2:return new RecipeSearchFragment();
                        case 3:return new RecipeSavedFragment();
                    }
                    return new RecipeListFragment();
                }
                public int getCount() {
                    return 4;
                }
                @Override
                public int getItemPosition(Object object) {
                    return PagerAdapter.POSITION_NONE;
                }
            };
            viewpager.setAdapter(pagerAdapter);

            home= this.getResources().getDrawable(R.drawable.ic_list_white_36dp);
            newRecipe= this.getResources().getDrawable(R.drawable.ic_star_white_36dp);
            search= this.getResources().getDrawable(R.drawable.ic_search_white_36dp);
            saved= this.getResources().getDrawable(R.drawable.whiteheartimg);
            home.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.iconColor),PorterDuff.Mode.MULTIPLY));
            newRecipe.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.iconColor),PorterDuff.Mode.MULTIPLY));
            search.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.iconColor),PorterDuff.Mode.MULTIPLY));
            saved.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.iconColor),PorterDuff.Mode.MULTIPLY));
            tabLayout.setupWithViewPager(viewpager);


            homeTab= getLayoutInflater().inflate(R.layout.custom_tab,null);
            ImageView homeTabicon=(ImageView) homeTab.findViewById(R.id.icon);
            homeTabicon.setImageDrawable(home);

            newTab= getLayoutInflater().inflate(R.layout.custom_tab_new,null);
            ImageView newTabicon=(ImageView) newTab.findViewById(R.id.icon);
            newTabicon.setImageDrawable(newRecipe);

            searchTab= getLayoutInflater().inflate(R.layout.custom_tab_search,null);
            ImageView searchTabicon=(ImageView) searchTab.findViewById(R.id.icon);
            searchTabicon.setImageDrawable(search);

            faveTab= getLayoutInflater().inflate(R.layout.custom_tab_favorite,null);
            ImageView faveTabicon=(ImageView) faveTab.findViewById(R.id.icon);
            faveTabicon.setImageDrawable(saved);
            tabLayout.getTabAt(0).setCustomView(homeTab);
            tabLayout.getTabAt(1).setCustomView(newTab);
            tabLayout.getTabAt(2).setCustomView(searchTab);
            tabLayout.getTabAt(3).setCustomView(faveTab);
        } else {
            setContentView(R.layout.no_connection);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        if (tabLayout.getSelectedTabPosition()==2  ) {
            pagerAdapter.notifyDataSetChanged();
            tabLayout.getTabAt(0).setCustomView(homeTab);
            tabLayout.getTabAt(1).setCustomView(newTab);
            tabLayout.getTabAt(2).setCustomView(searchTab);
            tabLayout.getTabAt(3).setCustomView(faveTab);;}
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 1500);
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
