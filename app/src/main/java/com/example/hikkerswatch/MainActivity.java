package com.example.hikkerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private LocationListener locationListener;




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startListening();
            }
        }
    }

    public void startListening() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

    }


    public void updateLocationInfo(Location location) {
//        Log.i("LocationInfo",location.toString());
        TextView textLongitude = findViewById(R.id.textLatitude);
        TextView textLatitude = findViewById(R.id.textLongitude);
        TextView textAccuracy = findViewById(R.id.textAccuracy);
        TextView textAltitude = findViewById(R.id.textAltitude);
        TextView textAddress = findViewById(R.id.textAddress);



        textLongitude.setText("Longitude: " + String.valueOf(location.getLongitude()));
        textLatitude.setText("Latitude: " + String.valueOf(location.getLatitude()));

        textAltitude.setText("Altitude: " + String.valueOf(location.getAltitude()));
        textAccuracy.setText("Accuracy: " + String.valueOf(location.getAccuracy()));

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            String strAddress = "Could not find address";

            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if(listAddresses != null && listAddresses.size() > 0) {
                Log.i("Place Info: ", listAddresses.get(0).toString());

                Address address = listAddresses.get(0);
                strAddress = "";
                strAddress += address.getAddressLine(0) != null ? address.getAddressLine(0) + "\n" : "";

                strAddress += address.getSubThoroughfare() != null ? address.getSubThoroughfare() + "\n" : "";
                strAddress += address.getLocality() != null ? address.getLocality() + "\n" : "";
                strAddress += address.getPostalCode() != null ? address.getPostalCode() + "\n" : "";
                strAddress += address.getCountryName() != null ? address.getCountryName() + "\n" : "";

                textAddress.setText(strAddress);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }




    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startListening();
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
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

        if(Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);

        } else {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(location != null) {
                    updateLocationInfo(location);
                }
            }
        }
    }
}
