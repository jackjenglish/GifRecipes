package com.jackj.recipegifs;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by jackj on 18/01/2017.
 */

public class RecipeAdapterSaved extends RecyclerView.Adapter<RecipeHolderSaved> {
    private Context mContext;
    private RecipeLab mRecipeLab;
    private List<Recipe> mRecipes;
    RecyclerView mRecyclerView;
    public RecipeAdapterSaved(List<Recipe> recipes, Context context){
        mContext=context;
        mRecipeLab=RecipeLab.get(mContext);
        mRecipes= recipes;

        Log.i("Recipes","retrieved");
    }
    @Override
    public RecipeHolderSaved onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(mContext);
        View view= layoutInflater.inflate(R.layout.grid_item,parent,false);
        mRecyclerView=(RecyclerView) parent;
        return new RecipeHolderSaved(view,mContext,mRecipeLab,this);
    }
    @Override
    public void onBindViewHolder(RecipeHolderSaved holder, int position) {
        Recipe recipe= mRecipes.get(position);
        holder.bindRecipe(recipe);
        // }
    }
    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    public List<Recipe> getRecipes() {
        return mRecipes;
    }
    public void setRecipes(List<Recipe> recipes){
        Log.i("dapterr","RECIPES SET");
        Log.i("dapterr",recipes.get(0).getTitle());
        mRecipes=recipes;
    }
    @Override
    public int getItemViewType(int position) {
        Log.i("getitemview","");
        if(position==0){
            return 1;
        }
        return 0;
    }
}
