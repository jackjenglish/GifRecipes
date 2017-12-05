package com.jackj.recipegifs;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class RecipeListFragment extends Fragment{

    private RecyclerView recipeRecyclerView;
    private RecipeAdapterC mAdapter;
    RecipeLab recipeLab;
    List<Recipe> recipes;
    boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    boolean mLoading = false;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        new FetchItemsTask("test").execute();
        Handler responseHandler = new Handler();
        /*mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<RecipeHolder>() {
                    @Override
                    public void onThumbnailDownloaded(RecipeHolder recipeHolder, Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        recipeHolder.bindDrawable(drawable);
                    }
                }
        );
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();*/
        Log.i("create","fraglist");
    }
    //private ThumbnailDownloader<RecipeHolder> mThumbnailDownloader;
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_recipe_list,container,false);
        recipeRecyclerView=(RecyclerView) view.findViewById(R.id.fragment_recipe_recycler_view);
         staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recipeRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        recipeLab= RecipeLab.get(getActivity());
        recipes= recipeLab.getRecipes("20");

        mAdapter = new RecipeAdapterC(recipes,getContext());
        recipeRecyclerView.setAdapter(mAdapter);
        recipeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //Log.i("S","Scroll");
                int totalItem = staggeredGridLayoutManager.getItemCount();

                int[] lastVisibleItem = staggeredGridLayoutManager.findLastVisibleItemPositions(null);
                //Log.i("lastVisibleItem",""+lastVisibleItem[0]);
                //Log.i("lastVisibleItem1",""+lastVisibleItem[1]);
                if (!mLoading) {
                    if(( lastVisibleItem[0] == totalItem - 1) || ( lastVisibleItem[1] == totalItem - 1) ){
                    mLoading = true;
                    // Scrolled to bottom. Do something here.
                        recipes= recipeLab.getRecipes(String.valueOf(mAdapter.getRecipes().size()+35));
                        mAdapter.setRecipes(recipes);
                        mAdapter.notifyDataSetChanged();
                    mLoading = false;}
                }
            }
        });

        return view;
    }


    private class FetchItemsTask extends AsyncTask<Void,Void,String> {
        List<String> featuredRecipes = new ArrayList<>();
        public FetchItemsTask(String query) {
        }
        @Override
        protected String doInBackground(Void... params) {
            try {
                String jsonString = JsonFetcher.getUrlString("http://104.236.58.149/recipes/featured/");
                JSONObject jsonBody = new JSONObject(jsonString);
                featuredRecipes =parseItems(jsonBody);

            } catch (IOException ioe) {
                Log.e("json", "Failed to fetch items", ioe);
            } catch (JSONException je){
                Log.e("json", "sdv jsgdvsvvs v items", je);
            }
            return null;
        }
        @Override
        protected void onPostExecute(String items) {
            updateDataset(featuredRecipes);
        }

    }
    public void updateDataset(List<String> featuredRecipesTitles){
        if(featuredRecipesTitles.size()>0){
        List<Recipe> featuredRecipes= new ArrayList<>();
        for(int i =0;i<featuredRecipesTitles.size();i++){
            try{
                Recipe r= recipeLab.getRecipeFromName(featuredRecipesTitles.get(i));
                if(r!=null){
                    featuredRecipes.add(r);}
            }catch (Exception e){

            }
        }

        long seed = System.nanoTime();
        Collections.shuffle(recipes, new Random(seed));
        Collections.shuffle(recipes, new Random(seed));
        for(int i =0;i<featuredRecipes.size();i++){
            //Log.i("update DATA",""+featuredRecipes.get(i));
            recipes.add(i,featuredRecipes.get(i));
        }
        mAdapter.setRecipes(recipes);
        mAdapter.notifyDataSetChanged();}

    }
    private List<String> parseItems(JSONObject jsonBody)
            throws IOException, JSONException {
        List<String> featuredRecipes= new ArrayList<>();
        JSONArray recipeJSONArray = jsonBody.getJSONArray("recipes");
        JSONObject featuredObject = recipeJSONArray.getJSONObject(0);
        for (int i = 0; i <featuredObject.names().length(); i++) {
            if(!featuredObject.getString(String.valueOf(0)).equals("null")){
            if(featuredObject.has((String.valueOf(i)))) {
                featuredRecipes.add((featuredObject.getString(String.valueOf(i))));
            }}
        }
        return featuredRecipes;
    }
}
