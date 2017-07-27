package myandroidninja.wordpress.simpliweather.Utils;

import myandroidninja.wordpress.simpliweather.Model.GetWeather;
import myandroidninja.wordpress.simpliweather.Model.Weather;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Kavitha on 25-07-2017.
 */

public interface ApiInterface {

    //"weather?lat=35&lon=139&appid=b1b15e88fa797225412429c1c50c122a1"

    @GET("weather")
    Call<GetWeather> getWeatherDetails(@Query("lat") double lat, @Query("lon") double lon, @Query("appid") String appid);

    @GET("weather")
    Call<GetWeather> getWeatherDetailsByZipCode(@Query("zip") int zip,@Query("appid") String appid);
}
