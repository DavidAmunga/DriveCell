package com.up.set.drivecell.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.up.set.drivecell.HomeActivity;
import com.up.set.drivecell.R;
import com.up.set.drivecell.customfont.CustomEditText;
import com.up.set.drivecell.model.Common;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.paperdb.Paper;

/**
 * Created by amush on 11-Nov-17.
 */

public class MapFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,LocationListener {
    private static final String TAG = "MapFragment";


    //Our Map
    private GoogleMap mMap;

    //To store longitude and latitude from map
    private double longitude;
    private double latitude;

    private static final int LOCATION_REQUEST = 1;

    //Google ApiClient
    private GoogleApiClient googleApiClient;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_map, container, false);


        //Initializing googleapi client
        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;


    }



    //Function to move the map
    private void moveMap() {
        //Creating a LatLng Object to store Coordinates
        LatLng latLng = new LatLng(latitude, longitude);

        //Adding marker to map
        mMap.addMarker(new MarkerOptions()
                .position(latLng) //setting position
                .draggable(true) //Making the marker draggable
                .title("Me")

        ); //Adding a title

        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }


    //Getting current location
    private void getCurrentLocation() {
        //Creating a location object
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST);

            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            //moving the map to location
            moveMap();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);


        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_REQUEST);

            return;
        }
        mMap.setMyLocationEnabled(true);
        getCurrentLocation();
    }

    @Override
    public void onConnected(Bundle bundle) {
        getCurrentLocation();

        getMarkers();

    }

    private void getMarkers() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Events");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    if(postSnapshot.hasChild("eventType"))
                    {
                        Double lat=Double.valueOf(postSnapshot.child("eventLatitude").getValue().toString());
                        Double lng=Double.valueOf(postSnapshot.child("eventLongitude").getValue().toString());
                        String type=postSnapshot.child("eventType").getValue().toString();
                        String name=postSnapshot.child("eventType").getValue().toString();

                        LatLng loc=new LatLng(lat,lng);
                        addMarkers(loc,name,type);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addMarkers(LatLng loc, String name, String type) {
        switch (type)
        {
            case "Accident":

                mMap.addMarker(new MarkerOptions()
                        .position(loc)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.caution))
                        .title(name)
                        .draggable(true));
                break;
            case "Road Block":

                mMap.addMarker(new MarkerOptions()
                        .position(loc)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.crossingguard))
                        .title(name)
                        .draggable(true));
                break;
            case "Closed Road":
                mMap.addMarker(new MarkerOptions()
                        .position(loc)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.closedroad))
                        .title(name)
                        .draggable(true));
                break;
            case "Fire":
                mMap.addMarker(new MarkerOptions()
                        .position(loc)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.fire))
                        .title(name)
                        .draggable(true));
                break;
            case "Theft":
                mMap.addMarker(new MarkerOptions()
                        .position(loc)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.theft))
                        .title(name)
                        .draggable(true));
                break;
            case "Robbery":
                mMap.addMarker(new MarkerOptions()
                        .position(loc)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.robbery))
                        .title(name)
                        .draggable(true));
                break;

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //Getting the coordinates
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //Moving the map
        moveMap();
    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();

    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();

    }



    @Override
    public void onMapLongClick(LatLng latLng) {

        //Adding a new marker to the current pressed position


        latitude = latLng.latitude;
        longitude = latLng.longitude;

        showAlertDialog(latitude,longitude);

    }
    String type="";
    private void showAlertDialog(final double latitude, final double longitude) {
        DialogPlus dialog = DialogPlus.newDialog(getContext())
                .setInAnimation(R.anim.slide_in_bottom)
                .setCancelable(true)
                .setContentHolder(new ViewHolder(R.layout.place_dialog))
                .setOutAnimation(R.anim.slide_out_bottom)
                .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                .create();

        dialog.show();
        View view=dialog.getHolderView();
        final CustomEditText eventName=(CustomEditText)view.findViewById(R.id.eventName);
        final CustomEditText eventDescription=(CustomEditText)view.findViewById(R.id.eventDescription);
        Button btnAdd=(Button)view.findViewById(R.id.btnAdd);
        final MaterialSpinner eventSpinner=(MaterialSpinner)view.findViewById(R.id.eventSpinner);
        eventSpinner.setItems("Accident","Road Block","Closed Road","Fire","Robbery","Theft");

        eventSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if(item.equals("Accident"))
                {
                    type="Accident";
                    Log.d(TAG, "onItemSelected:Accident");
                }
                else if(item.equals("Road Block"))
                {
                    type ="Road Block";
                    Log.d(TAG, "onItemSelected: Road Block");
                }
                else if(item.equals("Closed Road"))
                {
                    type="Closed Road";
                    Log.d(TAG, "onItemSelected: Closed Road");
                }
                else if(item.equals("Fire"))
                {
                    type="Fire";
                    Log.d(TAG, "onItemSelected: Fire");
                }
                else if(item.equals("Robbery"))
                {
                    type="Robbery";
                    Log.d(TAG, "onItemSelected: Robbery");
                }
                else if(item.equals("Theft"))
                {
                    type="Theft";
                    Log.d(TAG, "onItemSelected: Theft");
                }
                else
                {
                    type="Custom";
                }
            }

        });

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        final String userDisplayName=user.getDisplayName();
        Log.d(TAG, "showAlertDialog: Username"+userDisplayName);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=eventName.getText().toString();
                String desc=eventDescription.getText().toString();

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(desc) &&!TextUtils.isEmpty(name)&& !TextUtils.isEmpty(type)){
                    String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                    String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());

                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Events").push();
                    ref.child("eventName").setValue(name);
                    ref.child("eventDescription").setValue(desc);
                    ref.child("eventLatitude").setValue(latitude);
                    ref.child("eventLongitude").setValue(longitude);
                    ref.child("eventPostDate").setValue(date);
                    ref.child("eventPostTime").setValue(time);
                    ref.child("eventType").setValue(type);
                    ref.child("eventUploader").setValue(userDisplayName, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Toast.makeText(getContext(), "Event inserted", Toast.LENGTH_SHORT).show();
                        }
                    });




                }
                else
                {
                    Toast.makeText(getContext(), "Please insert all the details", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    @Override
    public void onLocationChanged(Location location) {
        if(location.hasSpeed())
        {
            Log.d(TAG, "Speedo");
            Double speed=Double.valueOf(location.getSpeed()*18/5);

            Toast.makeText(getContext(), ""+speed, Toast.LENGTH_SHORT).show();
        }

    }

}
