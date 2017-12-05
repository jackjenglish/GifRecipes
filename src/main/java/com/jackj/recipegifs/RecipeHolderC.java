package com.jackj.recipegifs;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
/**
 * Created by jackj on 18/01/2017.
 */

public class RecipeHolderC extends GenericRecipeHolder {
    Recipe mRecipe;
    Context mContext;
    RecipeAdapterC mAdapter;

    public RecipeHolderC(View itemView,Context context,RecipeLab rLab,RecyclerView.Adapter adapter) {
        super(itemView,context,rLab);
        itemView.setOnClickListener(this);
        mContext=context;
        mAdapter=(RecipeAdapterC) adapter;

    }

    @Override
    public void bindRecipe(Recipe recipe) {
        super.bindRecipe(recipe);
        mRecipe=recipe;
    }

    public void bindDrawable(Drawable drawable) {
        recipeImage.setImageDrawable(drawable);
    }
    @Override
    public void onClick(View v) {
        ArrayList<Recipe> pagerRecipes= (ArrayList<Recipe>) mAdapter.getRecipes();
        ArrayList<String> pagerRecipesIds=new ArrayList<>();
        for(Recipe recipe:pagerRecipes){
            pagerRecipesIds.add((recipe.getId()).toString());
        }
        Intent i = RecipeActivity.newIntent(mContext, mRecipe.getId(),pagerRecipesIds);
        mContext.startActivity(i);
    }
}
