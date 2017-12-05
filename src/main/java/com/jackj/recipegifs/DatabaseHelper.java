package com.jackj.recipegifs;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jackj.recipegifs.RecipeDbSchema.RecipeTable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jackj on 08/12/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper{
    private static final int VERSION=1;
    private static final String DATABASE_NAME="recipebase.db";
    private String splitRecipe[][];
    private Context mContext;
    int count16=0;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+RecipeTable.NAME+"("
        +" _id integer primary key autoincrement, "+
        RecipeTable.Cols.UUID+", "+
        RecipeTable.Cols.TITLE+", "+
        RecipeTable.Cols.LINK+", "+
        RecipeTable.Cols.DESCRIPTION+", "+
        RecipeTable.Cols.PERMALINK+", "+
        RecipeTable.Cols.THUMBNAIL+", "+
        RecipeTable.Cols.TAGS+", "+
        RecipeTable.Cols.SAVED+", "+
        RecipeTable.Cols.COMMENTS+")");
        db.execSQL("create table "+ SearchCategoryDbSchema.SearchCategoryTable.NAME+"("
                +" _id integer primary key autoincrement, "+
                SearchCategoryDbSchema.SearchCategoryTable.Cols.UUID+", "+
                SearchCategoryDbSchema.SearchCategoryTable.Cols.CATEGORY+")");

        try {
            InputStream inputStream = mContext.getResources().openRawResource(R.raw.recipedata87);
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            inputStream.close();
            String recipefile = result.toString("UTF-8");
            String[] recipes = recipefile.split("---------------------------------", -1);
            splitRecipe = new String[recipes.length][7];
            for (int i = 0; i < recipes.length; i++) {
                splitRecipe[i] = recipes[i].split("---", -1);
                splitRecipe[i][0] = splitRecipe[i][0].replaceAll("(\\r|\\n)", "");
            }
        } catch (IOException fnf) {
            System.out.println("file not found");
        }
        for (int i = 0; i < splitRecipe.length; i++) {
            Log.i("db","hello ");
            String[] row = splitRecipe[i];
            if (row.length > 4) {
                Recipe recipe = new Recipe();
                recipe.setTitle(row[0]);
                recipe.setUrl(row[1]);

                if (row.length == 8) {
                    if(row[7].length()>5){//if gihub link exists
                        recipe.setThumbnail(row[7]);
                    } else{
                        recipe.setThumbnail(row[4]);
                    }
                    recipe.setDescription(row[2]);
                    recipe.setPermalink(row[3]);

                    recipe.setTags(row[5]);
                    recipe.setSaved("0");
                    recipe.setCommentsNum(row[6]);

                }
                if (row.length == 7) {
                    if(row[6].length()>5){
                        recipe.setThumbnail(row[6]);
                    } else{
                        recipe.setThumbnail(row[4]);
                    }
                    recipe.setDescription("No Instructions/Directions");
                    recipe.setPermalink(row[2]);
                    recipe.setThumbnail(row[3]);
                    recipe.setTags(row[4]);
                    recipe.setSaved("0");
                    recipe.setCommentsNum(row[5]);

                    //Log.i("db","cnum "+row[5]);
                }
                addRecipe(recipe, db);
                addTags(recipe, recipe.getTags(), db);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    private static ContentValues getContentValues(Recipe recipe) {
        ContentValues values = new ContentValues();
        values.put(RecipeTable.Cols.UUID, recipe.getId().toString());
        values.put(RecipeTable.Cols.TITLE, recipe.getTitle());
        values.put(RecipeTable.Cols.LINK, recipe.getUrl());
        values.put(RecipeTable.Cols.DESCRIPTION, recipe.getDescription());
        values.put(RecipeTable.Cols.PERMALINK, recipe.getPermalink());
        values.put(RecipeTable.Cols.THUMBNAIL, recipe.getThumbnail());
        values.put(RecipeTable.Cols.TAGS, recipe.getTags());
        values.put(RecipeTable.Cols.SAVED, recipe.getSaved());
        values.put(RecipeTable.Cols.COMMENTS, recipe.getCommentsNum());
        return values;
    }


    public void addRecipe(Recipe recipe,SQLiteDatabase db) {
        ContentValues values = getContentValues(recipe);
        db.insert(RecipeTable.NAME, null, values);
    }
    public void addTags(Recipe recipe, String tags,SQLiteDatabase db) {
        String[] tagslist = tags.substring(1, tags.length() - 1).split(", ");

        for (String tag : tagslist) {
            ContentValues values = new ContentValues();
            values.put(SearchCategoryDbSchema.SearchCategoryTable.Cols.UUID, recipe.getId().toString());
            values.put(SearchCategoryDbSchema.SearchCategoryTable.Cols.CATEGORY, tag);
            db.insert(SearchCategoryDbSchema.SearchCategoryTable.NAME, null, values);
        }

    }
}
