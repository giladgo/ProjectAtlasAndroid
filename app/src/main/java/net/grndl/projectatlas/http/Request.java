package net.grndl.projectatlas.http;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by giladgo on 12/22/13.
 */
public abstract class Request<Params, Result> {

    private static final String LOG_TAG = "Request";
    private Params mParams;
    private Context mContext;
    private APIAsyncTask<Result> mTask;

    protected Request(Context context, Params params) {
        mContext = context;
        mParams = params;
    }

    protected Context getContext() {
        return mContext;
    }

    protected JsonElement doRequest(Uri uri, Header[] headers) throws IOException {
        return doRequest(uri.toString(), headers);
    }

    protected JsonElement doRequest(String uri, Header[] headers) throws IOException {
        HttpUriRequest request = new HttpGet(uri);
        if (headers != null) {
            request.setHeaders(headers);
        }
        return doRequest(request);
    }

    protected JsonElement doRequest(HttpUriRequest request) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        Log.d(LOG_TAG, request.getMethod().toUpperCase() + " " + request.getURI().toString());
        InputStreamReader streamReader = null;
        try {
            HttpResponse response = client.execute(request);
            streamReader = new InputStreamReader(response.getEntity().getContent());
            return new JsonParser().parse(streamReader);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error reading from API", e);
            throw e;
        } finally {
            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    protected abstract Result send(Params params) throws IOException;

    public void sendAsync(Callback<Result> callback) {
        mTask = new APIAsyncTask<Result>(callback);
        mTask.execute(this);
    }

    public void cancel() {
        if (mTask != null) {
            mTask.cancel();
        }
    }

    public Result send() throws IOException {
        return send(mParams);
    }
}
