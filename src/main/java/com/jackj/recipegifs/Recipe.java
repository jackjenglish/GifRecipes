package com.jackj.recipegifs;

import java.util.List;
import java.util.UUID;

/**
 * Created by jackj on 21/11/2016.
 */
public class Recipe {
    private String mTitle;
    private UUID mId;
    private String mUrl;
    private String permalink;
    private int timeTaken;
    private String description;
    private String mThumbnail;
    private List<String> ingredients;
    private String mTags;
    private String mSaved;
    private String commentsNum="notset";


    public Recipe(){
        this(UUID.randomUUID());

    }

    public Recipe(UUID id){mId= id;}

    public UUID getId() {
        return mId;
    }
    public void setId(UUID id) {
        this.mId = id;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }

    public String getSaved() {
        return mSaved;
    }

    public void setSaved(String saved) {
        mSaved = saved;
    }

    public String getCommentsNum() {
        return commentsNum;
    }

    public void setCommentsNum(String commentsNum) {
        this.commentsNum = commentsNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getTags() {
        return mTags;
    }

    public void setTags(String tags) {
        mTags = tags;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(String thumbnail) {
        mThumbnail = thumbnail;
    }
}
