package com.example.anshulsharma.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    centerOnMapLocaion(lastKnownLocation, "Your location");
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void centerOnMapLocaion(Location location,String title){

       LatLng userLocation=new LatLng(location.getLatitude(),location.getLongitude());
       mMap.clear();
       if(title!="Your Location"){
           mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
       }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,10));
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mMap.setOnMapLongClickListener(this);
        Intent intent = getIntent();
        if (intent.getIntExtra("placeNumber", 0) == 0) {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    centerOnMapLocaion(location, "Your Location");

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            if (Build.VERSION.SDK_INT < 23) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    centerOnMapLocaion(lastKnownLocation, "Your location");
                } else {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        }else {

            Location placeLocation=new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(MainActivity.locations.get(intent.getIntExtra("placeNumber",0)).latitude);
            placeLocation.setLongitude(MainActivity.locations.get(intent.getIntExtra("placeNumber",0)).longitude);

            centerOnMapLocaion(placeLocation,MainActivity.placeName.get(intent.getIntExtra("placeNumber",0)));

        }

    }

    @Override
    public void onMapLongClick(LatLng point) {


        mMap.clear();
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());

        String address="";
        try{

            List<Address> listAddresssg=geocoder.getFromLocation(point.latitude,point.longitude,1);
            if(listAddresssg!=null &&listAddresssg.size()>0){
                if(listAddresssg.get(0).getSubThoroughfare()!=null)
                    address+=listAddresssg.get(0).getSubThoroughfare()+" ";
                if(listAddresssg.get(0).getThoroughfare()!=null)
                    address+=listAddresssg.get(0).getThoroughfare()+" ";
                if(listAddresssg.get(0).getLocality()!=null)
                    address+=listAddresssg.get(0).getLocality()+" ";
                if(listAddresssg.get(0).getPostalCode()!=null)
                    address+=listAddresssg.get(0).getPostalCode()+" ";
                if(listAddresssg.get(0).getCountryName()!=null)
                    address+=listAddresssg.get(0).getCountryName()+" ";
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        if(address==""){

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy-MM-dd");
            address = sdf.format(new Date());
        }
        mMap.addMarker(new MarkerOptions().position(point).title(address));

        MainActivity.placeName.add(address);
        MainActivity.locations.add(point);
        MainActivity.arrayAdapter.notifyDataSetChanged();

        SharedPreferences sharedPreferences=this.getSharedPreferences("package com.example.anshulsharma.memorableplaces", Context.MODE_PRIVATE);

        ArrayList<String>locations=new ArrayList<>();
        locations.add(address);
        try {
            ArrayList<String>latitudes=new ArrayList<>();
            ArrayList<String>longitudes=new ArrayList<>();

            for(LatLng coordinates:MainActivity.locations){
                latitudes.add(Double.toString(coordinates.latitude));
                longitudes.add(Double.toString(coordinates.longitude));
            }
            sharedPreferences.edit().putString("placeName",ObjectSerializer.serialize(MainActivity.placeName)).apply();
            sharedPreferences.edit().putString("latitudes",ObjectSerializer.serialize(latitudes)).apply();
            sharedPreferences.edit().putString("longitudes",ObjectSerializer.serialize(longitudes)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Location Saved", Toast.LENGTH_SHORT).show();
    }
}
