package com.shamim.locationtrac;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.shamim.locationtrac.databinding.ActivityMainBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Intent service,comPareIntent;
    ActivityResultLauncher<String> backgroundLocation;
     ActivityResultLauncher<String[]> locationPermissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
              binding = ActivityMainBinding.inflate(getLayoutInflater());
             setContentView(binding.getRoot());

               backgroundLocation = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        // Handle permission granted
                        checkPermissions();
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        checkPermissions();
                    }
                }
        );

           locationPermissions = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    if (Boolean.TRUE.equals(permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false))) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            if (ActivityCompat.checkSelfPermission(
                                    this,
                                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED) {
                                backgroundLocation.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                            }
                        }
                    } else if (Boolean.TRUE.equals(permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false))) {
                        // Handle ACCESS_FINE_LOCATION permission granted

                        startService(service);
                    }
                }
        );


        service = new Intent(this, LocationService.class);
        comPareIntent= new Intent(this,CompareLocationActivity.class);

        binding.btnStartLocationTracking.setOnClickListener(view -> checkPermissions());

        binding.btnRemoveLocationTracking.setOnClickListener(view -> stopService(service));

        binding.btnCompereLocationTracking.setOnClickListener(view ->startActivity(comPareIntent));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {

                locationPermissions.launch(new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                });
            } else {
                startService(service);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(service);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe
    public void receiveLocationEvent(LocationEvent locationEvent) {
        binding.tvLatitude.setText("Latitude -> " + locationEvent.getLatitude());
        binding.tvLongitude.setText("Longitude -> " + locationEvent.getLongitude());
    }

}
