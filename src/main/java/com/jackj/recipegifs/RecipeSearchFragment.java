package com.jackj.recipegifs;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.example.jackj.recipegifs.RecipeDbSchema.RecipeTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class RecipeSearchFragment extends Fragment implements View.OnClickListener{

    private RecyclerView recipeRecyclerView;
    private RecipeAdapterC mRecipeAdapter;
    private SQLiteDatabase mDatabase;
    //private ThumbnailDownloader<RecipeHolder> mThumbnailDownloader;
    private List<SearchCategory> categories;
    String whereClause;
    String whereArgs[];
    Cursor cursor;
    RecipeCursorWrapper cursorWrapper;
    private RecipeLab mRecipeLab;
    private boolean adapterSetUp=false;

    RelativeLayout RLbreakfast;
    RelativeLayout RLdinner;
    RelativeLayout RLdessert;
    RelativeLayout RLbeverages;
    RelativeLayout RLsides;
    RelativeLayout RLwestern;
    RelativeLayout RLasian;
    RelativeLayout RLitalian;
    RelativeLayout RLmexican;
    RelativeLayout RLindian;
    RelativeLayout RLpoultry;
    RelativeLayout RLbeef;
    RelativeLayout RLpork;
    RelativeLayout RLdairy;
    RelativeLayout RLveg;
    RelativeLayout RLseafood;

    List<RelativeLayout> categoryButtons;
    Map<Integer,String> categoryCodes=new HashMap<Integer,String>();
    Map<Integer,Integer> categoryBackgrounds=new HashMap<Integer,Integer>();
    List<String> searchQueries= new ArrayList<>();
    List<Recipe> pagerRecipes= new ArrayList<>();
    View view;
    CardView situationCard;
    FloatingActionButton myFab;
    LayoutInflater mInflater;
    ScrollView scroll;
    LinearLayout main;
    private boolean hasSearched=false;
    View popUpLayout;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mRecipeLab= RecipeLab.get(getActivity());
        mDatabase= new DatabaseHelper(getContext()).getWritableDatabase();
        //setUpResponseHandler();
        Log.i("SEARCHFRAGCREATED","created");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_page, container, false);

        hasSearched=false;
        adapterSetUp=false;
        mInflater= inflater;
        searchQueries= new ArrayList<>();       //reset in case of backpress
        pagerRecipes= new ArrayList<>();

        recipeRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_recipe_recycler_view);
        situationCard= (CardView)view.findViewById(R.id.card_view_situation);
        scroll=(ScrollView) view.findViewById(R.id.scroll);
        main= (LinearLayout) scroll.getParent();

        buttonSetUp(view);
        categorySetUp();

        categoryButtons= new ArrayList<>();
        categoryButtons.add(RLbreakfast);
        categoryButtons.add(RLdinner);
        categoryButtons.add(RLdessert);
        categoryButtons.add(RLbeverages);
        categoryButtons.add(RLsides);
        categoryButtons.add(RLwestern);
        categoryButtons.add(RLasian);
        categoryButtons.add(RLitalian);
        categoryButtons.add(RLmexican);
        categoryButtons.add(RLindian);
        categoryButtons.add(RLseafood);
        categoryButtons.add(RLveg);
        categoryButtons.add(RLpoultry);
        categoryButtons.add(RLbeef);
        categoryButtons.add(RLpork);
        categoryButtons.add(RLdairy);

        categoryCodes.put(RLbreakfast.getId(),"1");
        categoryCodes.put(RLdinner.getId(),"2");
        categoryCodes.put(RLdessert.getId(),"3");
        categoryCodes.put(RLmexican.getId(),"4");
        categoryCodes.put(RLindian.getId(),"5");
        categoryCodes.put(RLitalian.getId(),"6");
        categoryCodes.put(RLasian.getId(),"7");
        categoryCodes.put(RLwestern.getId(),"8");
        categoryCodes.put(RLbeverages.getId(),"11");
        categoryCodes.put(RLveg.getId(),"12");
        categoryCodes.put(RLsides.getId(),"15");
        categoryCodes.put(RLseafood.getId(),"16");
        categoryCodes.put(RLpoultry.getId(),"17");
        categoryCodes.put(RLbeef.getId(),"18");
        categoryCodes.put(RLdairy.getId(),"20");
        categoryCodes.put(RLpork.getId(),"19");

        categoryBackgrounds.put(RLbreakfast.getId(),R.drawable.border3);
        categoryBackgrounds.put(RLdinner.getId(),R.drawable.border4);
        categoryBackgrounds.put(RLdessert.getId(),R.drawable.border3);
        categoryBackgrounds.put(RLmexican.getId(),R.drawable.border4);
        categoryBackgrounds.put(RLindian.getId(),R.drawable.border3);
        categoryBackgrounds.put(RLitalian.getId(),R.drawable.border3);
        categoryBackgrounds.put(RLasian.getId(),R.drawable.border4);
        categoryBackgrounds.put(RLwestern.getId(),R.drawable.border3);
        categoryBackgrounds.put(RLbeverages.getId(),R.drawable.border4);
        categoryBackgrounds.put(RLveg.getId(),R.drawable.border3);
        categoryBackgrounds.put(RLsides.getId(),R.drawable.border3);
        categoryBackgrounds.put(RLseafood.getId(),R.drawable.border4);
        categoryBackgrounds.put(RLpoultry.getId(),R.drawable.border3);
        categoryBackgrounds.put(RLbeef.getId(),R.drawable.border4);
        categoryBackgrounds.put(RLdairy.getId(),R.drawable.border4);
        categoryBackgrounds.put(RLpork.getId(),R.drawable.border3);

        return view;
    }

    /*private class RecipeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Recipe mRecipe;
        private TextView mTitleTextView;
        private ImageView recipeImage;
        private LinearLayout tagLinearLayout;
        private ImageView faveIcon;

        public RecipeHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.grid_item_recipe_title_text_view);
            recipeImage = (ImageView) itemView.findViewById(R.id.grid_item_imageview);
            tagLinearLayout=(LinearLayout)itemView.findViewById(R.id.tag_row_linear_layout);
        }

        public void bindRecipe(Recipe recipe) {
            mRecipe= recipe;
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
                        Toast.makeText(getActivity(), "Saved!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
            String tags=mRecipe.getTags();
            if(tags.length()>2) {
                String[] tagslist = tags.substring(1, tags.length() - 1).split(", ");
                tagLinearLayout.removeAllViews();
                //Log.i("debug","4");
                for (String tag : tagslist) {
                    if (!tag.equals("28") && !tag.equals("14")) {
                        ImageView tagImage = new ImageView(getContext());
                        Resources resources = getContext().getResources();
                        final int resourceId = resources.getIdentifier("i" + tag, "drawable", getContext().getPackageName());
                        Drawable drawable = getResources().getDrawable(resourceId);
                        //Log.i("debug","4");
                        float scale = getResources().getDisplayMetrics().density;
                        ImageView tagImageView = new ImageView(getContext());                //Display TAGS Section
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
            //String title = Charset.forName("UTF-8").encode(mRecipe.getTitle());
            mTitleTextView.setText(mRecipe.getTitle());
        }
        public void bindDrawable(Drawable drawable) {
            recipeImage.setImageDrawable(drawable);
        }
        @Override
        public void onClick(View v) {
            ArrayList<Recipe> pagerRecipes= (ArrayList<Recipe>) mRecipeAdapter.getRecipes();
            ArrayList<String> pagerRecipesIds=new ArrayList<>();
            for(Recipe recipe:pagerRecipes){
                pagerRecipesIds.add((recipe.getId()).toString());
            }
            Intent i = RecipeActivity.newIntent(getActivity(), mRecipe.getId(),pagerRecipesIds);
            startActivity(i);
        }
    }*/
   /* private class RecipeAdapter extends RecyclerView.Adapter<RecipeHolder> {
        private List<Recipe> mRecipes;
        public RecipeAdapter(List<Recipe> recipes) {
            mRecipes = recipes;
        }

        @Override
        public RecipeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.grid_item, parent, false);
            //Log.i("onCreate","vh");
            return new RecipeHolder(view);
        }

        @Override
        public void onBindViewHolder(RecipeHolder holder, int position) {
            Recipe recipe = mRecipes.get(position);
            //Log.i("recipe",""+position);
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

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_list_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //Take string and build sql query
                Log.i("query", "Submitted: "+s);
                whereClause = RecipeTable.Cols.TITLE+"like "+"%"+"?"+"%";
                whereArgs = new String[] {
                        "%"+s+"%"
                };
                cursor= mDatabase.query(
                        RecipeDbSchema.RecipeTable.NAME,
                        null,
                        RecipeTable.Cols.TITLE+" LIKE ?",
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
                        Log.i("query", "QueryTextSubmit: " + cursorWrapper.getRecipe().getTitle());
                        cursorWrapper.moveToNext();
                    }
                } finally {
                    cursor.close();
                }
                Log.i("before", "sort ");
                /*
                List<Recipe> sortedRecipes=new ArrayList<>();
                for(int i=0;i<recipes.size();i++){
                    Recipe recipe= recipes.get(i);
                    if((recipe.getTitle().toUpperCase()).contains(" "+s.toUpperCase()+" ")){//VERY inefficient using arraylist like this consider linkedlist http://stackoverflow.com/questions/20186681/how-to-move-specific-item-in-array-list-to-the-first-item
                        sortedRecipes.add(recipe);
                        recipes.remove(i);
                    }
                }
                sortedRecipes.addAll(recipes);*/
                updateDataset(recipes);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("query", "QueryTextChange: " + s);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String query = QueryPreferences.getStoredQuery(getActivity());
                //searchView.setQuery(query, false);
            }
        });
    }
    public void setUpAdapter(List<Recipe> queriedRecipes){
        View cLayout =getLayoutInflater(getArguments()).inflate(R.layout.recyclerfab,null);
        main.removeView(scroll);
        main.addView(cLayout);
        fabSetup();
        recipeRecyclerView = (RecyclerView) main.findViewById(R.id.fragment_recipe_recycler_view);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recipeRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        mRecipeAdapter = new RecipeAdapterC(queriedRecipes,getContext());
        recipeRecyclerView.setAdapter(mRecipeAdapter);
        adapterSetUp=true;
    }
    public void updateDataset(List<Recipe> queriedRecipes){
        Log.i("insideupdate", ""+queriedRecipes.size());
        if(adapterSetUp){
            Log.i("insideiuf", "vfdfv");
            mRecipeAdapter.setRecipes(queriedRecipes);
            Log.i("insideiuf", "vfdfv");
            mRecipeAdapter.notifyDataSetChanged();
            ;Log.i("insideiuf", "vfdfv");
        } else{
            setUpAdapter(queriedRecipes);
        }
    }
    @Override
    public void onClick(View v) {
        int id= v.getId();
            for(RelativeLayout rlayout:categoryButtons){
                if(rlayout.getId()==id){
                    SearchCategory selectedCategory=null;
                    for(SearchCategory category:categories){
                        if(category.getCategoryResId()==id){
                            selectedCategory=category;
                        }
                    }
                    Log.i("onClick",""+selectedCategory.getCategoryTitle() +selectedCategory.getCategoryCode());

                    if(!hasSearched){
                        setUpAdapter(null);
                        categorySearch(selectedCategory.getCategoryCode());
                        hasSearched=true;
                    } else {
                        onSelectCategory(selectedCategory);
                    }
                }
        }
    }
    public void buttonSetUp(View view){

        RLbreakfast= (RelativeLayout) view.findViewById(R.id.situation_rlayout_breakfast);
        RLbreakfast.setOnClickListener(this);
        RLdinner= (RelativeLayout) view.findViewById(R.id.situation_rlayout_dinner);
        RLdinner.setOnClickListener(this);
        RLdessert= (RelativeLayout) view.findViewById(R.id.situation_rlayout_dessert);
        RLdessert.setOnClickListener(this);
        RLbeverages= (RelativeLayout) view.findViewById(R.id.situation_rlayout_beverages);
        RLbeverages.setOnClickListener(this);
        RLsides= (RelativeLayout) view.findViewById(R.id.situation_rlayout_sides);
        RLsides.setOnClickListener(this);

        RLwestern= (RelativeLayout) view.findViewById(R.id.nation_rlayout_western);
        RLwestern.setOnClickListener(this);
        RLasian= (RelativeLayout) view.findViewById(R.id.nation_rlayout_asian);
        RLasian.setOnClickListener(this);
        RLitalian= (RelativeLayout) view.findViewById(R.id.nation_rlayout_italian);
        RLitalian.setOnClickListener(this);
        RLmexican= (RelativeLayout) view.findViewById(R.id.nation_rlayout_mexican);
        RLmexican.setOnClickListener(this);
        RLindian= (RelativeLayout) view.findViewById(R.id.nation_rlayout_indian);
        RLindian.setOnClickListener(this);

        RLpoultry= (RelativeLayout) view.findViewById(R.id.nation_rlayout_poultry);
        RLpoultry.setOnClickListener(this);
        RLbeef= (RelativeLayout) view.findViewById(R.id.nation_rlayout_Beef);
        RLbeef.setOnClickListener(this);
        RLpork= (RelativeLayout) view.findViewById(R.id.nation_rlayout_pork);
        RLpork.setOnClickListener(this);
        RLdairy= (RelativeLayout) view.findViewById(R.id.nation_rlayout_dairy);
        RLdairy.setOnClickListener(this);
        RLveg= (RelativeLayout) view.findViewById(R.id.nation_rlayout_vegetarianvegan);
        RLveg.setOnClickListener(this);
        RLseafood= (RelativeLayout) view.findViewById(R.id.nation_rlayout_seafood);
        RLseafood.setOnClickListener(this);


    }
    public void categorySearch(String query){
        //CHANGE BUTTON COLOR:

        //PASS IN ARRAYLIST OF RECIPES (DEAFAULT ALL) AND ONLY SHOW RECIPES THAT ARE IN LIST AND ARE RETURNED FROM SEARCH.
        if(query.equals("refresh")){
        } else{
            searchQueries.add(query);
        }
        String[] whereArgs= new String[searchQueries.size()];
        for(int i=0;i<searchQueries.size();i++){
            whereArgs[i]=searchQueries.get(i);
        }
        whereClause ="";
        for(int i=0;i<searchQueries.size();i++){
            if(i==0){
                whereClause += SearchCategoryDbSchema.SearchCategoryTable.Cols.CATEGORY+"=?";
            } else{
                whereClause += " or "+SearchCategoryDbSchema.SearchCategoryTable.Cols.CATEGORY+"=?";
            }

        }

        cursor= mDatabase.query(
                SearchCategoryDbSchema.SearchCategoryTable.NAME,
                null,
                whereClause,
                whereArgs,
                SearchCategoryDbSchema.SearchCategoryTable.Cols.UUID,
                "count(uuid)="+searchQueries.size(),
                null);
        CategoryCursorWrapper cursorWrapper= new CategoryCursorWrapper(cursor);
        List<UUID> uuids= new ArrayList<>();
        List<Recipe> recipes= new ArrayList<>();
        try{
            cursorWrapper.moveToFirst();
            while (!cursor.isAfterLast()){
                Recipe recipe=mRecipeLab.getRecipe(cursorWrapper.getUUID());
                recipes.add(recipe);
                cursorWrapper.moveToNext();
            }
        } finally {
            cursor.close();
        }
        updateDataset(recipes);
    }
    private void fabSetup(){
        final FloatingActionButton myFab = (FloatingActionButton) main.findViewById(R.id.myFAB);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                int[] location = new int[2];
                v.getLocationOnScreen(location);
                Point point = new Point();
                point.x = location[0];
                point.y = location[1];
                showStatusPopup(myFab,point);
            }
        });

    }
    private void showStatusPopup(final View anchor,Point p) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.search_page, null);
        popUpLayout=layout;
        // Creating the PopupWindow
        PopupWindow changeStatusPopUp = new PopupWindow(getActivity());
        changeStatusPopUp.setContentView(layout);
        filterSetUp(popUpLayout);
        changeStatusPopUp.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setHeight(1200);
        changeStatusPopUp.setFocusable(true);
        changeStatusPopUp.setAnimationStyle(R.style.Animation);

        // Some offset to align the popup_show a bit to the left, and a bit down, relative to button's position.
        int OFFSET_X = -20;
        int OFFSET_Y = 50;
        changeStatusPopUp.showAtLocation(anchor, Gravity.NO_GRAVITY,2000,p.y-changeStatusPopUp.getHeight()-25);
    }
    private void filterSetUp(View view){
        RLbreakfast= (RelativeLayout) view.findViewById(R.id.situation_rlayout_breakfast);
        RLbreakfast.setOnClickListener(this);
        //if(searchQueries.contains(categoryCodes.get(RLbreakfast.getId()))){RLbreakfast.setBackgroundColor(getResources().getColor(R.color.gray));}
        RLdinner= (RelativeLayout) view.findViewById(R.id.situation_rlayout_dinner);
        RLdinner.setOnClickListener(this);
        //if(searchQueries.contains(categoryCodes.get(RLdinner.getId()))){RLdinner.setBackgroundColor(getResources().getColor(R.color.gray));}

        RLdessert= (RelativeLayout) view.findViewById(R.id.situation_rlayout_dessert);
        RLdessert.setOnClickListener(this);
        //if(searchQueries.contains(categoryCodes.get(RLdessert.getId()))){RLdessert.setBackgroundColor(getResources().getColor(R.color.gray));}

        RLbeverages= (RelativeLayout) view.findViewById(R.id.situation_rlayout_beverages);
        RLbeverages.setOnClickListener(this);
        //if(searchQueries.contains(categoryCodes.get(RLbeverages.getId()))){RLbeverages.setBackgroundColor(getResources().getColor(R.color.gray));}

        RLsides= (RelativeLayout) view.findViewById(R.id.situation_rlayout_sides);
        RLsides.setOnClickListener(this);
        //if(searchQueries.contains(categoryCodes.get(RLsides.getId()))){RLsides.setBackgroundColor(getResources().getColor(R.color.gray));}

        RLwestern= (RelativeLayout) view.findViewById(R.id.nation_rlayout_western);
        RLwestern.setOnClickListener(this);
        //if(searchQueries.contains(categoryCodes.get(RLwestern.getId()))){RLwestern.setBackgroundColor(getResources().getColor(R.color.gray));}

        RLasian= (RelativeLayout) view.findViewById(R.id.nation_rlayout_asian);
        RLasian.setOnClickListener(this);
        //if(searchQueries.contains(categoryCodes.get(RLasian.getId()))){RLasian.setBackgroundColor(getResources().getColor(R.color.gray));}

        RLitalian= (RelativeLayout) view.findViewById(R.id.nation_rlayout_italian);
        RLitalian.setOnClickListener(this);
        //if(searchQueries.contains(categoryCodes.get(RLitalian.getId()))){RLitalian.setBackgroundColor(getResources().getColor(R.color.gray));}

        RLmexican= (RelativeLayout) view.findViewById(R.id.nation_rlayout_mexican);
        RLmexican.setOnClickListener(this);
        //if(searchQueries.contains(categoryCodes.get(RLmexican.getId()))){RLmexican.setBackgroundColor(getResources().getColor(R.color.gray));}

        RLindian= (RelativeLayout) view.findViewById(R.id.nation_rlayout_indian);
        RLindian.setOnClickListener(this);
        //if(searchQueries.contains(categoryCodes.get(RLindian.getId()))){RLindian.setBackgroundColor(getResources().getColor(R.color.gray));}

        RLpoultry= (RelativeLayout) view.findViewById(R.id.nation_rlayout_poultry);
        RLpoultry.setOnClickListener(this);
        //if(searchQueries.contains(categoryCodes.get(RLpoultry.getId()))){RLpoultry.setBackgroundColor(getResources().getColor(R.color.gray));}

        RLbeef= (RelativeLayout) view.findViewById(R.id.nation_rlayout_Beef);
        RLbeef.setOnClickListener(this);


        RLpork= (RelativeLayout) view.findViewById(R.id.nation_rlayout_pork);
        RLpork.setOnClickListener(this);


        RLdairy= (RelativeLayout) view.findViewById(R.id.nation_rlayout_dairy);
        RLdairy.setOnClickListener(this);


        RLveg= (RelativeLayout) view.findViewById(R.id.nation_rlayout_vegetarianvegan);
        RLveg.setOnClickListener(this);


        RLseafood= (RelativeLayout) view.findViewById(R.id.nation_rlayout_seafood);
        RLseafood.setOnClickListener(this);

        for(int i=0;i<searchQueries.size();i++){
        for(SearchCategory cat:categories){
            if(cat.getCategoryCode().equals(searchQueries.get(i))){
                RelativeLayout selected= (RelativeLayout) view.findViewById(cat.getCategoryResId());
                setSelectedColor(cat, view);
            }
        }}

    }

    private void onSelectCategory(SearchCategory cat){
        if(cat.isSelected()){
            Log.i("onClick","iselected");
            setUnselectedColor(cat,popUpLayout);
            searchQueries.remove(searchQueries.indexOf(cat.getCategoryCode()));
            categorySearch("refresh");
        } else{
            Log.i("onClick","isnotselected");
            setSelectedColor(cat,popUpLayout);
            categorySearch(cat.getCategoryCode());
        }
        //Log.i("equal","to "+d.toString());
    }
    private void setSelectedColor(SearchCategory cat,View view){
        RelativeLayout category=(RelativeLayout)view.findViewById(cat.getCategoryResId());
        category.setBackground(getResources().getDrawable(R.drawable.gray_background));
        cat.setSelected(true);
    }
    private void setUnselectedColor(SearchCategory cat,View view){
        RelativeLayout category=(RelativeLayout)view.findViewById(cat.getCategoryResId());
        category.setBackground(getResources().getDrawable(cat.getCategoryBackground()));
        cat.setSelected(false);
    }
    /*private void setUpResponseHandler(){
        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
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
        mThumbnailDownloader.getLooper();
    }*/
    private void categorySetUp(){
        categories=new ArrayList<>();

        SearchCategory breakfast = new SearchCategory();
        breakfast.setCategoryTitle("Breakfast");
        breakfast.setCategoryCode("1");
        breakfast.setCategoryResId(RLbreakfast.getId());

        SearchCategory dinner = new SearchCategory();
        dinner.setCategoryTitle("Dinner");
        dinner.setCategoryCode("2");
        dinner.setCategoryResId(RLdinner.getId());

        SearchCategory dessert = new SearchCategory();
        dessert.setCategoryTitle("Dessert");
        dessert.setCategoryCode("3");
        dessert.setCategoryResId(RLdessert.getId());

        SearchCategory mexican = new SearchCategory();
        mexican.setCategoryTitle("Mexican");
        mexican.setCategoryCode("4");
        mexican.setCategoryResId(RLmexican.getId());

        SearchCategory indian = new SearchCategory();
        indian.setCategoryTitle("Indian");
        indian.setCategoryCode("5");
        indian.setCategoryResId(RLindian.getId());

        SearchCategory italian = new SearchCategory();
        italian.setCategoryTitle("Italian");
        italian.setCategoryCode("6");
        italian.setCategoryResId(RLitalian.getId());

        SearchCategory asian = new SearchCategory();
        asian.setCategoryTitle("Chinese");
        asian.setCategoryCode("7");
        asian.setCategoryResId(RLasian.getId());


        SearchCategory western= new SearchCategory();
        western.setCategoryTitle("Western");
        western.setCategoryCode("8");
        western.setCategoryResId(RLwestern.getId());

        SearchCategory beverages = new SearchCategory();
        beverages.setCategoryTitle("Beverages");
        beverages.setCategoryCode("11");
        beverages.setCategoryResId(RLbeverages.getId());

        SearchCategory veg = new SearchCategory();
        veg.setCategoryTitle("Vegan");
        veg.setCategoryCode("12");
        veg.setCategoryResId(RLveg.getId());


        SearchCategory sides = new SearchCategory();
        sides.setCategoryTitle("Sides");
        sides.setCategoryCode("15");
        sides.setCategoryResId(RLsides.getId());

        SearchCategory seafood = new SearchCategory();
        seafood.setCategoryTitle("Seafood");
        seafood.setCategoryCode("16");
        seafood.setCategoryResId(RLseafood.getId());

        SearchCategory poultry = new SearchCategory();
        poultry.setCategoryTitle("poultry");
        poultry.setCategoryCode("17");
        poultry.setCategoryResId(RLpoultry.getId());

        SearchCategory beef = new SearchCategory();
        beef.setCategoryTitle("beef");
        beef.setCategoryCode("18");
        beef.setCategoryResId(RLbeef.getId());

        SearchCategory dairy = new SearchCategory();
        dairy.setCategoryTitle("dairy");
        dairy.setCategoryCode("20");
        dairy.setCategoryResId(RLdairy.getId());

        SearchCategory pork = new SearchCategory();
        pork.setCategoryTitle("pork");
        pork.setCategoryCode("19");
        pork.setCategoryResId(RLpork.getId());


        breakfast.setCategoryBackground(R.drawable.border3);
        dinner.setCategoryBackground(R.drawable.border4);
        dessert.setCategoryBackground(R.drawable.border3);
        mexican.setCategoryBackground(R.drawable.border4);
        indian.setCategoryBackground(R.drawable.border3);
        italian.setCategoryBackground(R.drawable.border3);
        asian.setCategoryBackground(R.drawable.border4);
        western.setCategoryBackground(R.drawable.border3);
        beverages.setCategoryBackground(R.drawable.border4);
        veg.setCategoryBackground(R.drawable.border4);
        sides.setCategoryBackground(R.drawable.border3);
        seafood.setCategoryBackground(R.drawable.border3);
        poultry.setCategoryBackground(R.drawable.border3);
        beef.setCategoryBackground(R.drawable.border4);
        dairy.setCategoryBackground(R.drawable.border4);
        pork.setCategoryBackground(R.drawable.border3);


        //First category--->random recipe
        categories.add(breakfast);
        categories.add(dinner);
        categories.add(dessert);
        categories.add(beverages);
        categories.add(sides);
        categories.add(western);
        categories.add(asian);
        categories.add(italian);
        categories.add(mexican);
        categories.add(indian);
        categories.add(seafood);
        categories.add(veg);
        categories.add(poultry);
        categories.add(beef);
        categories.add(pork);
        categories.add(dairy);
    }

}