package com.jackj.recipegifs;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jackj on 14/01/2017.
 */
public class RecipeWrapper implements Serializable {

    private static final long serialVersionUID = 1L;
    private ArrayList<Recipe> itemDetails;

    public RecipeWrapper(ArrayList<Recipe> items) {
        this.itemDetails = items;
    }

    public ArrayList<Recipe> getItemDetails() {
        return itemDetails;
    }
}