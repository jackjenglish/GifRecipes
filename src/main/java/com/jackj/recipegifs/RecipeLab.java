package com.jackj.recipegifs;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.jackj.recipegifs.RecipeDbSchema.RecipeTable;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by jackj on 21/11/2016.
 */
public class RecipeLab {
    private static RecipeLab sRecipeLab;
    private Context mContext;
    private ArrayList<Recipe> mRecipes;
    private SQLiteDatabase mDatabase;
    private String splitRecipe[][];


    public static RecipeLab get(Context context) {
        if (sRecipeLab == null) {
            sRecipeLab = new RecipeLab(context);
        }
        return sRecipeLab;
    }

    private RecipeLab(Context context) {
        mContext = context.getApplicationContext();
        mRecipes = new ArrayList<>();
        mDatabase = new DatabaseHelper(mContext).getWritableDatabase();
    }

    public RecipeCursorWrapper queryCrimes(String whereClause, String[] whereArgs,String limit) {
        Cursor cursor = mDatabase.query(
                RecipeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null,limit);
        return new RecipeCursorWrapper(cursor);
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
        values.put(RecipeTable.Cols.COMMENTS, recipe.getCommentsNum());
        return values;
    }

    public List<Recipe> getRecipes(String limit) {
        //return mRecipes;
        List<Recipe> recipes = new ArrayList<>();
        RecipeCursorWrapper cursor = queryCrimes(null, null,limit);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                recipes.add(cursor.getRecipe());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return recipes;
    }

    public Recipe getRecipe(UUID id) {
        RecipeCursorWrapper cursor = queryCrimes(
                RecipeTable.Cols.UUID + " = ?", new String[]{id.toString()},null
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getRecipe();
        } finally {
            cursor.close();
        }
    }
    public Recipe getRecipeFromName(String title) {
        Log.i("savedstatus","getrecipefromname");
        RecipeCursorWrapper cursor = queryCrimes(
                RecipeTable.Cols.TITLE + " = ?", new String[]{title},null
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getRecipe();
        } finally {
            cursor.close();
        }
    }
    public void changeSavedStatus(String savedStatus,UUID id){

        Log.i("savedstatus",savedStatus);
        ContentValues values = new ContentValues();
        values.put(RecipeTable.Cols.SAVED, savedStatus);

        String whereClause = RecipeTable.Cols.UUID+"=?";
        String[] whereArgs = new String[] {
                    id.toString()
        };
        mDatabase.update(RecipeTable.NAME, values, whereClause,whereArgs);

    }



}
