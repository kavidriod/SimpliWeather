package myandroidninja.wordpress.simpliweather;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import myandroidninja.wordpress.simpliweather.Model.GetWeather;
import myandroidninja.wordpress.simpliweather.Model.Weather;
import myandroidninja.wordpress.simpliweather.Utils.ApiClient;
import myandroidninja.wordpress.simpliweather.Utils.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener{

    private String TAG = MainActivity.class.getSimpleName();
    ImageView weatherIv;
    TextView weatherDescTv,tempTv,pressureTv,humidityTv,windSpeedTv,windDegreeTv,locationTv;

    GoogleApiClient mGoogleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private final static int ALL_PERMISSIONS_RESULT = 1001;

    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 3600000;
    private long FASTEST_INTERVAL = 180000;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList<String> permissions = new ArrayList();

    double lat,longt;
    String APP_ID="5c9a632daa8e69676e217cbf661eb955";


Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        findViewByIds();


        //Get based on User's Current Location using  getWeather();


        findLocationDetails();



    }

    private void findLocationDetails() {

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                        ALL_PERMISSIONS_RESULT);
        }

        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();

        }
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> needpermissions) {
        ArrayList result = new ArrayList();
        for (String permission : needpermissions) {
if(!hasPermission(permission))
{
    result.add(permission);
}
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
             return  (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
            }
            return true;
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }

            return false;
        }
        return true;
    }

    private void getWeather() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Log.i(TAG,"Long -> "+longt);
        Log.i(TAG,"Lat -> "+lat);
        Log.i(TAG,"App Id -> "+APP_ID);

        Call<GetWeather> getWeatherCall = apiInterface.getWeatherDetails(lat,longt,APP_ID);
        getWeatherCall.enqueue(new Callback<GetWeather>() {
            @Override
            public void onResponse(Call<GetWeather> call, Response<GetWeather> response) {
                GetWeather getWeather = response.body();

                Log.i(TAG,"get ? "+new Gson().toJson(getWeather));

                List<Weather> weatherList = getWeather.getWeather();
                weatherDescTv.setText(weatherList.get(0).getDescription());

//Convert Temp From Kelvin to Celsius.
                double kelvin = getWeather.getMain().getTemp();
                double celsius = kelvin - 273.15F;

                float celsiuss = (float) celsius;

                tempTv.setText("Temp: " +celsiuss +"(Celsius)");
                pressureTv.setText("Pressure: " +getWeather.getMain().getPressure());
                humidityTv.setText("Humidity: " +getWeather.getMain().getHumidity());

                windSpeedTv.setText("Wind Speed: " +getWeather.getWind().getSpeed());
                windDegreeTv.setText("Wind Degree: " +getWeather.getWind().getDeg());
                locationTv.setText(getWeather.getName()+","+getWeather.getSys().getCountry());


                String weatherIcon = getWeather.getWeather().get(0).getIcon()+".png";

                String weatherIconUrl = "http://openweathermap.org/img/w/"+weatherIcon;

                Log.i(TAG," > "+weatherIconUrl);

                Glide.with(getApplicationContext())
                        .load(weatherIconUrl)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(weatherIv);
            }

            @Override
            public void onFailure(Call<GetWeather> call, Throwable t) {

            }
        });

    }



    public  void  findViewByIds()
    {
        weatherIv = (ImageView) findViewById(R.id.weatherIv);

        weatherDescTv = (TextView) findViewById(R.id.weatherDescTv);
        tempTv = (TextView) findViewById(R.id.tempTv);
        pressureTv = (TextView) findViewById(R.id.pressureTv);
        humidityTv = (TextView) findViewById(R.id.humidityTv);
        windSpeedTv = (TextView) findViewById(R.id.windSpeedTv);
        windDegreeTv = (TextView) findViewById(R.id.windDegreeTv);
        locationTv = (TextView) findViewById(R.id.locationTv);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected()");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null)
        {
            lat = location.getLatitude();
            longt = location.getLongitude();

            getWeather();
        }

        startLocationUpdates();

    }

    private void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
                mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(getApplicationContext(), "Enable Permissions", Toast.LENGTH_LONG).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended()");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null)
        {
            lat = location.getLatitude();
            longt = location.getLongitude();

            getWeather();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            Log.i(TAG, "onStart() -> mGoogleApiClient != null ");
            mGoogleApiClient.connect();
        }
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settingsmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                Intent intent  = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;

        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}






