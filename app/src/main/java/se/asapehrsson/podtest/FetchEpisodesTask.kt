package se.asapehrsson.podtest

import android.util.Log

import com.google.gson.Gson

import java.io.IOException

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import se.asapehrsson.podtest.data.Episodes

class FetchEpisodesTask(var result: Result, var request: Request) : Runnable {
    private val gson = Gson()
    private val httpClient: OkHttpClient

    init {
        httpClient = HttpClient.instance()
    }

    override fun run() {

        Log.d(TAG, "Requesting: " + request.toString())

        httpClient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "Failed, got exception. " + e.toString())
                result.onFailed()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        var body = if (response.body() == null) "" else response.body()!!.string()

                        val episodes = gson.fromJson(body, Episodes::class.java)

                        Log.d(TAG, "Successful reply. " + episodes.toString())
                        result.onSuccess(episodes)
                    } else {
                        Log.d(TAG, "Failed reply. " + response.code())
                        result.onFailed()
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "Successful reply, got exception when processing body. " + e.toString())
                    result.onFailed()
                } finally {
                    try {
                        response.body()!!.close()
                    } catch (e: Exception) {
                        //Ignore, for now
                    }

                }
            }
        })
    }

    companion object {
        private val TAG = FetchEpisodesTask::class.java.simpleName
    }

}