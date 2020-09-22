package com.bookaroom.remote;

import android.content.Context;

import com.bookaroom.remote.client.SelfSignCertHttpClient;
import com.bookaroom.remote.converters.QueryConverterFactory;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
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
                    .addConverterFactory(QueryConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(SelfSignCertHttpClient.getSelfSignOkHttpClient(interceptors))
                    .build();
        }

        return retrofit;
    }


}
