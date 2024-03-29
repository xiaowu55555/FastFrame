package com.bingo.fastframe.library.net;



import com.bingo.fastframe.library.app.BaseApplication;
import com.bingo.fastframe.library.net.interceptor.CacheInterceptor;
import com.bingo.fastframe.library.net.interceptor.HttpInterceptor;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpClient {

    private static final int DEFAULT_TIME = 10;
    private static HttpClient mInstance;

    /**
     * 单例模式
     */
    public static HttpClient getInstance() {
        if (mInstance == null) {
            synchronized (HttpClient.class) {
                if (mInstance == null) {
                    mInstance = new HttpClient();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化必要对象和参数
     *
     * @param url 基础baseUrl
     * @return
     */
    public Retrofit getRetrofit(String url) {
        // 初始化okhttp
        CacheInterceptor cacheInterceptor = new CacheInterceptor();
        //设置缓存路径
        File httpCacheDirectory = new File(BaseApplication.getInstance().getCacheDir(), "HttpCache");
        //设置缓存 10M
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(DEFAULT_TIME, TimeUnit.SECONDS)//设置读取超时时间
                .connectTimeout(DEFAULT_TIME, TimeUnit.SECONDS)//设置请求超时时间
                .writeTimeout(DEFAULT_TIME, TimeUnit.SECONDS)//设置写入超时时间
                .addInterceptor(new HttpInterceptor())
                .addNetworkInterceptor(cacheInterceptor)
                .addInterceptor(cacheInterceptor)
                .cache(cache)
                .retryOnConnectionFailure(true)//设置出现错误进行重新连接。
                .build();
        // 初始化Retrofit
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public ApiService getApiService() {
        return getInstance().getRetrofit(ApiService.BASE_URL).create(ApiService.class);
    }

}
