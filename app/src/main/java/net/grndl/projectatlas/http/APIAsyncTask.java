package net.grndl.projectatlas.http;

import android.os.AsyncTask;

/**
 * Created by giladgo on 11/24/13.
 */
public class APIAsyncTask<Result> extends AsyncTask<Request<?, Result>, Void, Result> {

    private Exception mError = null;
    private Callback<Result> mCallback;
    private boolean mComplete;

    protected APIAsyncTask(Callback<Result> callback) {
        mCallback = callback;
        mError = null;
    }

    protected void setError(Exception error) {
        mError = error;
    }

    @Override
    protected void onPostExecute(Result result) {
        mComplete = true;
        if (mError != null) {
            mCallback.error(mError);
        } else {
            mCallback.success(result);
        }
    }

    public void cancel() {
        if (!this.isCancelled() && !mComplete) {
            cancel(true);
        }
    }

    @Override
    protected Result doInBackground(Request<?, Result>... params) {
        try {
            return params[0].send();
        } catch (Exception ex) {
            setError(ex);
            return null;
        }
    }
}