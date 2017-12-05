package com.jackj.recipegifs;


/**
 * Created by jackj on 08/12/2016.
 */
public class RecipeDbSchema {
    public static final class RecipeTable{
        public static final String NAME="recipes";
        public static final class Cols{
            public static final String UUID ="uuid";
            public static final String TITLE ="title";
            public static final String LINK ="link";
            public static final String DESCRIPTION ="description";
            public static final String PERMALINK="permalink";
            public static final String THUMBNAIL="thumbnail";
            public static final String TAGS="tags";
            public static final String SAVED="saved";
            public static final String COMMENTS="comments";
        }
    }
}
