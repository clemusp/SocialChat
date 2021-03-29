package com.optic.socialchat.providers;

import com.optic.socialchat.models.FCMBody;
import com.optic.socialchat.models.FCMResponse;
import com.optic.socialchat.retrofit.IFCMapi;
import com.optic.socialchat.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider(){

    }

    public Call<FCMResponse> sendNotification(FCMBody body){
        return RetrofitClient.getClient(url).create(IFCMapi.class).send(body);
    }

}
