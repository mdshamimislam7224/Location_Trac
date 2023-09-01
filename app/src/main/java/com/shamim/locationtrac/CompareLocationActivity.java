package com.shamim.locationtrac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.greenrobot.eventbus.EventBus;

public class CompareLocationActivity extends AppCompatActivity {
    private Button btnGetCurrentLocation;
    private Button btnCheckLocationArea;
    private TextView tvResult;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private Location specifiedLocation;
    Location currentLocation ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_location);
        //requestLocationUpdates();

        btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);
        btnCheckLocationArea = findViewById(R.id.btnCheckLocationArea);
        tvResult = findViewById(R.id.tvResult);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Define the specified location area (for demonstration purposes)
        specifiedLocation = new Location("");
        specifiedLocation.setLatitude(23.8000951);
        specifiedLocation.setLongitude(90.4296833);

        btnGetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocationUpdates();
            }
        });

        btnCheckLocationArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocationUpdates();
                checkLocationArea();
            }
        });
    }

    private void requestLocationUpdates() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(500); // 10 seconds

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                     currentLocation = locationResult.getLastLocation();

                    tvResult.setText("Current Location: " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
                }
            }, null);
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void checkLocationArea() {
        if (locationRequest == null) {
            Toast.makeText(this, "Please get the current location first.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (specifiedLocation == null) {
            Toast.makeText(this, "Please specify the target location first.", Toast.LENGTH_SHORT).show();
            return;
        }

        float[] results = new float[1];
        Location.distanceBetween(
                specifiedLocation.getLatitude(), specifiedLocation.getLongitude(),
                currentLocation.getLatitude(), // Replace with actual latitude
                currentLocation.getLongitude(), // Replace with actual longitude
                results);

        float distanceInMeters = results[0];

        if (distanceInMeters <= 1000) {
            tvResult.setText("Match: Inside the specified location area");
        } else {
            tvResult.setText("Not Match: Outside the specified location area");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}