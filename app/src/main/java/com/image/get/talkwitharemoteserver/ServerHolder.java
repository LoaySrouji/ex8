package com.image.get.talkwitharemoteserver;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerHolder {

    private static ServerHolder instance = null;
    private static final String URL = "http://hujipostpc2019.pythonanywhere.com";
    public final MainActivity.MyServer serverInterface;

    public synchronized static ServerHolder getInstance(){
        if(instance != null)
            return instance;

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder().client(okHttpClient)
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MainActivity.MyServer serverInterface = retrofit.create(MainActivity.MyServer.class);
        instance = new ServerHolder(serverInterface);
        return instance;
    }

    private ServerHolder(MainActivity.MyServer serverInterface) {
        this.serverInterface = serverInterface;
    }
}

