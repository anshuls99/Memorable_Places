package com.example.anshulsharma.memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView placeList;
    static ArrayList<String> placeName=new ArrayList<>();
    static ArrayList<LatLng>locations=new ArrayList<>();
    static ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locations.add(new LatLng(0,0));
        placeList=findViewById(R.id.namePlace);

        SharedPreferences sharedPreferences=this.getSharedPreferences("package com.example.anshulsharma.memorableplaces", Context.MODE_PRIVATE);
        ArrayList<String> longitude=new ArrayList<>();
        ArrayList<String> latitudes=new ArrayList<>();

        placeName.clear();
        longitude.clear();
        latitudes.clear();
        locations.clear();

        try {
            placeName=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("placeName",ObjectSerializer.serialize(new ArrayList<String>())));
            longitude=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("longitudes",ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("latitudes",ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(placeName.size()>0 && longitude.size()>0 && latitudes.size()>0){
            if(placeName.size()==longitude.size() &&longitude.size()==latitudes.size()){
                for(int i=0;i<latitudes.size();i++)
                    locations.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitude.get(i))));
            }
        }else{
            placeName.add("Add a new place....");
            locations.add(new LatLng(0,0));
        }

        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,placeName);
        placeList.setAdapter(arrayAdapter);
        placeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("placeNumber",position);
                startActivity(intent);
            }
        });


    }
}
