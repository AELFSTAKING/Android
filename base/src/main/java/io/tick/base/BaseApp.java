package io.tick.base;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.tencent.bugly.crashreport.CrashReport;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class BaseApp extends MultiDexApplication {

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    public SPCookieStore mSPCookieStore;

    @Override
    protected void attachBaseContext(Context base) {
        ContextProvider.attachApp(this);
        super.attachBaseContext(base);
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        initBugly();
        initFresco();
        initOkGo();
    }

    private void initBugly() {
        CrashReport.initCrashReport(getApplicationContext(), AppConfig.BUGLY_KEY,
                BuildConfig.DEBUG);
    }

    /**
     * 初始化fresco
     */
    private void initFresco() {
        Fresco.initialize(this);
    }

    public void initOkGo() {
        HttpHeaders headers = new HttpHeaders();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            //log相关
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkHttp");
            //log打印级别，决定了log显示的详细程度
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
            //log颜色级别，决定了log在控制台显示的颜色
            loggingInterceptor.setColorLevel(Level.INFO);
            //添加OkGo默认debug日志
            builder.addInterceptor(loggingInterceptor);
        }
        //超时时间设置
        builder.readTimeout(24L * 60L * 60L * 1000L, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(15000, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(15000, TimeUnit.MILLISECONDS);   //全局的连接超时时间
        mSPCookieStore = new SPCookieStore(this);
        //使用sp保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(new CookieJarImpl(mSPCookieStore));

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                String authType) throws
                                CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        // 其他统一的配置
        OkGo.getInstance().init(this)                           //必须调用初始化
                .setOkHttpClient(builder.build())               //必须设置OkHttpClient
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(0)          //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
                .addCommonHeaders(headers);              //全局公共头

    }
}
