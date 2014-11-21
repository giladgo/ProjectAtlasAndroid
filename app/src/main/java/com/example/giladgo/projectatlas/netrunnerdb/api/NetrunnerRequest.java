package com.example.giladgo.projectatlas.netrunnerdb.api;

import android.content.Context;
import android.net.Uri;

import com.example.giladgo.projectatlas.http.Request;
import com.google.gson.JsonElement;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * Created by giladgo on 11/21/14.
 */
public abstract class NetrunnerRequest <Params, Result> extends Request<Params, Result> {

    protected NetrunnerRequest(Context context, Params params) {
        super(context, params);
    }

    protected JsonElement doRequest(Uri uri) throws IOException {
        return super.doRequest(uri.toString(), new Header[]{});
    }

    protected Uri.Builder getNetrunnerUriBuilder() {

        String host = "netrunnerdb.com";

        return new Uri.Builder()
                .scheme("http")
                .authority(host);
    }

}
