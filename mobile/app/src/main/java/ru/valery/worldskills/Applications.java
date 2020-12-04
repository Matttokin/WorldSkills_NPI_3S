package ru.valery.worldskills;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.valery.worldskills.ApiModule.ApiValue;
import ru.valery.worldskills.ApiModule.ServerApi;

public class Applications extends Application {

    private static ServerApi umoriliApi;
    private static Retrofit retrofit;
    private static SharedPreferences mSettings;

    @Override
    public void onCreate() {
        super.onCreate();
        mSettings = getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE);

        //Получаем настройки сервера
        String server = "https://bd508361b787.ngrok.io";
        if (mSettings.contains("server")) {
            server = mSettings.getString("server", "https://bd508361b787.ngrok.io");
        }

        //Инициализируем библиотеку для взаимодействия с сервером
        setNuewRetro(server);


    }

    //По запросу возвращаем методы Api
    public static ServerApi getApi() {
        return umoriliApi;
    }

    //Настраиваем библиотеку для доступа к серверу
    public static void setNuewRetro(String server) {
        server = server + "/api/";
        retrofit = new Retrofit.Builder()
                .baseUrl(server)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        umoriliApi = retrofit.create(ServerApi.class);
    }
}
