package com.jackj.recipegifs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jackj on 30/12/2016.
 */
public class TabFragment extends Fragment implements View.OnClickListener{

    RelativeLayout breakfast;
    RelativeLayout dinner;
    RelativeLayout dessert;
    RelativeLayout beverages;
    RelativeLayout sides;
    RelativeLayout western;
    RelativeLayout asian;
    RelativeLayout italian;
    RelativeLayout mexican;
    RelativeLayout indian;
    RelativeLayout poultry;
    RelativeLayout beef;
    RelativeLayout pork;
    RelativeLayout dairy;
    RelativeLayout veg;
    RelativeLayout seafood;
    List<RelativeLayout> categoryButtons;
    Map<Integer,String> categoryCodes=new HashMap<Integer,String>();
    List<String> searchQueries= new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_page, container, false);
        Log.i("1","Created");
        buttonSetUp(rootView);
        categoryCodes.put(breakfast.getId(),"1");
        categoryCodes.put(dinner.getId(),"2");
        categoryCodes.put(dessert.getId(),"3");
        categoryCodes.put(mexican.getId(),"4");
        categoryCodes.put(indian.getId(),"5");
        categoryCodes.put(italian.getId(),"6");
        categoryCodes.put(asian.getId(),"7.10");
        categoryCodes.put(western.getId(),"8");
        categoryCodes.put(beverages.getId(),"11");
        categoryCodes.put(veg.getId(),"12");
        categoryCodes.put(sides.getId(),"15");
        categoryCodes.put(seafood.getId(),"16");
        categoryCodes.put(poultry.getId(),"17");
        categoryCodes.put(beef.getId(),"18");
        categoryCodes.put(dairy.getId(),"19");
        categoryCodes.put(pork.getId(),"20");

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id= v.getId();
        RelativeLayout clickedLayout;
        for(RelativeLayout rlayout:categoryButtons){
            if(rlayout.getId()==id){
                clickedLayout=rlayout;
                String searchQuery=categoryCodes.get(rlayout.getId());
                categorySearch(searchQuery);
            }
        }
    }

    public void buttonSetUp(View view){
        List<RelativeLayout> categoryButtons= new ArrayList<>();
        breakfast= (RelativeLayout) view.findViewById(R.id.situation_rlayout_breakfast);
        dinner= (RelativeLayout) view.findViewById(R.id.situation_rlayout_dinner);
        dessert= (RelativeLayout) view.findViewById(R.id.situation_rlayout_dessert);
        beverages= (RelativeLayout) view.findViewById(R.id.situation_rlayout_beverages);
        sides= (RelativeLayout) view.findViewById(R.id.situation_rlayout_sides);

        western= (RelativeLayout) view.findViewById(R.id.nation_rlayout_western);
        asian= (RelativeLayout) view.findViewById(R.id.nation_rlayout_asian);
        italian= (RelativeLayout) view.findViewById(R.id.nation_rlayout_italian);
        mexican= (RelativeLayout) view.findViewById(R.id.nation_rlayout_mexican);
        indian= (RelativeLayout) view.findViewById(R.id.nation_rlayout_indian);

        poultry= (RelativeLayout) view.findViewById(R.id.nation_rlayout_poultry);
        beef= (RelativeLayout) view.findViewById(R.id.nation_rlayout_Beef);
        pork= (RelativeLayout) view.findViewById(R.id.nation_rlayout_pork);
        dairy= (RelativeLayout) view.findViewById(R.id.nation_rlayout_dairy);
        veg= (RelativeLayout) view.findViewById(R.id.nation_rlayout_vegetarianvegan);
        seafood= (RelativeLayout) view.findViewById(R.id.nation_rlayout_seafood);

        categoryButtons.add(breakfast);
        categoryButtons.add(dinner);
        categoryButtons.add(dessert);
        categoryButtons.add(beverages);
        categoryButtons.add(sides);
        categoryButtons.add(western);
        categoryButtons.add(asian);
        categoryButtons.add(italian);
        categoryButtons.add(mexican);
        categoryButtons.add(indian);
        categoryButtons.add(seafood);
        categoryButtons.add(veg);
        categoryButtons.add(poultry);
        categoryButtons.add(beef);
        categoryButtons.add(pork);
        categoryButtons.add(dairy);

    }
    public void categorySearch(String query){
        searchQueries.add(query);
    }
}