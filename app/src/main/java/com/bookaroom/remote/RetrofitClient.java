package com.bookaroom.remote;

import com.bookaroom.remote.client.SelfSignCertHttpClient;
import com.bookaroom.utils.Constants;
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


    public static Retrofit getClient(String url){
        if(retrofit == null) {

            List<Interceptor> interceptors = new ArrayList<>();
            //interceptors.add(getHttpLoggingInterceptor());
            interceptors.add(getHeaderAuthorizationInterceptor());

            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(SelfSignCertHttpClient.getSelfSignOkHttpClient(interceptors))
                    .build();
        }

        return retrofit;
    }

    private static Interceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    private static Interceptor getHeaderAuthorizationInterceptor() {
        return new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {

                    okhttp3.Request request = chain.request();

                    String authenticationToken = SessionManager.getAuthenticationToken();
                    if (authenticationToken != null) {

                        Headers headers =
                                request.headers().newBuilder().add(Constants.AUTHORIZATION_HEADER,
                                                                   authenticationToken).build();
                        request = request.newBuilder().headers(headers).build();
                    }
                    return chain.proceed(request);
                }
            };
    }
}
