package com.changren.android.launcher.database.network;

import android.content.Context;
import android.text.TextUtils;

import com.changren.android.launcher.database.network.api.ApiService;
import com.changren.android.launcher.database.network.converter.CustomGsonConverterFactory;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Author: wangsy
 * Create: 2018-11-22 13:46
 * Description: Retrofit对象不需要重复创建，即可以对Retrofit进行封装
 */
public class RetrofitClient {

//    private static final int DEFAULT_TIMEOUT = 20;
//    private static final String BASE_URL = "https://test.changrentech.com/";

    private static RetrofitClient mInstance;
    private Retrofit retrofit;
//    private Cache cache = null;
//    private File httpCacheDirectory;

//    public static RetrofitClient getInstance(Context context, String url) {
//        return new RetrofitClient(context, url);
//    }

    public static RetrofitClient getInstance() {
        if (mInstance == null) {
            synchronized (RetrofitClient.class) {
                if (mInstance == null) {
                    mInstance = new RetrofitClient();
                }
            }
        }
        return mInstance;
    }

    public static RetrofitClient getInstance(Context context, String url) {
        if (mInstance == null) {
            synchronized (RetrofitClient.class) {
                if (mInstance == null) {
                    if (TextUtils.isEmpty(url)) {
                        url = HttpConfig.BASE_URL;
                    }
                    mInstance = new RetrofitClient(context, url);
                }
            }
        }
        return mInstance;
    }

    private RetrofitClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AddCommonParamsInterceptor())
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(HttpConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(HttpConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(HttpConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                .build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                //添加Gson解析
//                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(CustomGsonConverterFactory.create())
                //添加rxjava2
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(HttpConfig.BASE_URL)
                .build();
    }

    private RetrofitClient(Context context, String url) {

        if (TextUtils.isEmpty(url)) {
            url = HttpConfig.BASE_URL;
        }

//        if (httpCacheDirectory == null) {
//            httpCacheDirectory = new File(context.getCacheDir(), "tamic_cache");
//        }
//
//        try {
//            if (cache == null) {
//                cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
//            }
//        } catch (Exception e) {
//            LogUtils.e("OKHttp", "Could not create http cache", e);
//        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AddCommonParamsInterceptor())
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
//                .cache(cache)
                .connectTimeout(HttpConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(HttpConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(HttpConfig.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                .build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                //添加Gson解析
//                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(CustomGsonConverterFactory.create())
                //添加rxjava2
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url)
                .build();
    }

    /**
     * create you ApiService
     * Create an implementation of the API endpoints defined by the {@code service} interface.
     */
//    public <T> T create(final Class<T> service) {
//        if (service == null) {
//            throw new RuntimeException("Api service is null!");
//        }
//        return retrofit.create(service);
//    }

    private ApiService apiService;

//    public static ApiService getApiService() {
//        return getInstance().getApiService();
//    }

    public ApiService getApiService() {
        if (apiService == null) {
            synchronized (ApiService.class) {
                if (apiService == null) {
                    apiService = retrofit.create(ApiService.class);
                }
            }
        }
        return apiService;
    }

    public static <T> ObservableTransformer<T, T> applySchedulers() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
