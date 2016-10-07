package com.android.aayush.travelcompaniondemo;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {
    static RecyclerView recyclerView;
    GridLayoutManager glm;
    ArrayList<CategoryCard> categoryList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        recyclerView=(RecyclerView)findViewById(R.id.category_recycler);
        //recyclerView.setHasFixedSize(true);
        glm=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(glm);

        categoryList=new ArrayList<CategoryCard>();
        categoryList.add(new CategoryCard(R.drawable.icfood,"Restaurants"));
        categoryList.add(new CategoryCard(R.drawable.icmovies,"Movies"));
        categoryList.add(new CategoryCard(R.drawable.icshopping,"Shopping"));
        categoryList.add(new CategoryCard(R.drawable.icpark,"Parks"));
        categoryList.add(new CategoryCard(R.drawable.iclodging,"Hotels"));
        categoryList.add(new CategoryCard(R.drawable.ichealth,"Health Care"));
        categoryList.add(new CategoryCard(R.drawable.icart,"Art"));
        categoryList.add(new CategoryCard(R.drawable.icuniversity,"Education"));

        CategoryAdapter catAdap=new CategoryAdapter(categoryList);
        recyclerView.setAdapter(catAdap);
    }

    void getBackToMainActivity(){
        NavUtils.navigateUpFromSameTask(this);
    }


    //CategoryAdapter class to create a custom adapter, which can be set on RecyclerView of Category Activity.
   class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CardViewHolder>{

    ArrayList<CategoryCard> catList;

    CategoryAdapter(ArrayList<CategoryCard> catList){

        this.catList=catList;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_card,parent,false);
        CardViewHolder cvHolder=new CardViewHolder(v);
        v.setOnClickListener(new CardOnClickListener());//CardOnClickListener class created below this class.
        return cvHolder;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int pos) {
        holder.cardImg.setImageResource(catList.get(pos).placeIc);
        holder.cardText.setText(catList.get(pos).placeName);
    }

    @Override
    public int getItemCount() {

        return catList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        ImageView cardImg;
        TextView cardText;

        CardViewHolder(View itemView){
            super(itemView);
            cv=(CardView)itemView.findViewById(R.id.category_cardview);
            cardImg=(ImageView)itemView.findViewById(R.id.place_icon);
            cardText=(TextView)itemView.findViewById(R.id.place_name);

        }
    }
}

//CardOnClickListener for listening clicks on CardViews in RecyclerView.
     class CardOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String placesSearchStr;
            int itemPos = recyclerView.indexOfChild(v);
            Log.e("Clicked Position:",String.valueOf(itemPos));

            if(itemPos==0)
            {
                placesSearchStr="https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                        "json?location="+MainDrawerActivity.currLatLng.latitude+","+MainDrawerActivity.currLatLng.longitude+
                        "&radius=1000&sensor=true&types=food|cafe|restaurant"+
                        "&key=AIzaSyDRaVoNhiI_4-G_vmk8QGY9Dn4_vx95nV0";
                MainDrawerActivity.onCategoryCardItemClicked(placesSearchStr);
            }
            else if(itemPos==1)
            {
                placesSearchStr="https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                        "json?location="+MainDrawerActivity.currLatLng.latitude+","+MainDrawerActivity.currLatLng.longitude+
                        "&radius=1000&sensor=true&types=movie_theater"+
                        "&key=AIzaSyDRaVoNhiI_4-G_vmk8QGY9Dn4_vx95nV0";
                MainDrawerActivity.onCategoryCardItemClicked(placesSearchStr);
            }
            else if(itemPos==2)
            {
                placesSearchStr="https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                        "json?location="+MainDrawerActivity.currLatLng.latitude+","+MainDrawerActivity.currLatLng.longitude+
                        "&radius=1000&sensor=true&types=clothing_store|department_store|shopping_mall|store"+
                        "&key=AIzaSyDRaVoNhiI_4-G_vmk8QGY9Dn4_vx95nV0";
                MainDrawerActivity.onCategoryCardItemClicked(placesSearchStr);
            }
            else if(itemPos==3)
            {
                placesSearchStr="https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                        "json?location="+MainDrawerActivity.currLatLng.latitude+","+MainDrawerActivity.currLatLng.longitude+
                        "&radius=1000&sensor=true&types=amusement_park|park"+
                        "&key=AIzaSyDRaVoNhiI_4-G_vmk8QGY9Dn4_vx95nV0";
                MainDrawerActivity.onCategoryCardItemClicked(placesSearchStr);
            }
            else if(itemPos==4)
            {
                placesSearchStr="https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                        "json?location="+MainDrawerActivity.currLatLng.latitude+","+MainDrawerActivity.currLatLng.longitude+
                        "&radius=1000&sensor=true&types=lodging"+
                        "&key=AIzaSyDRaVoNhiI_4-G_vmk8QGY9Dn4_vx95nV0";
                MainDrawerActivity.onCategoryCardItemClicked(placesSearchStr);
            }
            else if(itemPos==5)
            {
                placesSearchStr="https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                        "json?location="+MainDrawerActivity.currLatLng.latitude+","+MainDrawerActivity.currLatLng.longitude+
                        "&radius=1000&sensor=true&types=hospital|doctor|physiotherapist"+
                        "&key=AIzaSyDRaVoNhiI_4-G_vmk8QGY9Dn4_vx95nV0";
                MainDrawerActivity.onCategoryCardItemClicked(placesSearchStr);
            }
            else if(itemPos==6)
            {
                placesSearchStr="https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                        "json?location="+MainDrawerActivity.currLatLng.latitude+","+MainDrawerActivity.currLatLng.longitude+
                        "&radius=1000&sensor=true&types=art_gallery|museum"+
                        "&key=AIzaSyDRaVoNhiI_4-G_vmk8QGY9Dn4_vx95nV0";
                MainDrawerActivity.onCategoryCardItemClicked(placesSearchStr);
            }
            else if(itemPos==7)
            {
                placesSearchStr="https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                        "json?location="+MainDrawerActivity.currLatLng.latitude+","+MainDrawerActivity.currLatLng.longitude+
                        "&radius=1000&sensor=true&types=school|university|library"+
                        "&key=AIzaSyDRaVoNhiI_4-G_vmk8QGY9Dn4_vx95nV0";
                MainDrawerActivity.onCategoryCardItemClicked(placesSearchStr);
            }

            getBackToMainActivity();
        }
    }
}

class CategoryCard{
    int placeIc;
    String placeName;
    CategoryCard(int placeIc,String placeName)
    {
        this.placeIc=placeIc;
        this.placeName=placeName;
    }
}
