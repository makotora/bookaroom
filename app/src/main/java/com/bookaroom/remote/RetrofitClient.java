package com.bookaroom.remote;

import android.app.Activity;
import android.content.Context;

import com.bookaroom.remote.client.SelfSignCertHttpClient;
import com.bookaroom.utils.Constants;
import com.bookaroom.utils.InterceptorFactory;
import com.bookaroom.utils.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;


    public static Retrofit getClient(Context context, String url){
        if(retrofit == null) {

            List<Interceptor> interceptors = new ArrayList<>();
            //interceptors.add(InterceptorFactory.getHttpLoggingInterceptor());
            interceptors.add(InterceptorFactory.getHeaderAuthorizationInterceptor(context));

            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(SelfSignCertHttpClient.getSelfSignOkHttpClient(interceptors))
                    .build();
        }

        return retrofit;
    }


}
