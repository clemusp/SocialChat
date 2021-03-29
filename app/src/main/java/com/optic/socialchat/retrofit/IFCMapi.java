package com.optic.socialchat.retrofit;

import com.optic.socialchat.models.FCMBody;
import com.optic.socialchat.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMapi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAsJy2CoQ:APA91bHoVHIJgMMXjwjviSgMhkS980HjuO79RzG6cuE9IdjP-DV0pSPM-Zr6VuI68O0SMqQOF2vMHW-_JxhbeT1euMdveQDK2R9pIeYJSeF9YhoOxWUmH5EGBGorHaCe0sxW1jtlM7GT"
    })

    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);

}
