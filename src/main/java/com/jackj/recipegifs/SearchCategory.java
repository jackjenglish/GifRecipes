package com.jackj.recipegifs;

import android.graphics.drawable.Drawable;

/**
 * Created by jackj on 30/12/2016.
 */
public class SearchCategory {
    private String categoryTitle;
    private Drawable drawable;
    private String categoryCode;
    private int categoryResId;
    private int categoryBackground;
    private boolean selected;

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public int getCategoryResId() {
        return categoryResId;
    }

    public void setCategoryResId(int categoryResId) {
        this.categoryResId = categoryResId;
    }

    public int getCategoryBackground() {
        return categoryBackground;
    }

    public void setCategoryBackground(int categoryBackground) {
        this.categoryBackground = categoryBackground;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryId) {
        this.categoryCode = categoryId;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public void onSelect(){

    }
}
