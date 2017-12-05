package com.jackj.recipegifs;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by jackj on 05/01/2017.
 */
public class RecipeSavedFragment extends Fragment {

    private RecyclerView recipeRecyclerView;
    private RecipeAdapterSaved mAdapter;
    Cursor cursor;
    private SQLiteDatabase mDatabase;
    RecipeCursorWrapper cursorWrapper;
    RecipeLab recipeLab;
    List<Recipe> savedRecipes;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDatabase = new DatabaseHelper(getContext()).getWritableDatabase();
        recipeLab=RecipeLab.get(getActivity());
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
        //Log.i("saved","debug0");
    }
    //private ThumbnailDownloader<RecipeHolder> mThumbnailDownloader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_recipe_list,container,false);//INFLATE NO SAVED RECIPES VIEW
        savedRecipes= getSavedRecipes();
        view= inflater.inflate(R.layout.fragment_recipe_list,container,false);
        recipeRecyclerView=(RecyclerView) view.findViewById(R.id.fragment_recipe_recycler_view);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recipeRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new RecipeAdapterSaved(savedRecipes,getContext());
        recipeRecyclerView.setAdapter(mAdapter);
        //Log.i("saved","debug3");
        return view;
    }

   /* private class RecipeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleTextView;
        Recipe mRecipe;
        private RelativeLayout rLayout;
        private LinearLayout tagLinearLayout;
        private ImageView recipeImage;
        private ImageView faveIcon;


        View itemV;
        StaggeredGridLayoutManager.LayoutParams layoutParams;

        public RecipeHolder(View itemView) {
            super(itemView);
            itemV=itemView;
            itemView.setOnClickListener(this);
            mTitleTextView= (TextView) itemView.findViewById(R.id.grid_item_recipe_title_text_view);
            tagLinearLayout=(LinearLayout)itemView.findViewById(R.id.tag_row_linear_layout);
            recipeImage=(ImageView) itemView.findViewById(R.id.grid_item_imageview);

        }
        public void bindRecipe(Recipe recipe){
            mRecipe= recipe;
            //Log.i("saved","bind");
            if(getPosition()==0){
                GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(recipeImage);
                Glide.with(getContext()).load("http://i.imgur.com/Lox9ZPR.png").into(imageViewTarget);
                mTitleTextView.setText("Random GifRecipe");
            } else{
                GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(recipeImage);
                Glide.with(getContext()).load(mRecipe.getThumbnail()).placeholder(R.drawable.blueback).into(imageViewTarget);
                faveIcon=(ImageView)itemView.findViewById(R.id.favourite_icon);
                final Drawable unsaved= getActivity().getResources().getDrawable(R.drawable.gray_heart);
                final Drawable saved= getActivity().getResources().getDrawable(R.drawable.gray_heart_filled);
                if(mRecipe.getSaved().equals("1")){
                    faveIcon.setImageDrawable(saved);
                } else {
                    faveIcon.setImageDrawable(unsaved);
                }
                faveIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //if(mRecipe.getDescription().equals("Random Recipe")){
                        //    Log.i("faveIcon","Clicked Random Fave");
                        // } else {
                        Log.i("faveIcon","Clicked  Fave");
                        if (mRecipe.getSaved().equals("1")) {
                            Log.i("faveIcon","Fave==1");
                            recipeLab.changeSavedStatus("0", mRecipe.getId());
                            faveIcon.setImageDrawable(unsaved);
                        } else {
                            Log.i("faveIcon","Fave==0");
                            Log.i("faveIcon","Fave==0");
                            recipeLab.changeSavedStatus("1", mRecipe.getId());
                            faveIcon.setImageDrawable(saved);
                        }
                        Log.i("faveIcon Position",""+getPosition());
                        savedRecipes.remove(getPosition());
                        mAdapter.setRecipes(savedRecipes);
                        mAdapter.notifyItemRemoved(getPosition());
                        //}

                    }
                });
                String tags=mRecipe.getTags();
                //Log.i("drawable",tags);
                if(tags.length()>2) {
                    String[] tagslist = tags.substring(1, tags.length() - 1).split(", ");
                    tagLinearLayout.removeAllViews();
                    for (String tag : tagslist) {
                        if (!tag.equals("28") && !tag.equals("14")){


                            ImageView tagImage= new ImageView(getContext());
                            Resources resources = getContext().getResources();
                            final int resourceId = resources.getIdentifier("i"+tag, "drawable", getContext().getPackageName());
                            // Log.i("inside ","i"+tag);
                            //Log.i("drawable",""+resourceId);
                            Drawable drawable= getResources().getDrawable(resourceId);

                            float scale = getResources().getDisplayMetrics().density;
                            ImageView tagImageView= new ImageView(getContext());                //Display TAGS Section
                            tagImageView.setImageDrawable(drawable);
                            //   tagImageView.setColorFilter(ContextCompat.getColor(getContext(),R.color.gray));
                            int paddingSize = (int) (4*scale + 0.5f);
                            tagImageView.setPadding(paddingSize,paddingSize,paddingSize,paddingSize);

                            int viewSize = (int) (18*scale + 0.5f);
                            LinearLayout.LayoutParams params =new LinearLayout.LayoutParams(
                                    viewSize,
                                    viewSize);
                            params.gravity= Gravity.CENTER;
                            tagImageView.setLayoutParams(params);

                            tagLinearLayout.addView(tagImageView);
                        }
                        //tagImage.setImageDrawable();
                    }
                }
                mTitleTextView.setText(mRecipe.getTitle());
                layoutParams = (StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams();
            }



        }
        public void bindDrawable(Drawable drawable){
            recipeImage.setImageDrawable(drawable);
        }
        @Override
        public void onClick(View v) {
            randomizeZero();
            ArrayList<Recipe> pagerRecipes= (ArrayList<Recipe>) mAdapter.getRecipes();
            ArrayList<String> pagerRecipesIds=new ArrayList<>();
            for(Recipe recipe:pagerRecipes){
                pagerRecipesIds.add((recipe.getId()).toString());
            }
            Intent i = RecipeActivity.newIntent(getActivity(), mRecipe.getId(),pagerRecipesIds);
            //Log.i("recipe",""+mRecipe.getTitle());




            /*if(mRecipe.getDescription().equals("Random Recipe")){
                //Log.i("inseide random","yes");
                List<Recipe> rList=(RecipeLab.get(getContext()).getRecipes(null));
                int randomRec= ThreadLocalRandom.current().nextInt(0, rList.size());

                UUID randomId=(RecipeLab.get(getContext()).getRecipes(null)).get(randomRec).getId();

                Recipe recipe=RecipeLab.get(getContext()).getRecipe(randomId);
                pagerRecipesIds=new ArrayList<>();
                pagerRecipesIds.add((recipe.getId()).toString());
                i = RecipeActivity.newIntent(getActivity(), randomId,pagerRecipesIds);
            }

            //Log.i("intent","r");
            startActivity(i);
        }
    }*/
    /*private class RecipeAdapter extends RecyclerView.Adapter<RecipeHolder>{
        private List<Recipe> mRecipes;
        public RecipeAdapter(List<Recipe> recipes){
            mRecipes= recipes;
        }
        @Override
        public RecipeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater= LayoutInflater.from(getActivity());
            View view= layoutInflater.inflate(R.layout.grid_item,parent,false);
            return new RecipeHolder(view);

        }

        @Override
        public void onBindViewHolder(RecipeHolder holder, int position) {
            Recipe recipe= mRecipes.get(position);
            //setFadeAnimation(holder.itemView);
            //Log.i("saved","onbind");
            holder.bindRecipe(recipe);
            //mThumbnailDownloader.queueThumbnail(holder, recipe.getThumbnail());
        }

        @Override
        public int getItemCount() {
            return mRecipes.size();
        }
        public void setRecipes(List<Recipe> recipes){
            mRecipes=recipes;
        }
        public List<Recipe> getRecipes() {
            return mRecipes;
        }
    }*/

    public List<Recipe> getSavedRecipes(){
        //Take string and build sql query
        String whereClause = RecipeDbSchema.RecipeTable.Cols.SAVED+"=?";
        String[] whereArgs = new String[] {
                "1"
        };
        cursor= mDatabase.query(
                RecipeDbSchema.RecipeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);
        cursorWrapper= new RecipeCursorWrapper(cursor);
        List<Recipe> recipes= new ArrayList<>();
        try{
            cursorWrapper.moveToFirst();
            while (!cursor.isAfterLast()){
                recipes.add(cursorWrapper.getRecipe());
                cursorWrapper.moveToNext();
            }
        } finally {
            cursor.close();
        }
        /*Recipe recipe=new Recipe();
        recipe.setTitle("Random GifRecipe");
        recipe.setUrl("http://i.imgur.com/Lox9ZPR.png");
        recipe.setDescription("Random Recipe");
        recipe.setTags("[]");
        recipe.setCommentsNum("0");
        recipe.setSaved("0");
        recipe.setPermalink("http://i.imgur.com/4UbrJsv.png");
        recipe.setThumbnail("http://i.imgur.com/Lox9ZPR.png");*/

        List<Recipe> rList=(RecipeLab.get(getContext()).getRecipes(null));
        int randomRec= ThreadLocalRandom.current().nextInt(0, rList.size());
        UUID randomId=(RecipeLab.get(getContext()).getRecipes(null)).get(randomRec).getId();
        Recipe recipe=RecipeLab.get(getContext()).getRecipe(randomId);

        //Log.i("added random","yes");
        recipes.add(0,recipe);

       // Log.i("returning saved","yes");
        return recipes;
    }
    public void randomizeZero(){
        List<Recipe> rList=(RecipeLab.get(getContext()).getRecipes(null));
        int randomRec= ThreadLocalRandom.current().nextInt(0, rList.size());
        UUID randomId=(RecipeLab.get(getContext()).getRecipes(null)).get(randomRec).getId();
        Recipe recipe=RecipeLab.get(getContext()).getRecipe(randomId);

        mAdapter.getRecipes().get(0).setId(randomId);
       // mAdapter.setRecipes(mAdapter.getRecipes().remove(0))
    }
}