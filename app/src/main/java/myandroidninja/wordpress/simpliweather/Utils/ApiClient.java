package myandroidninja.wordpress.simpliweather.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Kavitha on 25-07-2017.
 */

public class ApiClient {

    public static  final  String BASE_URL = "http://api.openweathermap.org/data/2.5/";

    public static Retrofit retrofit = null;

    public static Retrofit getClient()
    {
        if (retrofit == null)
        {
            retrofit  = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        return  retrofit;
    }




}
