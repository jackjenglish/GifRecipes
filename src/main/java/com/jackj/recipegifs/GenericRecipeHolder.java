package com.jackj.recipegifs;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

/**
 * Created by jackj on 18/01/2017.
 */

public abstract class GenericRecipeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    Recipe mRecipe;
    protected TextView mTitleTextView;
    protected ImageView recipeImage;
    protected LinearLayout tagLinearLayout;
    protected ImageView faveIcon;
    Context mContext;
    RecipeLab mRecipeLab;


    public GenericRecipeHolder(View itemView,Context context,RecipeLab rLab) {
        super(itemView);
        itemView.setOnClickListener(this);
        mContext=context;
        mRecipeLab=rLab;
        faveIcon=(ImageView)itemView.findViewById(R.id.favourite_icon);
        mTitleTextView = (TextView) itemView.findViewById(R.id.grid_item_recipe_title_text_view);
        recipeImage = (ImageView) itemView.findViewById(R.id.grid_item_imageview);
        tagLinearLayout=(LinearLayout)itemView.findViewById(R.id.tag_row_linear_layout);
    }

    public void bindRecipe(Recipe recipe) {
        mRecipe= recipe;
        loadThumbnail(mRecipe.getThumbnail());
        faveButtonSetup();
        tagsSetup();
        setTitleText(mRecipe.getTitle());

    }

    public void bindDrawable(Drawable drawable) {
        recipeImage.setImageDrawable(drawable);
    }
    public void loadThumbnail(String thumbnail){
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(recipeImage);
        Glide.with((mContext)).load(thumbnail).placeholder(R.drawable.blueback).into(imageViewTarget);
    }
    public void setTitleText(String title){
        mTitleTextView.setText(title);
    }
    public void faveButtonSetup(){

        final Drawable unsaved= mContext.getResources().getDrawable(R.drawable.gray_heart);
        final Drawable saved= mContext.getResources().getDrawable(R.drawable.gray_heart_filled);
        if(mRecipe.getSaved().equals("1")){
            faveIcon.setImageDrawable(saved);
        } else {
            faveIcon.setImageDrawable(unsaved);
        }
        //Log.i("debug","1");
        //Log.i("debug",mRecipe.getTitle());
        faveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecipe.getSaved().equals("1")){
                    mRecipeLab.changeSavedStatus("0",mRecipe.getId());
                    mRecipe.setSaved("0");
                    faveIcon.setImageDrawable(unsaved);
                } else{
                    mRecipeLab.changeSavedStatus("1",mRecipe.getId());
                    faveIcon.setImageDrawable(saved);
                    mRecipe.setSaved("1");
                    Toast.makeText(mContext, "Saved!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void tagsSetup(){
        String tags=mRecipe.getTags();
        if(tags.length()>2) {
            String[] tagslist = tags.substring(1, tags.length() - 1).split(", ");
            tagLinearLayout.removeAllViews();
            //Log.i("debug","4");
            for (String tag : tagslist) {
                if (!tag.equals("28") && !tag.equals("14")) {
                    ImageView tagImage = new ImageView(mContext);
                    Resources resources = mContext.getResources();
                    final int resourceId = resources.getIdentifier("i" + tag, "drawable", mContext.getPackageName());
                    Drawable drawable = mContext.getResources().getDrawable(resourceId);
                    //Log.i("debug","4");
                    float scale = mContext.getResources().getDisplayMetrics().density;
                    ImageView tagImageView = new ImageView(mContext);                //Display TAGS Section
                    tagImageView.setImageDrawable(drawable);
                    int paddingSize = (int) (4 * scale + 0.5f);
                    tagImageView.setPadding(paddingSize, paddingSize, paddingSize, paddingSize);
                    int viewSize = (int) (18 * scale + 0.5f);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            viewSize,
                            viewSize);
                    params.gravity = Gravity.CENTER;
                    //Log.i("debug","6");
                    tagImageView.setLayoutParams(params);

                    tagLinearLayout.addView(tagImageView);
                }
            }
        }
    }
}
