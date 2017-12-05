package com.jackj.recipegifs;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.List;

public class RecentRecipeFragment extends Fragment{

    private RecyclerView recipeRecyclerView;
    //private RecipeAdapter mRecipeAdapter;
    private RecipeLab mRecipeLab;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDatabase;
    private List<Recipe> cachedRecentRecipes;
    private RecipeAdapterC mAdapter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDbHelper= new DatabaseHelper(getActivity());
        mDatabase= mDbHelper.getWritableDatabase();
        Log.i("created", "recenrec");
        cachedRecentRecipes=new ArrayList<>();
        new FetchItemsTask("test").execute();
/*
       // Handler responseHandler = new Handler();
       // mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
       // mThumbnailDownloader.setThumbnailDownloadListener(
       //         new ThumbnailDownloader.ThumbnailDownloadListener<RecipeHolder>() {
                    @Override
                    public void onThumbnailDownloaded(RecipeHolder recipeHolder, Bitmap bitmap) {
       //                 Drawable drawable = new BitmapDrawable(getResources(), bitmap);
       ///                 recipeHolder.bindDrawable(drawable);
       //             }
                }
       // );
        //mThumbnailDownloader.start();
       // mThumbnailDownloader.getLooper();*/



    }
   // private ThumbnailDownloader<RecipeHolder> mThumbnailDownloader;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_recipe_list,container,false);

        recipeRecyclerView=(RecyclerView) view.findViewById(R.id.fragment_recipe_recycler_view);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recipeRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mRecipeLab= RecipeLab.get(getActivity());

        //mRecipeAdapter= new RecipeAdapter(cachedRecentRecipes);
        refreshSavedIcons(cachedRecentRecipes);
        mAdapter= new RecipeAdapterC(cachedRecentRecipes,getContext());
        recipeRecyclerView.setAdapter(mAdapter);
        return view;
    }
    public void refreshSavedIcons(List<Recipe> cachedRecentRecipes){
        for(Recipe recipe: cachedRecentRecipes){
            recipe.setSaved((getRecipeIfExists(recipe.getTitle())).getSaved());
        }
    }



    private class FetchItemsTask extends AsyncTask<Void,Void,String> {
        List<Recipe> recentRecipes = new ArrayList<>();
        public FetchItemsTask(String query) {
        }
        @Override
        protected String doInBackground(Void... params) {
            try {
                String jsonString = JsonFetcher.getUrlString("http://104.236.58.149/recipes/recentrecipes/");
                JSONObject jsonBody = new JSONObject(jsonString);
                recentRecipes =parseItems(jsonBody);
            } catch (IOException ioe) {
                Log.e("json", "Failed to fetch items", ioe);
            } catch (JSONException je){
                Log.e("json", "sdv jsgdvsvvs v items", je);
            }
            return null;
        }
        @Override
        protected void onPostExecute(String items) {
            //Log.i("Recipes Retrieved: ",""+recentRecipes.size());
            updateDataset(recentRecipes);
        }

    }
    public void updateDataset(List<Recipe> recentRecipes){
        cachedRecentRecipes=recentRecipes;
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recipeRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new RecipeAdapterC(cachedRecentRecipes,getContext());
        recipeRecyclerView.setAdapter(mAdapter);
        //mAdapter.setRecipes(recentRecipes);
        //mAdapter.notifyDataSetChanged();

    }
    private List<Recipe> parseItems(JSONObject jsonBody)
            throws IOException, JSONException {
        List<Recipe> recentRecipes= new ArrayList<>();
        //JSONObject photosJsonObject = jsonBody.getJSONObject("recipe");
        JSONArray recipeJSONArray = jsonBody.getJSONArray("recipe");
       for (int i = 0; i < recipeJSONArray.length(); i++) {
            JSONObject recipeObject = recipeJSONArray.getJSONObject(i);
            Recipe recipe= new Recipe();
            recipe.setTitle(recipeObject.getString("title"));
            recipe.setUrl(recipeObject.getString("link"));
            recipe.setDescription(recipeObject.getString("description"));
            recipe.setPermalink(recipeObject.getString("permalink"));
            recipe.setThumbnail(recipeObject.getString("thumbnail"));
            recipe.setTags("[]");
            recipe.setSaved("0");

           if(getRecipeIfExists(recipe.getTitle())==null){
               //Log.i("check","doesnt exist");
               mDbHelper.addRecipe(recipe,mDatabase);
               recentRecipes.add(recipe);
           } else {
               recentRecipes.add(getRecipeIfExists(recipe.getTitle()));
           }

        }
        return recentRecipes;
    }
    public Recipe getRecipeIfExists(String title) {
        String whereClause=RecipeDbSchema.RecipeTable.Cols.TITLE + " = ?";
        String whereArgs[]=new String[]{title};
        Cursor cursor= mDatabase.query(
                RecipeDbSchema.RecipeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);
        RecipeCursorWrapper cursorWrapper = new RecipeCursorWrapper(cursor);
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursorWrapper.moveToFirst();
            //Log.i("already exists",cursorWrapper.getRecipe().getTitle());
            return cursorWrapper.getRecipe();
        } finally {
            cursorWrapper.close();
        }
    }



}
