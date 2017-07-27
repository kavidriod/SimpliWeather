package myandroidninja.wordpress.simpliweather;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import java.util.List;

import myandroidninja.wordpress.simpliweather.Model.GetWeather;
import myandroidninja.wordpress.simpliweather.Model.Weather;
import myandroidninja.wordpress.simpliweather.Utils.ApiClient;
import myandroidninja.wordpress.simpliweather.Utils.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ZipCodeActivity extends AppCompatActivity {

    private String TAG = ZipCodeActivity.class.getSimpleName();
    ImageView weatherIv;
    TextView weatherDescTv,tempTv,pressureTv,humidityTv,windSpeedTv,windDegreeTv,locationTv;
    String APP_ID="5c9a632daa8e69676e217cbf661eb955";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);


        findViewByIds();

        // Get Location based on Zipcode By Calling getWeatherByPinCode()


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int zipcode = bundle.getInt("ZIPCODE");
            if (zipcode == 0)
                zipcode = 20006;

            getWeatherByPinCode(zipcode);
        }


    }




    public void getWeatherByPinCode(int zipcode) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Log.i(TAG,"zipcode -> "+zipcode);
        Log.i(TAG,"App Id -> "+APP_ID);

        Call<GetWeather> getWeatherCall = apiInterface.getWeatherDetailsByZipCode(zipcode,APP_ID);
        getWeatherCall.enqueue(new Callback<GetWeather>() {
            @Override
            public void onResponse(Call<GetWeather> call, Response<GetWeather> response) {
                GetWeather getWeather = response.body();

                Log.i(TAG,"get ? "+new Gson().toJson(getWeather));

                if (getWeather != null)
                {
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
                else
                {
                    Toast.makeText(getApplicationContext(),"Sorry City not found",Toast.LENGTH_LONG).show();

                }
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
}
