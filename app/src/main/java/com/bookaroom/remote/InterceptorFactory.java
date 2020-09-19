package com.bookaroom.remote;

import android.content.Context;

import com.bookaroom.utils.Constants;
import com.bookaroom.utils.session.SessionManager;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;

public class InterceptorFactory {
    public static Interceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    public static Interceptor getHeaderAuthorizationInterceptor(Context context) {
        return new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {

                okhttp3.Request request = chain.request();

                String authenticationToken = SessionManager.getAuthenticationToken(context);
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
