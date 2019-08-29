package com.darryncampbell.gservicescheck;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Detecting the presence of Google Services
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        //  https://developers.google.com/android/reference/com/google/android/gms/common/GoogleApiAvailability.html#isGooglePlayServicesAvailable(android.content.Context)
        if (result == ConnectionResult.SUCCESS)
        {
            setGmsServicesAvailable("Yes");
            CheckLocationServices();
        }
        else if (result == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED ||
                result == ConnectionResult.SERVICE_UPDATING)
            setGmsServicesAvailable("Yes but requires user action to update");
        else
            setGmsServicesAvailable("No");

        setGmsLocationServicesAvailalbe("No, Google services are not available");

        //  Detecting whether intents will resolve prior to launching them
        final PackageManager pm = getPackageManager();

        Button btnMapIntent = findViewById(R.id.btnIntentMap);
        btnMapIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri mapUri = Uri.parse("geo:0,0?q=London");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);

                //  Check the intent will resolve
                if (mapIntent.resolveActivity(pm) != null)
                    startActivity(mapIntent);
                else
                    Toast.makeText(getApplicationContext(), "No application to handle Map intent", Toast.LENGTH_LONG).show();
            }
        });

        Button btnBrowserIntent = findViewById(R.id.btnIntentBrowser);
        btnBrowserIntent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Uri browserUri = Uri.parse("http://www.google.co.uk");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);

                //  Check the intent will resolve
                if (browserIntent.resolveActivity(pm) != null)
                    startActivity(browserIntent);
                else
                    Toast.makeText(getApplicationContext(), "No application to handle Browser intent", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void CheckLocationServices()
    {
        //  Detecting the presence of Google Location Services.  You can only run this check if
        //  Google Services themselves are present on the device
        //  https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    setGmsLocationServicesAvailalbe("Yes");
                } catch (ApiException exception)
                {
                    switch (exception.getStatusCode())
                    {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            setGmsLocationServicesAvailalbe("No, cannot be satisfied");
                            break;
                        default:
                            setGmsLocationServicesAvailalbe("No");
                            break;
                    }
                }
            }
        });
    }

    private void setGmsServicesAvailable(String text)
    {
        TextView txtGMSServicesAvailable = findViewById(R.id.txtGMSServciesAvailable);
        txtGMSServicesAvailable.setText(text);
    }

    private void setGmsLocationServicesAvailalbe(String text)
    {
        TextView txtGMSLocationServicesAvailable = findViewById(R.id.txtGmsLocationServicesAvailable);
        txtGMSLocationServicesAvailable.setText(text);
    }
}
