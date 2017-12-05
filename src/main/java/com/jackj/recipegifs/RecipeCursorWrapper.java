package com.example.jackj.recipegifs;


import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.jackj.recipegifs.RecipeDbSchema.RecipeTable;

import java.util.UUID;

/**
 * Created by jackj on 08/12/2016.
 */
public class RecipeCursorWrapper extends CursorWrapper {
    public RecipeCursorWrapper(Cursor cursor){
        super(cursor);
    }
    public Recipe getRecipe(){
        String uuidString = getString(getColumnIndex(RecipeTable.Cols.UUID));
        String title = getString(getColumnIndex(RecipeTable.Cols.TITLE));
        String link = getString(getColumnIndex(RecipeTable.Cols.LINK));
        String description = getString(getColumnIndex(RecipeTable.Cols.DESCRIPTION));
        String permalink = getString(getColumnIndex(RecipeTable.Cols.PERMALINK));
        String thumbnail = getString(getColumnIndex(RecipeTable.Cols.THUMBNAIL));
        String tags= getString(getColumnIndex(RecipeTable.Cols.TAGS));
        String saved= getString(getColumnIndex(RecipeTable.Cols.SAVED));
        String commentsNum= getString(getColumnIndex(RecipeTable.Cols.COMMENTS));

        Recipe recipe= new Recipe(UUID.fromString(uuidString));
        recipe.setTitle(title);
        recipe.setUrl(link);
        recipe.setDescription(description);
        recipe.setPermalink(permalink);
        recipe.setThumbnail(thumbnail);
        recipe.setTags(tags);
        recipe.setSaved(saved);
        recipe.setCommentsNum(commentsNum);
        return recipe;
    }
}
