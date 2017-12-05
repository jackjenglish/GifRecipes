package com.jackj.recipegifs;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by jackj on 25/11/2016.
 */
public class RecipeFragment extends Fragment{
    private static final String RECIPE_ID_ARGUMENT_KEY = "crime_id";


    private Recipe mRecipe;

    private TextView TVdescription;
    VideoView videoView;
    MediaController mc;
    Context context;
    private String gifurl;
    LinearLayout videoLayout;
    private boolean hasDescExpanded=false;
    private boolean hasCommentExpanded=false;
    private boolean commentsCached=false;
    LinearLayout descLinLay;
    LinearLayout commentLinLay;
    LinearLayout commentHolderLinLay;
    ImageView expandCollapseImageView;
    ImageView expandCollapseCommentImageView;
    Drawable expand;
    Drawable collapse;
    Map<String,String> categoryCodes;
    View descTagDivide;
    TextView commentExpandCollapseTV;
    int commentCount;
    int stopPosition;
    ImageView favourite;
    RecipeLab recipeLab;
    ImageView loadingImage;


    public static RecipeFragment newInstance(UUID recipeId) {
        Bundle args = new Bundle();
        args.putSerializable(RECIPE_ID_ARGUMENT_KEY, recipeId);

        RecipeFragment fragment = new RecipeFragment();
        fragment.setArguments(args);
        //Log.i("fragrec","newINstance");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID recipeId = (UUID) getArguments().getSerializable(RECIPE_ID_ARGUMENT_KEY);
        mRecipe = RecipeLab.get(getActivity()).getRecipe(recipeId);
        recipeLab=RecipeLab.get(getActivity());
        categoryMapSetUp();

       // Log.i("oncraq: ","createdagian");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recipe, container, false);

        videoLayout = (LinearLayout) v.findViewById(R.id.linlay1);
        videoLayout.setBackgroundColor(getResources().getColor(R.color.background));
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
               FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        FrameLayout fl = new FrameLayout(getActivity());fl.setLayoutParams(lp);
        //fl.setBackground(getResources().getDrawable(R.drawable.blueback));


        final VideoView videoView = new VideoView(getActivity());
        videoView.setId(R.id.videoView);

        ImageView redditGifs=new ImageView(getActivity());

        //ImageView loadingImageVideo= new ImageView(getActivity());
        //loadingImageVideo.setImageDrawable(getResources().getDrawable(R.drawable.progress_animation));


        if(mRecipe.getUrl().contains("imgur")){
           String gifUrl= mRecipe.getUrl();
            gifUrl=gifUrl.replaceAll("gifv","mp4");
            //Log.i("gif",gifUrl);
            videoView.setVideoURI(Uri.parse(gifUrl));
            FrameLayout.LayoutParams flp=(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    600));
            flp.gravity = Gravity.CENTER;
            //Log.i("if","videoif");
            videoView.setLayoutParams(flp);
            videoView.setZOrderOnTop(true);
            videoView.layout(videoView.getLeft(),videoView.getTop(),300,300);
            fl.addView(videoView);
        } else if(mRecipe.getUrl().contains("redd")){
            String gifUrl= mRecipe.getUrl();

            FrameLayout.LayoutParams flp=(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    600));
            redditGifs.setLayoutParams(flp);
            //Log.i("gif2",gifUrl);

            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(redditGifs);
            Glide.with(this).load(gifUrl).placeholder(R.drawable.progress_animation).override(40, 40).into(imageViewTarget);


            fl.addView(redditGifs);
        }else{
            String gifUrl= mRecipe.getUrl();
            //Log.i("gif2",gifUrl);
            videoView.setVideoURI(Uri.parse(gifUrl));
            FrameLayout.LayoutParams flp=(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    600));
            flp.gravity = Gravity.CENTER;
            //Log.i("if","videoelse");
            videoView.setLayoutParams(flp);
            videoView.setZOrderOnTop(true);
            videoView.layout(videoView.getLeft(),videoView.getTop(),300,300);
            fl.addView(videoView);
        }
        videoLayout.addView(fl);

        fl.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(videoView.isPlaying()){
                    Log.i("vv","playing");
                    stopPosition = videoView.getCurrentPosition();
                    videoView.pause();//            RESUMING NOT WORKING, GET MP WORKING TO TRY SOLVE
                } else  {
                    Log.i("vv","paused"+stopPosition);
                    videoView.seekTo(stopPosition);
                }
            }
        });

        //MediaController mediacontroller = new MediaController(getActivity());
        //mediacontroller.setAnchorView(videoView);
        //mediacontroller.setMediaPlayer(videoView);
        //videoView.setMediaController(mediacontroller);
        //videoLayout.addView(mediacontroller);

        TextView TVtitle= (TextView) v.findViewById(R.id.tv_recipe_fragment_title);
        TVtitle.setText(mRecipe.getTitle());

        favourite=(ImageView)v.findViewById(R.id.imageview_recipe_fragment_favourite);
        final Drawable unsaved= getActivity().getResources().getDrawable(R.drawable.gray_heart);
        final Drawable saved= getActivity().getResources().getDrawable(R.drawable.gray_heart_filled);

        if (mRecipe.getSaved().equals("1")){
            favourite.setImageDrawable(saved);

        } else{
            favourite.setImageDrawable(unsaved);

        }
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mRecipe.getSaved().equals("1")){

                    recipeLab.changeSavedStatus("0",mRecipe.getId());
                    mRecipe.setSaved("0");
                    favourite.setImageDrawable(unsaved);

                } else{
                    recipeLab.changeSavedStatus("1",mRecipe.getId());
                    mRecipe.setSaved("1");
                    favourite.setImageDrawable(saved);
                    Toast.makeText(getActivity(), "Saved!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        descLinLay=(LinearLayout)v.findViewById(R.id.linlay_desc_section);
        TVdescription= (TextView) v.findViewById(R.id.tv_recipe_fragment_description);
        TVdescription.setText(mRecipe.getDescription());
        descLinLay.removeView(TVdescription);

        LinearLayout expandCollapseDesc=(LinearLayout)v.findViewById(R.id.rLayout_recipe_fragment_description_expand_collapse);
        expand= this.getResources().getDrawable(R.drawable.ic_expand_more_white_36dp);
        expand.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.iconColordark), PorterDuff.Mode.MULTIPLY));
        collapse= this.getResources().getDrawable(R.drawable.ic_expand_less_white_36dp);
        collapse.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.iconColordark), PorterDuff.Mode.MULTIPLY));
        expandCollapseImageView= (ImageView)v.findViewById(R.id.imageview_recipe_fragment_description_expand_collapse);
        expandCollapseImageView.setImageDrawable(expand);

        descTagDivide= new View(getActivity());
        LinearLayout.LayoutParams divider =(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                3));
        descTagDivide.setLayoutParams(divider);
        descTagDivide.setBackgroundColor(getResources().getColor(R.color.gray));

        expandCollapseDesc.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(hasDescExpanded){
                    descLinLay.removeView(TVdescription);
                    expandCollapseImageView.setImageDrawable(expand);
                    hasDescExpanded=false;
                    //descLinLay.removeView(descTagDivide);
                } else{
                    descLinLay.addView(TVdescription);
                    expandCollapseImageView.setImageDrawable(collapse);
                    //descLinLay.addView(descTagDivide);
                    hasDescExpanded=true;
                }

            }
        });

        commentExpandCollapseTV= (TextView) v.findViewById(R.id.tv_recipe_fragment_comment_expand_collapse);
        if(mRecipe.getCommentsNum().equals("notset")){
            commentExpandCollapseTV.setText("Expand Comments");
        } else {
            commentExpandCollapseTV.setText(mRecipe.getCommentsNum()+" Comments");
        }

        commentLinLay=(LinearLayout)v.findViewById(R.id.linlay_comment_section);
        commentHolderLinLay=(LinearLayout)v.findViewById(R.id.linlay_comment_holder);

        loadingImage= new ImageView(getActivity());
        loadingImage.setImageDrawable(getResources().getDrawable(R.drawable.progress_animation));
        float scale = getResources().getDisplayMetrics().density;
        int paddingSize = (int) (4 * scale + 0.5f);
        LinearLayout.LayoutParams loadingParams = new LinearLayout.LayoutParams(
                paddingSize*8,
                paddingSize*8);
        loadingParams.gravity=Gravity.CENTER;
        loadingImage.setLayoutParams(loadingParams);
        loadingImage.setPadding(paddingSize,paddingSize,paddingSize,paddingSize*2);


        LinearLayout expandCollapseComment=(LinearLayout)v.findViewById(R.id.LLayout_recipe_fragment_comment_expand_collapse);
        expandCollapseCommentImageView= (ImageView)v.findViewById(R.id.imageview_recipe_fragment_comment_expand_collapse);
        expandCollapseCommentImageView.setImageDrawable(expand);
        expandCollapseComment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(hasCommentExpanded){
                    expandCollapseCommentImageView.setImageDrawable(expand);
                    hasCommentExpanded=false;
                    commentHolderLinLay.removeView(commentLinLay);
                } else{
                    expandCollapseCommentImageView.setImageDrawable(collapse);
                    hasCommentExpanded=true;
                    if(!commentsCached){
                        commentHolderLinLay.addView(loadingImage);
                        getComments();}
                    else{
                        commentHolderLinLay.addView(commentLinLay);
                    }

                }

            }
        });


        String tags=mRecipe.getTags();
        LinearLayout tagLinearLayout=(LinearLayout)v.findViewById(R.id.linlay_tags_fragment_recipe);
        if(tags.length()>2) {
            String[] tagslist = tags.substring(1, tags.length() - 1).split(", ");
            tagLinearLayout.removeAllViews();
            int tagCount=0;
            for (String tag : tagslist) {
                if (!tag.equals("28") && !tag.equals("14")) {
                    if(tagCount<5){
                    LinearLayout tagLLayout=new LinearLayout(getActivity());
                    scale = getResources().getDisplayMetrics().density;
                    paddingSize = (int) (4 * scale + 0.5f);
                    //int viewSize = (int) (18 * scale + 0.5f);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,0,paddingSize*2,0);
                    tagLLayout.setLayoutParams(params);
                    tagLLayout.setPadding(paddingSize*2,paddingSize,paddingSize*2,paddingSize);
                    tagLLayout.setBackground(getResources().getDrawable(R.drawable.rounded_tag_box));
                    //tagLLayout.setBackgroundColor(getResources().getColor(R.color.tagsBoxColor));
                    TextView tagText=new TextView(getActivity());
                    LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT );
                    tagText.setLayoutParams(paramsText);
                    tagText.setTextColor(getResources().getColor(R.color.tagsTextColor));
                    tagText.setText(getTagText(tag));
                    tagText.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
                    //tagText.setTypeface(null, Typeface.BOLD);
                    tagLLayout.addView(tagText);
                    tagLinearLayout.addView(tagLLayout);
                    tagCount++;}
                }
            }
        }
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        mp.start();
                    }
                });

            }
        });

        return v;
    }

    public void categoryMapSetUp(){
        categoryCodes=new HashMap<String,String>();
        categoryCodes.put("1","Breakfast");
        categoryCodes.put("2","Dinner");
        categoryCodes.put("3","Dessert");
        categoryCodes.put("4","Mexican");
        categoryCodes.put("5","Indian");
        categoryCodes.put("6","Italian");
        categoryCodes.put("7","Asian");
        categoryCodes.put("8","Western");
        categoryCodes.put("11","Beverage");
        categoryCodes.put("12","Vegan/Vegetarian");
        categoryCodes.put("15","Sides");
        categoryCodes.put("16","Seafood");
        categoryCodes.put("17","Poultry");
        categoryCodes.put("18","Beef");
        categoryCodes.put("19","Pork");
        categoryCodes.put("20","Dairy");
    }
    public String getTagText(String code){
        return categoryCodes.get(code);
    }
    private void getComments(){
        String perma = (mRecipe.getPermalink().replace("vegan","")).substring(23,29);
       new FetchItemsTask(perma).execute();
    }
    private class FetchItemsTask extends AsyncTask<Void,Void,String> {
        List<Recipe> recentRecipes = new ArrayList<>();
        String permaCode;
        public FetchItemsTask(String perma) {
            permaCode=perma;
        }
        @Override
        protected String doInBackground(Void... params) {
            try {
                String jsonString = JsonFetcher.getUrlString("http://104.236.58.149/recipes/comments/"+permaCode);
                commentCount = (jsonString.length() - jsonString.replace("author", "").length())/6;
                Log.i("count", "commentCount " + commentCount);
                return jsonString;
            } catch (IOException ioe) {
                Log.e("json", "Failed to fetch items", ioe);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            Log.i("onpost","inside");
            try{
            efficientParse(jsonString);}
            catch (JSONException je){
                Log.i("json","ECEPTIOMonpostexecute");
            } catch (IOException io){
                Log.i("IO","ECEPTIOMonposetexecute");
            }
        }

    }
    private void efficientParse(String jsonString) throws IOException, JSONException {
        commentExpandCollapseTV.setText(commentCount+" Comments");//DISPLAY COMMENTS NUMBER
        JSONObject jsonBody = new JSONObject(jsonString);
        JSONArray recipeJSONArray = jsonBody.getJSONArray("comments");

        float scale = getResources().getDisplayMetrics().density;
        int paddingSize = (int) (4 * scale + 0.5f);
        LinearLayout.LayoutParams topCommParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT );
        topCommParams.setMargins(0,0,2*paddingSize,0);

        for (int i = 0; i < recipeJSONArray.length(); i++) {
            JSONObject commentObject = recipeJSONArray.getJSONObject(i);

            List<LinearLayout> TVCommentsList = new ArrayList<>();
            JSONArray replies = (JSONArray) commentObject.get("replies");
            if(replies.length()>0){
                TVCommentsList=getReplies(commentObject,1);
            }
            LinearLayout commentLLayout= new LinearLayout(getActivity());
            commentLLayout.setOrientation(LinearLayout.VERTICAL);
            commentLLayout.setLayoutParams(topCommParams);

            TextView authorTV= new TextView(getActivity());
            authorTV.setLayoutParams(topCommParams);
            authorTV.setPadding(paddingSize*2, paddingSize * 2, 0, 0);
            authorTV.setText(commentObject.getString("author"));
            authorTV.setTextColor(getResources().getColor(R.color.tagsTextColor));
            authorTV.setTextSize(paddingSize);
            authorTV.setTypeface(Typeface.create("sans-serif-light", Typeface.BOLD));

            TextView commentBodyTV= new TextView(getActivity());
            commentBodyTV.setLayoutParams(topCommParams);
            commentBodyTV.setPadding(paddingSize*2, paddingSize * 2, 0, paddingSize * 2);
            commentBodyTV.setText(commentObject.getString("body"));
            commentBodyTV.setTextColor(getResources().getColor(R.color.tagsTextColor));
            commentBodyTV.setTextSize(paddingSize);
            commentBodyTV.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));

            commentLLayout.addView(authorTV);
            commentLLayout.addView(commentBodyTV);
            commentLinLay.addView(commentLLayout);

            for(LinearLayout ll:TVCommentsList){
                commentLinLay.addView(ll);
            }
            View topCommentDivide = new View(getActivity());
            LinearLayout.LayoutParams divider = (new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    paddingSize / 2));
            topCommentDivide.setLayoutParams(divider);
            topCommentDivide.setBackgroundColor(getResources().getColor(R.color.gray));
            commentLinLay.addView(topCommentDivide);

        }
        commentHolderLinLay.removeView(loadingImage);
        commentsCached=true;

    }

    private List<LinearLayout> getReplies(JSONObject comment,int nestLevel)throws IOException, JSONException {
        List<LinearLayout> TVCommentsList = new ArrayList<>();

        float scale = getResources().getDisplayMetrics().density;
        int paddingSize = (int) (4 * scale + 0.5f);
        LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsText.setMargins(((1+nestLevel)*paddingSize),0,((1+nestLevel)*paddingSize),0);


        JSONArray replies = (JSONArray) comment.get("replies");
        if (replies.length() > 0) {
            for (int i = 0; i < replies.length(); i++) {
                JSONObject commentObject = replies.getJSONObject(i);

                LinearLayout commentLLayout= new LinearLayout(getActivity());
                commentLLayout.setLayoutParams(paramsText);
                commentLLayout.setOrientation(LinearLayout.VERTICAL);
                commentLLayout.setBackground(getResources().getDrawable(R.drawable.left_comment_border));

                TextView authorTV= new TextView(getActivity());
                authorTV.setPadding(paddingSize, paddingSize * 2, paddingSize, 0);
                authorTV.setText(commentObject.getString("author"));
                authorTV.setTextColor(getResources().getColor(R.color.tagsTextColor));
                authorTV.setTextSize(paddingSize);
                authorTV.setTypeface(Typeface.create("sans-serif-light", Typeface.BOLD));

                TextView commentBodyTV= new TextView(getActivity());
                commentBodyTV.setPadding(paddingSize, paddingSize * 2, paddingSize, paddingSize * 2);
                commentBodyTV.setText(commentObject.getString("body"));
                commentBodyTV.setTextColor(getResources().getColor(R.color.tagsTextColor));
                commentBodyTV.setTextSize(paddingSize);
                commentBodyTV.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));

                commentLLayout.addView(authorTV);
                commentLLayout.addView(commentBodyTV);
                TVCommentsList.add(commentLLayout);

                List<LinearLayout> replyList = new ArrayList<>();
                replyList=getReplies(commentObject,nestLevel+1);
                for(LinearLayout replyLL:replyList){
                    TVCommentsList.add(replyLL);
                }
            }

        }
        return TVCommentsList;
    }
}