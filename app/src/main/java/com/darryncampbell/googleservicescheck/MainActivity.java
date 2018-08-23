package com.darryncampbell.googleservicescheck;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Detecting the presence of Google Services
        GoogleApiClient mGoogleApiClient_GMS;
        setGmsServicesAvailable("Initialising");
        mGoogleApiClient_GMS = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (!mGoogleApiClient_GMS.isConnected())
            mGoogleApiClient_GMS.connect();

        setGmsLocationServicesAvailalbe("Pending");

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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setGmsServicesAvailable("Yes");
        //  Since we have GMS Services available we can run the Location Services test
        CheckLocationServices();
    }

    @Override
    public void onConnectionSuspended(int i) {
        setGmsServicesAvailable("No - Suspended");
        setGmsLocationServicesAvailalbe("No");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        String reason = "";
        if (connectionResult.getErrorMessage() != null)
            reason = " - " + connectionResult.getErrorMessage();
        setGmsServicesAvailable("No" + reason);
        setGmsLocationServicesAvailalbe("No");
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
                            setGmsLocationServicesAvailalbe("No, requires user action");
                            break;
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
