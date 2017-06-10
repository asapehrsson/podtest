package podtest.jiki.se.podtest;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import podtest.jiki.se.podtest.model.Episodes;

public class FetchEpisodesTask implements Runnable {
    private static final String TAG = FetchEpisodesTask.class.getSimpleName();
    private final Gson gson = new Gson();
    private OkHttpClient httpClient;
    private String url;

    private final Result result;

    interface Result {
        void onSuccess(Episodes episode);

        void onFailed();
    }

    public FetchEpisodesTask(Result result, String url) {
        this.result = result;
        this.url = url;
        httpClient = HttpClient.instance();
    }

    @Override
    public void run() {
        final Request request = new Request.Builder()
                .url(url)
                .build();

        Log.d(TAG, "Requesting: " + url);
        httpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "Failed, got exception. " + e.toString());
                result.onFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String body = response.body().string();
                        Episodes episodes = gson.fromJson(body, Episodes.class);

                        Log.d(TAG, "Successful reply. " + episodes.toString());
                        result.onSuccess(episodes);
                    } else {
                        Log.d(TAG, "Failed reply. " + response.code());
                        result.onFailed();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Successful reply, got exception when processing body. " + e.toString());
                    result.onFailed();
                } finally {
                    try {
                        response.body().close();
                    } catch (Exception e) {
                        //Ignore, for now
                    }
                }
            }
        });
    }
}