package com.example.contacts;

import android.app.Application;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import java.util.List;

public class ApplicationClass extends Application {

    public static final String APPLICATION_ID = "418C0D42-F553-8355-FF6C-69E7017CE500";
    public static final String API_KEY = "73B0AF47-F6C1-406B-A3DC-5C62AA7E405E";
    public static final String SERVER_URL = "https://api.backendless.com";

    public static BackendlessUser user;
    public static List<Contact> contacts;

    @Override
    public void onCreate() {
        super.onCreate();

        Backendless.setUrl( SERVER_URL );
        Backendless.initApp( getApplicationContext(), APPLICATION_ID, API_KEY );

    }
}

