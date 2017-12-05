package com.jackj.recipegifs;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by jackj on 25/11/2016.
 */
public class RecipeActivity extends AppCompatActivity {
    ViewPager mViewPager;
    private List<Recipe> mRecipes;
    private List<Recipe> pagerRecipes;
    private static final String RECIPE_EXTRA_KEY ="recipeKey";
    private static final String RECIPELIST_EXTRA_KEY ="recipeList";
    InterstitialAd mInterstitialAd;


     public static Intent newIntent(Context packageContext, UUID recipeId,ArrayList<String> pagerRecipesPositions) {
        // Log.i("newintent","hello");
        Intent intent = new Intent(packageContext, RecipeActivity.class);
        intent.putExtra(RECIPE_EXTRA_KEY, recipeId);


         //RecipeWrapper wrapper= new RecipeWrapper(pagerRecipes);
         intent.putStringArrayListExtra(RECIPELIST_EXTRA_KEY,pagerRecipesPositions);
         //Log.i("newintent","hello2");
        return intent;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_pager);
        //Log.i("Created","RecipeActivity");
        //Log.i("id",""+getIntent().getSerializableExtra(RECIPE_EXTRA_KEY));

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6778210430507167/1653535335");
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.i("scrollAd","CLOSED");
                requestNewInterstitial();
            }
        });
        requestNewInterstitial();

        UUID recipeId= (UUID) getIntent().getSerializableExtra(RECIPE_EXTRA_KEY);
        ArrayList<String> intentRecipeIds= getIntent().getStringArrayListExtra(RECIPELIST_EXTRA_KEY);

        pagerRecipes=new ArrayList<>();


        //Log.i("id","inside not null");

        if(intentRecipeIds.get(0).equals("allr")){
            mRecipes=RecipeLab.get(this).getRecipes(null);
            pagerRecipes=mRecipes;
        } else{
            for(String id:intentRecipeIds){
                pagerRecipes.add(RecipeLab.get(this).getRecipe(UUID.fromString(id)));
            }
        }

       // if(intentRecipes.size()>0){
         //  mRecipes=intentRecipes;
        //} else{
        //Log.i("list SIze",""+pagerRecipes.size());


        //}


        mViewPager= (ViewPager) findViewById(R.id.activity_recipe_pager_view_pager);
        mViewPager.setOffscreenPageLimit(0);
        FragmentManager fm=getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            public Fragment getItem(int position) {
                //Log.i("setting","recipes");
                Recipe recipe=pagerRecipes.get(position);
                return RecipeFragment.newInstance(recipe.getId());
            }
            public int getCount() {
                return pagerRecipes.size();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i("scrollAd","pos :"+position);
                if(position%8==0 && position>7){
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        for(int i=0;i<pagerRecipes.size();i++){
            if(pagerRecipes.get(i).getId().equals(recipeId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
    private void requestNewInterstitial() {
        Log.i("scrollAd","requestNewInterstitial");
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("F94CCC7F6D12F500124003976C075B95")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
}
