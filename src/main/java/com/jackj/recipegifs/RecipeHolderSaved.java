package com.jackj.recipegifs;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by jackj on 18/01/2017.
 */

public class RecipeHolderSaved extends GenericRecipeHolder {
    Context mContext;
    RecipeLab mRecipeLab;
    List<Recipe> pagerRecipes;
    RecipeAdapterSaved mAdapter;

    public RecipeHolderSaved(View itemView,Context context,RecipeLab rLab,RecyclerView.Adapter adapter) {
        super(itemView,context,rLab);
        itemView.setOnClickListener(this);
        mContext=context;
        mRecipeLab=rLab;
        mAdapter=(RecipeAdapterSaved) adapter;
        pagerRecipes=mAdapter.getRecipes();
    }

    @Override
    public void bindRecipe(Recipe recipe) {
        //super.bindRecipe(recipe);
        mRecipe= recipe;
        if(getPosition()==0){
            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(recipeImage);
            Glide.with(mContext).load("http://i.imgur.com/Lox9ZPR.png").into(imageViewTarget);
            mTitleTextView.setText("Random GifRecipe");
        } else{
            loadThumbnail(mRecipe.getThumbnail());
            faveButtonSetup();
            tagsSetup();
            setTitleText(mRecipe.getTitle());
        }
    }

    @Override
    public void onClick(View v) {
        randomizeZero();
        ArrayList<Recipe> pagerRecipes= (ArrayList<Recipe>) mAdapter.getRecipes();
        ArrayList<String> pagerRecipesIds=new ArrayList<>();
        for(Recipe recipe:pagerRecipes){
            pagerRecipesIds.add((recipe.getId()).toString());
        }
        Intent i = RecipeActivity.newIntent(mContext, mRecipe.getId(),pagerRecipesIds);
        mContext.startActivity(i);
    }
    @Override
    public void faveButtonSetup() {
        final Drawable unsaved= mContext.getResources().getDrawable(R.drawable.gray_heart);
        final Drawable saved= mContext.getResources().getDrawable(R.drawable.gray_heart_filled);
        faveIcon.setImageDrawable(saved);
        faveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecipeLab.changeSavedStatus("0", mRecipe.getId());
                faveIcon.setImageDrawable(unsaved);
                Log.i("faveIcon Position",""+getPosition());
                pagerRecipes.remove(getPosition());
                mAdapter.notifyItemRemoved(getPosition());
            }
        });
    }

    public void randomizeZero(){
        List<Recipe> rList=(RecipeLab.get(mContext).getRecipes(null));
        int randomRec= ThreadLocalRandom.current().nextInt(0, rList.size());
        UUID randomId=(RecipeLab.get(mContext).getRecipes(null)).get(randomRec).getId();
        Recipe recipe=RecipeLab.get(mContext).getRecipe(randomId);
        mAdapter.getRecipes().get(0).setId(randomId);
    }
}
