package com.android.aayush.travelcompaniondemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    static LatLng latLng;
    static GoogleMap googleMap;
    Location currLoc;
    Criteria criteria;
    LocationManager locManager;
    LocationListener locListener;
    Marker currLocMarker;
    MarkerOptions[] markerOptions;
    SharedPreferences sPref;
    String mapType;

    String[] names={"Sign in","Settings"};
    int[] icId={R.drawable.ic_account_box,R.drawable.ic_setting_light};

    public static void changeMapType(String mType){
        if(mType.equals("MAP_TYPE_NORMAL"))
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        else if(mType.equals("MAP_TYPE_HYBRID"))
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        else if(mType.equals("MAP_TYPE_SATELLITE"))
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        else
            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        sPref= PreferenceManager.getDefaultSharedPreferences(this);
        mapType=sPref.getString("MAP_TYPE",null);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Find initial location using NETWORK_PROVIDER
        locManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        try {
            currLoc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        latLng=new LatLng(currLoc.getLatitude(),currLoc.getLongitude());

        //Set location listener to get location updates
        locListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latLng=new LatLng(location.getLatitude(),location.getLongitude());
                currLocMarker.remove();
                currLocMarker=googleMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
                Toast toast=new Toast(getApplicationContext());
                toast.setText("GPS Disabled!");
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }

        };
        try {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 2, locListener);
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }


        //Initialization of MainDrawerActivity components
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Inflate Nav Drawer listview with "name" and "icId" arrays
        ArrayList<HashMap<String,String>> aList=new ArrayList<HashMap<String,String>>();
        for(int i=0;i<icId.length;i++)
        {
            HashMap<String,String> tempMap=new HashMap<String,String>();
            tempMap.put("icon",Integer.toString(icId[i]));
            tempMap.put("name",names[i]);
            aList.add(tempMap);
        }

        String[] from={"icon","name"};
        int[] to={R.id.nav_list_image,R.id.nav_list_text};
        SimpleAdapter listAdap=new SimpleAdapter(this,aList,R.layout.nav_list_item,from,to);
        ListView navList=(ListView)navigationView.getHeaderView(0).findViewById(R.id.nav_list);
        navList.setAdapter(listAdap);
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0)
                    ;
                else if(i==1)
                    startActivity(new Intent(getBaseContext(),SettingsActivity.class));
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }



    @Override
    public void onMapReady(GoogleMap Map) {

        googleMap=Map;
        try{
            googleMap.setMyLocationEnabled(true);
            Toast.makeText(this,"MAP Ready!",Toast.LENGTH_SHORT).show();
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }

        changeMapType(mapType);

        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        currLocMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));

    }

    public void onNearbyClicked(View view)
    {
        String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                "json?location="+latLng.latitude+","+latLng.longitude+
                "&radius=1000&sensor=true"+
                "&types=food|museum|cafe|stadium|beauty_salon|restaurant" +
                "|movie_theater|park|shopping_mall|hindu_temple|night_club|spa"+
                "&key=AIzaSyDRaVoNhiI_4-G_vmk8QGY9Dn4_vx95nV0";
        NearbySearchTask nearbySearchTask=new NearbySearchTask();
        nearbySearchTask.execute(placesSearchStr);
    }

    private class NearbySearchTask extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... strings) {
            StringBuilder placesBuilder = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection;
            InputStream inputStream;
            BufferedReader reader;

            try {
                url = new URL(strings[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                inputStream = urlConnection.getInputStream();
                reader=new BufferedReader(new InputStreamReader(inputStream));
                String tempLine;
                while((tempLine=reader.readLine())!=null) {
                    placesBuilder.append(tempLine);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return placesBuilder.toString();
            }

        @Override
        protected void onPostExecute(String result) {
            googleMap.clear();
            currLocMarker=googleMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
            LatLng placeLL=null;
            String name="";
            String vicinity="";

            try{
                JSONObject resultObject = new JSONObject(result);
                JSONArray placesArray = resultObject.getJSONArray("results");
                markerOptions=new MarkerOptions[placesArray.length()];
                for (int p=0; p<placesArray.length(); p++) {
                    MarkerOptions m=new MarkerOptions();
                    JSONObject obj=placesArray.getJSONObject(p).getJSONObject("geometry").getJSONObject("location");
                    placeLL=new LatLng(Double.valueOf(obj.getString("lat")),Double.valueOf(obj.getString("lng")));
                    m.position(placeLL);
                    if(!obj.isNull("vicinity"))
                    {
                        vicinity=obj.getString("vicinity");
                        m.snippet(vicinity);
                    }
                    if(!obj.isNull("name"))
                    {
                        name=obj.getString("name");
                        m.title(name);
                    }

                    markerOptions[p]=m;
                }

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            for(int p=0;p<markerOptions.length;p++)
            {
                googleMap.addMarker(markerOptions[p]);
            }

        }
        }
}

