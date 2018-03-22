package com.example.user.googlepickers;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,OnMapReadyCallback,LocationListener {

    Place place;
    MapView mMapView;
    LinearLayout li_map_fragment;
    Location location;
    LocationManager locationManager;
    double lat,lon;
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    private TextView tvPlaceDetails;
    private FloatingActionButton fabPickPlace;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*********************************initailise View*/
        initViews();
          /*initailise View*******************************/

        /****************************MapView Code*/
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);
      /*MapView Code********************************/


        /*************************PlacePicker Code*/

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        fabPickPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

         /*PlacePicker Code**********************/

        /*************************************** location from GPS*/

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        if (location != null) {
            onLocationChanged(location);
        }

         /* location from GPS*********************************************/





    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fabPickPlace = (FloatingActionButton) findViewById(R.id.fab);
        tvPlaceDetails = (TextView) findViewById(R.id.placeDetails);

        mMapView = findViewById(R.id.mapView);
        li_map_fragment = findViewById(R.id.li_map_fragment);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(li_map_fragment, connectionResult.getErrorMessage() + "Check internet Connection", Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {

                place = PlacePicker.getPlace(data, this);
                StringBuilder stBuilder = new StringBuilder();
                String placename = String.format("%s", place.getName());
                String latitude1 = String.valueOf(place.getLatLng().latitude);
                String longitude1 = String.valueOf(place.getLatLng().longitude);
                String address = String.format("%s", place.getAddress());
                stBuilder.append("Name: ");
                stBuilder.append(placename);
                stBuilder.append("\n");
                stBuilder.append("Latitude: ");
                stBuilder.append(latitude1);
                stBuilder.append("\n");
                stBuilder.append("Logitude: ");
                stBuilder.append(longitude1);
                stBuilder.append("\n");
                stBuilder.append("Address: ");
                stBuilder.append(address);

                //tvPlaceDetails.setText(stBuilder.toString());
            }
        }
    }

    /*Map Override Method *******************************/
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (mMap != null) {
            mMap.clear();
            if (place == null) {
                if (location == null) {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(this, Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(27.175120, 78.042209, 1);
                        String address1 = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();

                        LatLng pune = new LatLng(27.175120, 78.042209);
                        mMap.addMarker(new MarkerOptions().position(pune).title(address1));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pune, 15));

                        tvPlaceDetails.setText(address1 + "\n" + city + "\n" + state + "\n" + country + "\n" + postalCode + "\n" + knownName);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {

                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(this, Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        String address1 = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();

                        LatLng pune = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(pune).title(address1));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pune, 15));

                        tvPlaceDetails.setText(address1 + "\n" + city + "\n" + state + "\n" + country + "\n" + postalCode + "\n" + knownName);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                    String address1 = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();

                    LatLng pune = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                    mMap.addMarker(new MarkerOptions().position(pune).title(place.getName().toString()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pune, 15));

                    tvPlaceDetails.setText(place.getName() + "\n" + address1 + "\n" + city + "\n" + state + "\n" + country + "\n" + postalCode + "\n" + knownName);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    /********************************************Map Override Method*/

    /*Activity Life Cycle Override Method*/
    @Override
    protected void onResume() {
        super.onResume();
        onMapReady(mMap);
    }


    /********************************************LocationListerner override method*/
    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    /*LocationListerner  override method********************************************/
}
