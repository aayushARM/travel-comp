package com.android.aayush.travelcompaniondemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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

import javax.net.ssl.HttpsURLConnection;

public class MainDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        PlaceSelectionListener, ActivityCompat.OnRequestPermissionsResultCallback {

    static LatLng currLatLng;
    static GoogleMap googleMap;
    static Marker currLocMarker;
    static MarkerOptions[] nearbyMarkerOptions;
    static MarkerOptions placeSrchMarkerOptions;
    static MarkerOptions currLocMarkerOptions;
    static ProgressDialog pdFetchResult;
    Location currLoc;
    LocationManager locManager;
    LocationListener locListener;
    SharedPreferences sPref;
    String mapType;
    Intent placeSrchIntent;
    LatLngBounds llBounds;
    ProgressDialog pdCurrLoc;
    //Check if the latest button clicked was "Search by name" or not, to be used in FullScreenMapsActivity.
    // 0=no button clicked yet(initial condition)
    // 1=latest button clicked was "Search by name"
    // 2=latest button clicked was not "Search by name"
    static int SEARCH_BY_NAME_CLICKED=0;
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
        //Check permissions...

        if ((ContextCompat.checkSelfPermission(this,"android.permission.ACCESS_COARSE_LOCATION")
                != PackageManager.PERMISSION_GRANTED)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{"android.permission.ACCESS_COARSE_LOCATION"},
                        100);
            }

        sPref= PreferenceManager.getDefaultSharedPreferences(this);
        mapType=sPref.getString("MAP_TYPE","MAP_TYPE_NORMAL");

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.dashboard_map);
        mapFragment.getMapAsync(this);

        pdFetchResult=new ProgressDialog(MainDrawerActivity.this);
        pdFetchResult.setIndeterminate(true);
        pdFetchResult.setMessage("Fething locations...");
        pdFetchResult.setCancelable(false);

        llBounds=new LatLngBounds(new LatLng(22.842937, 72.381635),new LatLng(23.135046, 72.679706));
        try {
            placeSrchIntent = new PlaceAutocomplete.IntentBuilder
                    (PlaceAutocomplete.MODE_OVERLAY)
                    .setBoundsBias(llBounds)
            .build(MainDrawerActivity.this);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

        locManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //Set location listener to get location updates
        locListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currLatLng =new LatLng(location.getLatitude(),location.getLongitude());
                if (currLocMarker != null) {
                    currLocMarker.remove();
                }
                currLocMarkerOptions=new MarkerOptions().position(currLatLng).title("Your Location");
                currLocMarker=googleMap.addMarker(currLocMarkerOptions);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLatLng,14));

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
                //ToDo: Fill this method !
            }

        };

        try {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 2, locListener);
            if(locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            Log.e("SDADAD","True");
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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(requestCode==1) {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //Continue normal execution...
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Couldn't find location permissions!")
                            .setMessage("Please grant this app location permissions in order to proceed...");

                    builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ActivityCompat.requestPermissions(MainDrawerActivity.this,
                                    new String[]{"android.permission.ACCESS_COARSE_LOCATION"},
                                    100);
                        }
                    });

                    builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.exit(0);
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
            }
        }
        return;
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
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Just the body for future use. Does nothing right now.
        int id = item.getItemId();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }


//Override onMapReady method of OnMapReadyCallback interface.
    @Override
    public void onMapReady(GoogleMap Map) {

        googleMap=Map;
        try{
            googleMap.setMyLocationEnabled(true);
            //Toast.makeText(this,"MAP Ready!",Toast.LENGTH_SHORT).show();
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }

        changeMapType(mapType);

        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setPadding(0,0,0,100);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                pdCurrLoc=new ProgressDialog(MainDrawerActivity.this);
                pdCurrLoc.setIndeterminate(true);
                pdCurrLoc.setMessage("Finding your current location, please wait...");
                pdCurrLoc.setCancelable(false);
                pdCurrLoc.show();
                new GetInitialLocationTask().execute();
            }
        });

    }

    public void onSearchClicked(View v){
            startActivityForResult(placeSrchIntent, 1);
    }

    @Override
    public void onPlaceSelected(Place place) {
        // Get info about the selected place.
        SEARCH_BY_NAME_CLICKED=1;
        LatLng tempLL=place.getLatLng();
        googleMap.clear();
        currLocMarkerOptions=new MarkerOptions().position(currLatLng).title("Your Location");
        currLocMarker=googleMap.addMarker(currLocMarkerOptions);
        placeSrchMarkerOptions=new MarkerOptions().position(tempLL)
                .title(place.getName().toString())
                .snippet(place.getAddress().toString());
        googleMap.addMarker(placeSrchMarkerOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tempLL,14));
    }

    @Override
    public void onError(Status status) {
        Log.e("SEARCH_TAG", "An error occurred: " + status);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                this.onPlaceSelected(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                this.onError(status);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Method invoked when "Find nearby" is clicked.
    public void onNearbyClicked(View view)
    {
        String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                "json?location="+ currLatLng.latitude+","+ currLatLng.longitude+
                "&radius=1000&sensor=true"+
                "&types=food|museum|cafe|stadium|beauty_salon|restaurant" +
                "|movie_theater|park|shopping_mall|hindu_temple|night_club|spa"+
                "&key=AIzaSyDRaVoNhiI_4-G_vmk8QGY9Dn4_vx95nV0";
        pdFetchResult.show();
        new NearbySearchTask().execute(placesSearchStr);
    }

    //Method invoked when "Find by category" is clicked.
    public void onCategoriesClicked(View view){
        startActivity(new Intent(this,CategoryActivity.class));
    }

    //Method invoked when a particular category is selected.
    public static void onCategoryCardItemClicked(String placesSearchStr){
        pdFetchResult.show();
        new NearbySearchTask().execute(placesSearchStr);
    }

    public void onFullscreenClicked(View v){
        startActivity(new Intent(this,FullscreenMapsActivity.class));
    }

static class NearbySearchTask extends AsyncTask<String,Void,Void>
    {
        @Override
        protected Void doInBackground(String... strings) {
            SEARCH_BY_NAME_CLICKED=2;
            StringBuilder placesBuilder = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection;
            InputStream inputStream;
            BufferedReader reader;
            LatLng placeLL;
            String name;
            String vicinity;
            String iconUrl;
            Bitmap tmpIc,icon;

            //Get the JSON response of placesSearchStr
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

                JSONObject resultObject = new JSONObject(placesBuilder.toString());
                JSONArray placesArray = resultObject.getJSONArray("results");
                nearbyMarkerOptions =new MarkerOptions[placesArray.length()];

                //Generate marker attribute corresponding to each place in JSON response
                for (int p=0; p<placesArray.length(); p++) {
                    MarkerOptions m=new MarkerOptions();
                    JSONObject arrayObj=placesArray.getJSONObject(p);
                    JSONObject locationObj=arrayObj.getJSONObject("geometry").getJSONObject("location");
                    placeLL=new LatLng(Double.valueOf(locationObj.getString("lat")),Double.valueOf(locationObj.getString("lng")));
                    iconUrl=placesArray.getJSONObject(p).getString("icon");

                    url=new URL(iconUrl);
                    urlConnection=(HttpsURLConnection)url.openConnection();
                    urlConnection.setDoInput(true);
                    urlConnection.connect();
                    InputStream ipStream=urlConnection.getInputStream();
                    tmpIc=BitmapFactory.decodeStream(ipStream);
                    icon=Bitmap.createScaledBitmap(tmpIc,50,50,true);

                    m.position(placeLL);
                    if(!arrayObj.isNull("vicinity"))
                    {
                        vicinity=arrayObj.getString("vicinity");
                        m.snippet(vicinity);
                    }
                    if(!arrayObj.isNull("name"))
                    {
                        name=arrayObj.getString("name");
                        m.title(name);
                    }

                    if(!arrayObj.isNull("icon"))
                    {
                        m.icon(BitmapDescriptorFactory.fromBitmap(icon));
                    }
                    nearbyMarkerOptions[p]=m;
                }

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
            }

        @Override
        protected void onPostExecute(Void v) {
            googleMap.clear();
            currLocMarkerOptions=new MarkerOptions().position(currLatLng).title("Your Location");
            currLocMarker=googleMap.addMarker(currLocMarkerOptions);
            //Add markers with attributes set above.
            for(int p = 0; p< nearbyMarkerOptions.length; p++)
            {
                googleMap.addMarker(nearbyMarkerOptions[p]);
            }
            pdFetchResult.dismiss();
        }
        }

    class GetInitialLocationTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            currLoc=null;
            long k=0;
            try {
                while(currLoc==null) {
                    currLoc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    k++;
                }
            }
            catch(SecurityException e){
                e.printStackTrace();
            }
            Log.e("LOCATION_SEARCH","Loop ran "+k+" times");
            currLatLng =new LatLng(currLoc.getLatitude(),currLoc.getLongitude());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pdCurrLoc.dismiss();
            currLocMarkerOptions=new MarkerOptions().position(currLatLng).title("Your Location");
            currLocMarker=googleMap.addMarker(currLocMarkerOptions);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLatLng,14));
        }
    }
}

