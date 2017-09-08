package se.asapehrsson.podtest;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpClient {
    private static final String TAG = HttpClient.class.getSimpleName();
    private static OkHttpClient httpClient;

    public static OkHttpClient instance() {
        if (httpClient == null) {
            httpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LoggingInterceptor())
                    .build();
        }
        return httpClient;
    }

    private static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            //long before = System.nanoTime();
            Response response = chain.proceed(request);
            //Log.d(TAG, String.format("Req %s took %.1fms%n", response.request().url(), (System.nanoTime() - before) / 1e6d));

            return response;
        }
    }

}
