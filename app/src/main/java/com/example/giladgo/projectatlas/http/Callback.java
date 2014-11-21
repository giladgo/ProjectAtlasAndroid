package com.example.giladgo.projectatlas.http;

/**
 * Created by giladgo on 11/24/13.
 */
public interface Callback<Result> {
    public void error(Exception e);
    public void success(Result results);
}
