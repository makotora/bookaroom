package com.bookaroom.remote.converters;

import androidx.annotation.Nullable;

import com.bookaroom.utils.Constants;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Converter;
import retrofit2.Retrofit;

public class QueryConverterFactory extends Converter.Factory {
    public static QueryConverterFactory create() {
        return new QueryConverterFactory();
    }

    private QueryConverterFactory() {
    }

    @Nullable
    @Override
    public Converter<?, String> stringConverter(
            Type type,
            Annotation[] annotations,
            Retrofit retrofit) {
        if (type == Date.class) {
            return DateQueryConverter.INSTANCE;
        }
        return null;
    }

    private static final class DateQueryConverter implements Converter<Date, String> {
        static final DateQueryConverter INSTANCE = new DateQueryConverter();

        private static final ThreadLocal<DateFormat> DF = new ThreadLocal<DateFormat>() {
            @Override
            public DateFormat initialValue() {
                return new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT);
            }
        };

        @Override
        public String convert(Date date) {
            return DF.get().format(date);
        }
    }
}